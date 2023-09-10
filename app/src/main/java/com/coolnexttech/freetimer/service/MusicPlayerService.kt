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
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.model.toWorkoutData
import com.coolnexttech.freetimer.manager.MediaPlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlayerService : Service() {
    private var mediaPlayerManager = MediaPlayerManager(this)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var workoutData: WorkoutData

    private var _initialWorkoutDuration = 0
    private var _initialRestDuration = 0

    companion object {
        const val serviceWorkoutData = "workout_data"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val json = intent?.getStringExtra(serviceWorkoutData)
        workoutData = json?.toWorkoutData() ?: return START_STICKY
        _initialWorkoutDuration = workoutData.workDuration
        _initialRestDuration = workoutData.restDuration

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
            while (true) {
                workoutData.print()

                if (workoutData.isRestModeActive) {
                    workoutData.restDuration -= 1
                    if (workoutData.isCurrentSetRestFinished()) {
                        workoutData = WorkoutData(
                            id = workoutData.id,
                            isRestModeActive = false,
                            setCount = workoutData.setCount - 1,
                            workDuration = _initialWorkoutDuration,
                            restDuration = _initialRestDuration
                        )
                    }
                    mediaPlayerManager.playAudio(R.raw.boxing_bell)
                    if (workoutData.isWorkoutFinished()) {
                        mediaPlayerManager.stopAudio()
                    }
                } else {
                    workoutData.workDuration -= 1
                    if (workoutData.isCurrentSetWorkoutFinished()) {
                        mediaPlayerManager.playAudio(R.raw.boxing_bell)
                        workoutData.isRestModeActive = true
                    }
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