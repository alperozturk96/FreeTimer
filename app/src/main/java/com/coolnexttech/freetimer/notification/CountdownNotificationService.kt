package com.coolnexttech.freetimer.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class CountdownNotificationService(private val context: Context) {

    companion object {
        const val channelId = "CountdownTimerServiceChannelId"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(setCount: String, timeLeft: String, iconId: Int, actionIconId: Int, actionTitle: String) {
        val resumeAndPauseAction = PendingIntent.getBroadcast(
            context,
            2,
            Intent(context, CountdownNotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentTitle(setCount)
            .setContentText(timeLeft)
            .setSmallIcon(iconId)
            .setSilent(true)
            .addAction(actionIconId, actionTitle, resumeAndPauseAction)
            .build()

        notificationManager.notify(1, notification)
    }

    fun cancelNotification() {
        notificationManager.cancelAll()
    }
}