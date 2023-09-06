package com.coolnexttech.freetimer.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TimerWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        println("Background work is running.")
        return Result.failure()
    }
}