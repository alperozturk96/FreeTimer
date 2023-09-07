package com.coolnexttech.freetimer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.coolnexttech.freetimer.service.CountdownTimerService

class FreeTimer : Application() {
    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            CountdownTimerService.countdownTimerServiceId,
            "FreeTimer",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}