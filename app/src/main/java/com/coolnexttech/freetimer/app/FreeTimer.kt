package com.coolnexttech.freetimer.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.coolnexttech.freetimer.notification.CountdownNotificationService

class FreeTimer: Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CountdownNotificationService.channelId,
            "Countdown",
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "Used for the track countdown"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}