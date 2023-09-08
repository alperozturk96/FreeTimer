package com.coolnexttech.freetimer

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.coolnexttech.freetimer.service.CountdownTimerService
import com.coolnexttech.freetimer.ui.theme.FreeTimerTheme
import com.coolnexttech.freetimer.view.CountDownView
import com.coolnexttech.freetimer.view.HomeView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            askNotificationPermission(this)

            var showCountDownTimer by remember { mutableStateOf(false) }
            var setCount by remember { mutableIntStateOf(0) }
            var workoutDuration by remember { mutableIntStateOf(0) }
            var restDuration by remember { mutableIntStateOf(0) }

            FreeTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    if (!showCountDownTimer) {
                        HomeView(setSetCount = {
                            setCount = it
                        }, setWorkoutDuration = {
                            workoutDuration = it
                        }, setRestDuration = {
                            restDuration = it
                        }, showCountDownTimer = {
                            if (setCount > 0 && workoutDuration > 0 && restDuration > 0) {
                                showCountDownTimer = true
                                startService(setCount, workoutDuration, restDuration)
                            } else {
                                Toast.makeText(this, "Please enter valid timer value", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        CountDownView(
                            finishTraining = {
                                stopService()
                                showCountDownTimer = false
                            }
                        )
                    }
                }
            }
        }
    }

    private fun startService(setCount: Int, workoutDuration: Int, restDuration: Int) {
        Intent(applicationContext, CountdownTimerService::class.java).also {
            it.action = CountdownTimerService.Actions.Start.toString()
            CountdownTimerService.setCount = setCount
            CountdownTimerService.workoutDuration = workoutDuration
            CountdownTimerService.restDuration = restDuration

            CountdownTimerService.initialRestDuration = restDuration
            CountdownTimerService.initialWorkoutDuration = workoutDuration
            startService(it)
        }
    }

    private fun stopService() {
        Intent(applicationContext, CountdownTimerService::class.java).also {
            it.action = CountdownTimerService.Actions.Stop.toString()
            stopService(it)
        }
    }

    private fun askNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }
}
