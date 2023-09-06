package com.coolnexttech.freetimer.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coolnexttech.freetimer.util.TimerWorker

@Composable
fun HomeView(
    setCount: String,
    workoutDuration: String,
    restDuration: String,
    setSetCount: (String) -> Unit,
    setWorkoutDuration: (String) -> Unit,
    setRestDuration: (String) -> Unit,
    showCountDownTimer: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        TimerInput(value = setCount, label = "Set Count", onValueChange = {
            setSetCount(it)
        })

        TimerInput(value = workoutDuration, label = "Workout Duration In Second", onValueChange = {
            setWorkoutDuration(it)
        })

        TimerInput(value = restDuration, label = "Rest Duration In Second", onValueChange = {
            setRestDuration(it)
        })

        Button(onClick = {
            if (checkTimerValue(setCount) && checkTimerValue(workoutDuration) && checkTimerValue(restDuration)) {
                showCountDownTimer()
            } else {
                Toast.makeText(context, "Please enter valid timer value", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Start Timer")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

private fun checkTimerValue(input: String): Boolean {
    return try {
        input.toInt()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

@Composable
private fun TimerInput(value: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Normal),
        maxLines = 1,
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(label) })
}