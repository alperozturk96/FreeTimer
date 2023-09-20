package com.coolnexttech.freetimer.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.coolnexttech.freetimer.model.NotificationData

class CountdownNotificationService(context: Context) {

    companion object {
        const val channelId = "CountdownTimerServiceChannelId"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val resumeAndPauseAction = PendingIntent.getBroadcast(
        context,
        2,
        Intent(context, CountdownNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )
    private val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setSilent(true)

    fun showNotification(data: NotificationData) {
        notificationBuilder.apply {
            setContentTitle(data.setCount)
            setContentText(data.timeLeft)
            setSmallIcon(data.iconId)
            addAction(data.actionIconId, data.actionTitle, resumeAndPauseAction)
        }

        notificationManager.notify(1, notificationBuilder.build())
    }

    fun cancelNotification() {
        notificationManager.cancelAll()
    }
}