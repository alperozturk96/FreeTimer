package com.coolnexttech.freetimer.service

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
        const val notificationId = 1
    }

    private val binder = LocalBinder()
    inner class LocalBinder : Binder() {
        fun getService(): CountdownTimerService = this@CountdownTimerService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.Start.toString() -> start()
            Actions.Stop.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
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