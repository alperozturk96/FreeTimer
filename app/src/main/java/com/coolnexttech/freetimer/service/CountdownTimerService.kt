package com.coolnexttech.freetimer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.coolnexttech.freetimer.R

class CountdownTimerService: Service() {
    companion object {
        const val countdownTimerServiceId = "countdownTimerService"
        const val channelId = "countdownTimerNotificationChannelId"
        const val notificationId = 1
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()

        when(intent?.action) {
            Actions.Start.toString() -> start()
            Actions.Stop.toString() -> stopSelf()
        }
        return START_STICKY
    }

    private fun createChannel() {
        val notificationChannel = NotificationChannel(
            channelId,
            "FreeTimer",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun tick(second: Int, action: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
           action()
        }, second.toLong() * 1000)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, countdownTimerServiceId)
            .setSilent(true)
            .setSmallIcon(R.drawable.im_timer)
            .build()

        startForeground(notificationId, notification)
    }

    enum class Actions {
        Start, Stop
    }
}