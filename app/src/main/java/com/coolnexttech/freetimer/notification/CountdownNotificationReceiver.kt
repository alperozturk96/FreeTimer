package com.coolnexttech.freetimer.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.coolnexttech.freetimer.model.CountdownController

class CountdownNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CountdownController.toggle()
    }
}