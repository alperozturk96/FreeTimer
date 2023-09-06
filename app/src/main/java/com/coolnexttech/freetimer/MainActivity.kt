package com.coolnexttech.freetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.coolnexttech.freetimer.ui.theme.FreeTimerTheme
import com.coolnexttech.freetimer.view.CountDownView
import com.coolnexttech.freetimer.view.HomeView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showCountDownTimer by remember { mutableStateOf(false) }
            var setCount by remember { mutableStateOf("") }
            var workoutDuration by remember { mutableStateOf("") }
            var restDuration by remember { mutableStateOf("") }

            FreeTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    if (!showCountDownTimer) {
                        HomeView(setCount, workoutDuration, restDuration, setSetCount = {
                            setCount = it
                        }, setWorkoutDuration = {
                            workoutDuration = it
                        }, setRestDuration =  {
                            restDuration = it
                        }, showCountDownTimer = {
                            showCountDownTimer = true
                        })
                    } else {
                        CountDownView(setCount.toInt(), workoutDuration.toInt(), restDuration.toInt()) {
                            showCountDownTimer = false
                        }
                    }
                }
            }
        }
    }
}
