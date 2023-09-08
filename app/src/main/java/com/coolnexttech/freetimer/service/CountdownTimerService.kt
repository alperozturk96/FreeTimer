package com.coolnexttech.freetimer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.coolnexttech.freetimer.R
import java.util.Timer
import java.util.TimerTask

class CountdownTimerService: Service() {
    companion object {
        const val countdownTimerServiceId = "countdownTimerService"
        const val channelId = "countdownTimerNotificationChannelId"
        const val notificationId = 1

        var isRestModeActive = false
        var setCount: Int = 0
        var workoutDuration: Int = 0
        var restDuration: Int = 0

        var initialWorkoutDuration: Int = 0
        var initialRestDuration: Int = 0

        var ringBell = false
        var finishTraining = false

        fun reset() {
            setCount = 0
            restDuration = 0
            workoutDuration = 0
            isRestModeActive = false
            ringBell = false
            finishTraining = false
            initialWorkoutDuration = 0
            initialRestDuration = 0
        }
    }

    private var timer = Timer()

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

    private fun tick() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                println("!!! TICK !!!")

                if (isRestModeActive) {
                    restDuration -= 1

                    if (restDuration == 0) {
                        setCount -= 1
                        isRestModeActive = false
                        workoutDuration = initialWorkoutDuration
                        restDuration = initialRestDuration
                        ringBell = true

                        if (setCount == 0) {
                            finishTraining = true
                        }
                    }
                } else {
                    workoutDuration -= 1

                    if (workoutDuration == 0) {
                        ringBell = true
                        isRestModeActive = true
                    }
                }
            }
        }, 0, 1000)
    }

    private fun start() {
        tick()

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