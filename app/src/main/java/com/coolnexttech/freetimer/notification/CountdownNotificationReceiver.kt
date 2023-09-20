package com.coolnexttech.freetimer.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.coolnexttech.freetimer.model.play
import kotlinx.coroutines.flow.update

class CountdownNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        play.update {
            !it
        }
    }
}