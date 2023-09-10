package com.coolnexttech.freetimer.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
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
    private var workoutData: WorkoutData? = null

    companion object {
        const val serviceWorkoutData = "workout_data"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val json = intent?.getStringExtra(serviceWorkoutData)
        workoutData = json?.toWorkoutData()

        when (intent?.action) {
            Actions.Start.toString() -> startService()
            Actions.Stop.toString() -> stopSelf()
        }
        return START_STICKY
    }

    private fun startService() {
        setWakeLock()
        scope.launch {
            while (true) {

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