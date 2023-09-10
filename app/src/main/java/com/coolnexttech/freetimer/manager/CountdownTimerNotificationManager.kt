package com.coolnexttech.freetimer.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.coolnexttech.freetimer.R

class CountdownTimerNotificationManager(private val context: Context) {
    private val channelId = "CountdownTimerServiceChannelId"
    private val notificationName = "CountdownTimerServiceNotificationName"
    private val notificationId = 1

    private val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotification(timeLeft: String) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(timeLeft))
            .setSilent(true)
            .setSmallIcon(R.drawable.im_timer)
            .build()

        nm.notify(notificationId, notification)
    }

    fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, notificationName, importance)
        nm.createNotificationChannel(channel)
    }
}