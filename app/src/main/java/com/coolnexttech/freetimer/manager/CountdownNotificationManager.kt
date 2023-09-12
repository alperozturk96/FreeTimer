package com.coolnexttech.freetimer.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class CountdownNotificationManager(private val context: Context) {
    private val channelId = "CountdownTimerServiceChannelId"
    private val notificationName = "CountdownTimerServiceNotificationName"
    private val notificationId = 1

    private val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val importance = NotificationManager.IMPORTANCE_DEFAULT
    private val channel = NotificationChannel(channelId, notificationName, importance)

    init {
        createNotificationChannel()
    }

    private val notification = NotificationCompat.Builder(context, channelId)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setSilent(true)

    fun updateNotification(timeLeft: String, iconId: Int) {
        notification.apply {
            setContentText(timeLeft)
            setSmallIcon(iconId)
        }
        nm.notify(notificationId, notification.build())
    }

    private fun createNotificationChannel() {
        nm.createNotificationChannel(channel)
    }

    fun deleteNotificationChannel() {
        nm.cancelAll()
        nm.deleteNotificationChannel(channelId)
    }
}