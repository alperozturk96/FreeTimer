package com.coolnexttech.freetimer.view.countdown

import android.app.NotificationManager
import android.content.Context
import android.os.Handler
import android.os.Looper
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
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.util.MusicPlayer

@Composable
fun CountDownView(navController: NavHostController, initialWorkoutData: WorkoutData) {
    val initialWorkoutDuration = initialWorkoutData.workDuration
    val initialRestDuration = initialWorkoutData.restDuration

    var workoutData by remember { mutableStateOf(initialWorkoutData) }
    var isRestModeActive by remember { mutableStateOf(false) }

    val context: Context = LocalContext.current
    val musicPlayer = MusicPlayer(context)

    Handler(Looper.getMainLooper()).postDelayed({
        if (isRestModeActive) {
            workoutData = workoutData.copy(restDuration = workoutData.restDuration - 1)

            if (workoutData.restDuration == 0) {
                workoutData = workoutData.copy(setCount = workoutData.setCount - 1)
                isRestModeActive = false
                workoutData = workoutData.copy(workDuration = initialWorkoutDuration)
                workoutData = workoutData.copy(restDuration = initialRestDuration)
                musicPlayer.playAudio(R.raw.boxing_bell)

                if (workoutData.setCount == 0) {
                    musicPlayer.stopAudio()
                    navController.popBackStack()
                }
            }
        } else {
            workoutData = workoutData.copy(workDuration = workoutData.workDuration - 1)

            if (workoutData.workDuration == 0) {
                musicPlayer.playAudio(R.raw.boxing_bell)
                isRestModeActive = true
            }
        }
    }, 1000)

    CountDownViewState(workoutData, isRestModeActive)
}

@Composable
private fun CountDownViewState(workoutData: WorkoutData, isRestModeActive: Boolean) {
    val context: Context = LocalContext.current

    val timeLeft = if (isRestModeActive) {
        "Rest: " + workoutData.restDuration
    } else {
        "Time Left: " + workoutData.workDuration
    }

    updateNotification(context, timeLeft)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        InfoText(text = "SET: " + workoutData.setCount)
        InfoText(text = timeLeft)
        Spacer(modifier = Modifier.weight(1f))
    }
}

private fun updateNotification(context: Context, timeLeft: String) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(
        context,
        "CountdownTimerServiceNotification"
    )
        .setSilent(true)
        .setSmallIcon(R.drawable.im_timer)
        .setContentTitle(timeLeft)
        .build()

    notificationManager.notify(1, notification)
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
