package com.coolnexttech.freetimer.view

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.service.CountdownTimerService
import com.coolnexttech.freetimer.util.MusicPlayer

@Composable
fun CountDownView(
    setCountValue: Int,
    workoutDurationValue: Int,
    restDurationValue: Int,
    finishTraining: () -> Unit
) {
    val context: Context = LocalContext.current
    val musicPlayer = MusicPlayer(context)

    var isRestModeActive by remember { mutableStateOf(false) }

    var setCount by remember { mutableIntStateOf(setCountValue) }
    var workoutDuration by remember { mutableIntStateOf(workoutDurationValue) }
    var restDuration by remember { mutableIntStateOf(restDurationValue) }

    BackHandler {
        musicPlayer.stopAudio()
        finishTraining()
    }

    Handler(Looper.getMainLooper()).postDelayed({
        if (isRestModeActive) {
            restDuration -= 1

            if (restDuration == 0) {
                setCount -= 1
                isRestModeActive = false
                workoutDuration = workoutDurationValue
                restDuration = restDurationValue
                musicPlayer.playAudio(R.raw.boxing_bell)

                if (setCount == 0) {
                    musicPlayer.stopAudio()
                    finishTraining()
                }
            }
        } else {
            workoutDuration -= 1

            if (workoutDuration == 0) {
                musicPlayer.playAudio(R.raw.boxing_bell)
                isRestModeActive = true
            }
        }
    }, 1000)

    val timeLeft = if (isRestModeActive) {
        "Rest: $restDuration"
    } else {
        "Time Left: $workoutDuration"
    }

    updateNotification(context, timeLeft)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        InfoText(text = "SET: $setCount")
        InfoText(text = timeLeft)
        Spacer(modifier = Modifier.weight(1f))
    }
}

private fun updateNotification(context: Context, timeLeft: String) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(
        context,
        CountdownTimerService.countdownTimerServiceId
    )
        .setSilent(true)
        .setSmallIcon(R.drawable.im_timer)
        .setContentTitle(timeLeft)
        .build()

    notificationManager.notify(CountdownTimerService.notificationId, notification)
}

@Composable
private fun InfoText(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
        fontSize = 48.sp,
        modifier = Modifier
            .padding(all = 20.dp)
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(all = 20.dp),
    )
}
