package com.coolnexttech.freetimer.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.coolnexttech.freetimer.model.CountdownController

class CountdownNotificationService(private val context: Context) {

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

    fun showNotification(setCountInfo: String, timeLeftInfo: String, iconId: Int) {
        val notificationActionTitle = context.getString(CountdownController.data.value.notificationActionTitleId)

        val notification = NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentTitle(setCountInfo)
            .setContentText(timeLeftInfo)
            .setSmallIcon(iconId)
            .addAction(CountdownController.data.value.controlButtonIconId, notificationActionTitle, resumeAndPauseAction)
            .setSilent(true)
            .build()

        notificationManager.notify(1, notification)
    }

    fun cancelNotification() {
        notificationManager.cancelAll()
    }
}