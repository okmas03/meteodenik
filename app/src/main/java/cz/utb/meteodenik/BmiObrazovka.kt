package cz.utb.meteodenik

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun BmiObrazovka(modifier: Modifier = Modifier) {
    var hmotnost by remember { mutableStateOf("") }
    var vyska by remember { mutableStateOf("") }

    val kg = hmotnost.replace(',', '.').toDoubleOrNull()
    val cm = vyska.replace(',', '.').toDoubleOrNull()
    val bmi = if (kg != null && cm != null && cm > 0) kg / ((cm / 100) * (cm / 100)) else null

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = hmotnost,
            onValueChange = { hmotnost = it },
            label = { Text(text = stringResource(R.string.bmi_hmotnost)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        OutlinedTextField(
            value = vyska,
            onValueChange = { vyska = it },
            label = { Text(text = stringResource(R.string.bmi_vyska)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Text(
            text = if (bmi != null) stringResource(R.string.bmi_vysledek, bmi)
            else stringResource(R.string.bmi_zadejte)
        )
    }
}

