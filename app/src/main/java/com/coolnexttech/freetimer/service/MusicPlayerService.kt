package com.coolnexttech.freetimer.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import com.coolnexttech.freetimer.manager.MediaPlayerManager
import com.coolnexttech.freetimer.model.startCountDown
import com.coolnexttech.freetimer.viewmodel.CountDownViewModel.Companion.countdownData
import com.coolnexttech.freetimer.viewmodel.CountDownViewModel.Companion.initialRestDuration
import com.coolnexttech.freetimer.viewmodel.CountDownViewModel.Companion.initialWorkoutDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MusicPlayerService : Service() {
    private var mediaPlayerManager = MediaPlayerManager(this)
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("MusicPlayerService Started")

        when (intent?.action) {
            Actions.Start.toString() -> startService()
            Actions.Stop.toString() -> {
                scope.cancel()
                stopSelf()
            }
        }

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, MusicPlayerService::class.java).also {
            it.setPackage(packageName)
        }
        val restartServicePendingIntent = PendingIntent.getService(
            this, 1, restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        applicationContext.getSystemService(Context.ALARM_SERVICE)
        val alarmService =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000,
            restartServicePendingIntent
        )
    }

    private fun startService() {
        setWakeLock()

        scope.launch {
            while (!countdownData.value.isWorkoutFinished()) {
                println("MusicPlayerService Running")

                countdownData.update {
                    it.startCountDown(
                        mediaPlayerManager,
                        initialWorkoutDuration,
                        initialRestDuration
                    )
                }
                delay(1000)
            }
        }
    }

    @SuppressLint("WakelockTimeout")
    private fun setWakeLock() {
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MusicPlayerService::lock").apply {
                    acquire()
                }
            }
    }

    enum class Actions {
        Start, Stop
    }
}