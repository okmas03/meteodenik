package cz.utb.meteodenik

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.utb.meteodenik.model.UKAZKOVA_MESTA
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme

private const val TAG = "Cyklus"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity  onCreate   (savedInstanceState = $savedInstanceState)")
        enableEdgeToEdge()
        setContent {
            MeteoDenikTheme {
                var zobrazitBmi by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (zobrazitBmi) {
                        BackHandler { zobrazitBmi = false }
                        BmiObrazovka(modifier = Modifier.padding(innerPadding))
                    } else {
                        MeteoObrazovka(
                            onOtevritBmi = { zobrazitBmi = true },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity  onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity  onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity  onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MainActivity  onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "MainActivity  onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity  onDestroy  (isFinishing = $isFinishing)")
    }
}

@Composable
fun MeteoObrazovka(
    onOtevritBmi: () -> Unit,
    modifier: Modifier = Modifier
) {
    var zapsano by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val pocet = UKAZKOVA_MESTA.size

    Column(modifier = modifier.padding(16.dp)) {

        // ① titulek s plurálem
        Text(
            text = pluralStringResource(R.plurals.pocet_zaznamu, pocet, pocet),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // ② tlačítko polohy
        Button(onClick = { /* poloha přijde v další lekci */ }) {
            Text(stringResource(R.string.seznam_tlacitko_poloha))
        }

        // ③ seznam měst
        UKAZKOVA_MESTA.forEach { mesto ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(context, DetailMestaActivity::class.java)
                            .putExtra(DetailMestaActivity.EXTRA_MESTO, mesto.nazev)
                            .putExtra(DetailMestaActivity.EXTRA_TEPLOTA, mesto.teplota)
                        context.startActivity(intent)
                    }
                    .padding(vertical = 16.dp)
            ) {
                Text(mesto.nazev, style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.teplota_format, mesto.teplota))
            }
            HorizontalDivider()
        }

        if (zapsano) {
            Text(
                text = stringResource(R.string.text_po_kliknuti),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // ④ tlačítko zápisu
        Button(
            onClick = { zapsano = true },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.seznam_tlacitko_zapsat))
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Meteo deník - $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MeteoDenikTheme {
        Greeting("Android")
    }
}