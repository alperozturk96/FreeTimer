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
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.manager.MediaPlayerManager
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.model.startCountDown
import com.coolnexttech.freetimer.model.toCountdownData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlayerService : Service() {
    private var mediaPlayerManager = MediaPlayerManager(this)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var countdownData: CountdownData

    private var _initialWorkoutDuration = 0
    private var _initialRestDuration = 0

    companion object {
        const val serviceCountdownData = "countdown_data"
        var canStartService = true
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!canStartService) {
            stopSelf()
            return START_STICKY
        }

        println("MusicPlayerService Started")

        val json = intent?.getStringExtra(serviceCountdownData)
        countdownData = json?.toCountdownData() ?: return START_STICKY

        _initialWorkoutDuration = countdownData.workDuration
        _initialRestDuration = countdownData.restDuration

        when (intent.action) {
            Actions.Start.toString() -> startService()
            Actions.Stop.toString() -> stopSelf()
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
            while (!countdownData.isWorkoutFinished()) {
                println("MusicPlayerService Running")
                countdownData = countdownData.startCountDown(mediaPlayerManager, _initialWorkoutDuration, _initialRestDuration)
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