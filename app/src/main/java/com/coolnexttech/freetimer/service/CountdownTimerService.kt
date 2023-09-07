package com.coolnexttech.freetimer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.coolnexttech.freetimer.R

class CountdownTimerService: Service() {
    companion object {
        const val countdownTimerServiceId = "countdownTimerService"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.Start.toString() -> start()
            Actions.Stop.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, countdownTimerServiceId)
            .setSmallIcon(R.drawable.im_timer)
            .setContentTitle("FreeTimer")
            .setContentText("Time Left: X")
            .build()

        startForeground(1, notification)
    }

    private fun stop() {

    }

    enum class Actions {
        Start, Stop
    }
}