package cz.utb.meteodenik

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme

private const val TAG = "Cyklus"

class DetailMestaActivity : ComponentActivity() {

    companion object {
        const val EXTRA_MESTO = "cz.utb.meteodenik.extra.MESTO"
        const val EXTRA_TEPLOTA = "cz.utb.meteodenik.extra.TEPLOTA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "DetailMestaActivity  onCreate   (savedInstanceState = $savedInstanceState)")
        enableEdgeToEdge()

        val mesto = intent.getStringExtra(EXTRA_MESTO) ?: getString(R.string.mesto_nezname)
        val teplota = intent.getDoubleExtra(EXTRA_TEPLOTA, Double.NaN)

        setContent {
            MeteoDenikTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DetailObrazovka(mesto, teplota, Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onStart()   { super.onStart();   Log.d(TAG, "DetailMestaActivity  onStart") }
    override fun onResume()  { super.onResume();  Log.d(TAG, "DetailMestaActivity  onResume") }
    override fun onPause()   { super.onPause();   Log.d(TAG, "DetailMestaActivity  onPause") }
    override fun onStop()    { super.onStop();    Log.d(TAG, "DetailMestaActivity  onStop") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "DetailMestaActivity  onRestart") }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "DetailMestaActivity  onDestroy  (isFinishing = $isFinishing)")
    }
}

@Composable
fun DetailObrazovka(mesto: String, teplota: Double, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(mesto, style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.teplota_format, teplota))
    }
}