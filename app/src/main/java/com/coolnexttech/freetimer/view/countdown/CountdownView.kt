package com.coolnexttech.freetimer.view.countdown

import android.app.NotificationManager
import android.content.Context
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.coolnexttech.freetimer.navigation.Destinations
import com.coolnexttech.freetimer.viewmodel.CountDownViewModel

@Composable
fun CountDownView(navController: NavHostController, viewModel: CountDownViewModel, initialWorkoutData: WorkoutData) {
    val context: Context = LocalContext.current

    val workoutData by viewModel.workoutData.collectAsState()
    val isRestModeActive by viewModel.isRestModeActive.collectAsState()
    val isTrainingCompleted by viewModel.isTrainingCompleted.collectAsState()

    DisposableEffect(Unit) {
        viewModel.init(initialWorkoutData, context)
        onDispose {

        }
    }

    if (isTrainingCompleted) {
        navigateBackToHome(navController)
    }

    CountDownViewState(workoutData, isRestModeActive)
}

private fun navigateBackToHome(navController: NavHostController) {
    navController.navigate(Destinations.Home) {
        popUpTo(Destinations.Home){
            inclusive = true
        }
    }
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
