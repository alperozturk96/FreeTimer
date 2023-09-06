package com.coolnexttech.freetimer

import android.annotation.SuppressLint
import android.app.Application
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.coolnexttech.freetimer.util.TimerWorker
import java.util.concurrent.TimeUnit

class FreeTimer: Application() {

    @SuppressLint("InvalidPeriodicWorkRequestInterval")
    override fun onCreate() {
        super.onCreate()

        // Create a periodic work request to run MyWorker every 1 second
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            TimerWorker::class.java,
            1,
            TimeUnit.SECONDS
        ).build()

        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }
}