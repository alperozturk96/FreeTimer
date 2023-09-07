package com.coolnexttech.freetimer

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
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
import androidx.core.app.ActivityCompat
import com.coolnexttech.freetimer.service.CountdownTimerService
import com.coolnexttech.freetimer.ui.theme.FreeTimerTheme
import com.coolnexttech.freetimer.view.CountDownView
import com.coolnexttech.freetimer.view.HomeView

class MainActivity : ComponentActivity() {
    private lateinit var countdownTimerService: CountdownTimerService
    private var boundService = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Intent(this, CountdownTimerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        setContent {
            askNotificationPermission(this)

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
                        }, setRestDuration = {
                            restDuration = it
                        }, showCountDownTimer = {
                            showCountDownTimer = true
                            Intent(applicationContext, CountdownTimerService::class.java).also {
                                it.action = CountdownTimerService.Actions.Start.toString()
                                startService(it)
                            }
                        })
                    } else {
                        CountDownView(
                            countdownTimerService,
                            setCountValue = setCount.toInt(),
                            workoutDurationValue = workoutDuration.toInt(),
                            restDurationValue = restDuration.toInt(),
                            finishTraining = {
                                Intent(applicationContext, CountdownTimerService::class.java).also {
                                    it.action = CountdownTimerService.Actions.Stop.toString()
                                    stopService(it)
                                }
                                showCountDownTimer = false
                            }
                        )
                    }
                }
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as CountdownTimerService.LocalBinder
            countdownTimerService = binder.getService()
            boundService = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            boundService = false
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
