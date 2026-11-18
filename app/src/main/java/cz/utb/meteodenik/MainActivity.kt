package cz.utb.meteodenik

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme
import cz.utb.meteodenik.ui.MestaObrazovka
import cz.utb.meteodenik.ui.VizitkaKarta

private const val TAG = "Cyklus"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity  onCreate   (savedInstanceState = $savedInstanceState)")
        enableEdgeToEdge()
        setContent {
            MeteoDenikTheme {
                MestaObrazovka()
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