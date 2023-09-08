package com.coolnexttech.freetimer.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coolnexttech.freetimer.ui.theme.BorderColor

@Composable
fun HomeView(
    setSetCount: (Int) -> Unit,
    setWorkoutDuration: (Int) -> Unit,
    setRestDuration: (Int) -> Unit,
    showCountDownTimer: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        TimerInput(label = "Set Count", onValueChange = {
            setSetCount(it)
        })

        TimerInput(label = "Workout Duration In Second", onValueChange = {
            setWorkoutDuration(it)
        })

        TimerInput(label = "Rest Duration In Second", onValueChange = {
            setRestDuration(it)
        })

        Button(onClick = {
            showCountDownTimer()
        }) {
            Text(text = "Start Timer", color = Color.Black)
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TimerInput(
    label: String,
    onValueChange: (Int) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }

    OutlinedTextField(
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = BorderColor,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Normal),
        maxLines = 1,
        value = text,
        onValueChange = { value ->
            if (value.length <= 2) {
                val sanitizedValue = value.replace(Regex("[^0-9]"), "")
                text = sanitizedValue.filter { it.isDigit() }
                try {
                    onValueChange(sanitizedValue.toInt())
                } catch (_: Throwable) { }
            }
        },
        label = { Text(label, color = Color.Black) })
}