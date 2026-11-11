package cz.utb.meteodenik

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

private const val OPRAVNENI = Manifest.permission.ACCESS_COARSE_LOCATION

enum class StavPolohy { NEZJISTENO, ZAMITNUTO, TRVALE_ZAMITNUTO, POVOLENO }

@Composable
fun PolohaKarta(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context.najdiAktivitu()

    var stav by remember {
        mutableStateOf(if (maOpravneni(context)) StavPolohy.POVOLENO else StavPolohy.NEZJISTENO)
    }
    var zobrazVysvetleni by remember { mutableStateOf(false) }
    var poloha by remember { mutableStateOf<Location?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { povoleno ->
        // Trvalé zamítnutí poznáme až tady: po zamítnutí vrací rationale false jen tehdy,
        // když už systém dialog znovu neukáže.
        stav = when {
            povoleno -> StavPolohy.POVOLENO
            activity != null &&
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, OPRAVNENI) ->
                StavPolohy.ZAMITNUTO
            else -> StavPolohy.TRVALE_ZAMITNUTO
        }
    }

    // Návrat z Nastavení: uživatel mohl oprávnění mezitím povolit nebo odebrat.
    LifecycleResumeEffect(Unit) {
        if (maOpravneni(context)) stav = StavPolohy.POVOLENO
        else if (stav == StavPolohy.POVOLENO) stav = StavPolohy.NEZJISTENO
        onPauseOrDispose { }
    }

    LaunchedEffect(stav) {
        if (stav == StavPolohy.POVOLENO) poloha = nactiPolohu(context)
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.poloha_titulek), style = MaterialTheme.typography.titleMedium)

            when {
                stav == StavPolohy.POVOLENO -> {
                    val p = poloha
                    Text(
                        if (p != null) stringResource(R.string.poloha_souradnice, p.latitude, p.longitude)
                        else stringResource(R.string.poloha_neznama)
                    )
                    OutlinedButton(onClick = { poloha = nactiPolohu(context) }) {
                        Text(stringResource(R.string.poloha_obnovit))
                    }
                }

                stav == StavPolohy.TRVALE_ZAMITNUTO -> {
                    Text(stringResource(R.string.poloha_trvale_zamitnuto))
                    Button(onClick = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                        )
                    }) { Text(stringResource(R.string.poloha_otevrit_nastaveni)) }
                }

                zobrazVysvetleni -> {
                    Text(stringResource(R.string.poloha_vysvetleni))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            zobrazVysvetleni = false
                            launcher.launch(OPRAVNENI)
                        }) { Text(stringResource(R.string.poloha_pokracovat)) }
                        TextButton(onClick = { zobrazVysvetleni = false }) {
                            Text(stringResource(R.string.poloha_ted_ne))
                        }
                    }
                }

                else -> { // NEZJISTENO nebo ZAMITNUTO
                    if (stav == StavPolohy.ZAMITNUTO) Text(stringResource(R.string.poloha_zamitnuto))
                    Button(onClick = {
                        when {
                            maOpravneni(context) -> stav = StavPolohy.POVOLENO
                            activity != null &&
                                    ActivityCompat.shouldShowRequestPermissionRationale(activity, OPRAVNENI) ->
                                zobrazVysvetleni = true
                            else -> launcher.launch(OPRAVNENI)
                        }
                    }) { Text(stringResource(R.string.seznam_tlacitko_poloha)) }
                }
            }
        }
    }
}

private fun maOpravneni(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, OPRAVNENI) == PackageManager.PERMISSION_GRANTED

/** Před každým čtením znovu ověří oprávnění; null = bez oprávnění nebo bez fixu. */
private fun nactiPolohu(context: Context): Location? {
    if (!maOpravneni(context)) return null
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return try {
        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    } catch (e: SecurityException) {
        null
    }
}

private fun Context.najdiAktivitu(): Activity? {
    var c = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}