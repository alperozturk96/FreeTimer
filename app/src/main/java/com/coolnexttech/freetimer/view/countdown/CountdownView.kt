package com.coolnexttech.freetimer.view.countdown

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.ui.navigation.Destinations
import com.coolnexttech.freetimer.ui.component.RoundedBox
import com.coolnexttech.freetimer.manager.CountdownTimerNotificationManager
import com.coolnexttech.freetimer.model.toJson
import com.coolnexttech.freetimer.service.MusicPlayerService
import com.coolnexttech.freetimer.ui.component.LifecycleEventListener
import com.coolnexttech.freetimer.viewmodel.CountDownViewModel

@Composable
fun CountDownView(
    navController: NavHostController,
    viewModel: CountDownViewModel,
    initialWorkoutData: WorkoutData
) {
    val context: Context = LocalContext.current
    val notificationManager = CountdownTimerNotificationManager(context)

    val workoutData by viewModel.workoutData.collectAsState()
    val isTrainingCompleted by viewModel.isCountDownCompleted.collectAsState()

    BackHandler {
        viewModel.removeTempWorkoutData()
        notificationManager.deleteNotificationChannel()
        navController.popBackStack()
    }

    ObserveWorkoutData(context, viewModel)

    DisposableEffect(Unit) {
        viewModel.init(initialWorkoutData, context)
        onDispose {

        }
    }

    if (isTrainingCompleted) {
        navigateBackToHome(navController)
    }

    CountDownViewState(workoutData, notificationManager)
}

@Composable
private fun ObserveWorkoutData(context: Context, viewModel: CountDownViewModel) {
    val serviceIntent = Intent(context, MusicPlayerService::class.java)

    LifecycleEventListener { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                viewModel.saveTempWorkoutData()

                serviceIntent.action = MusicPlayerService.Actions.Start.toString()
                serviceIntent.putExtra(MusicPlayerService.serviceWorkoutData, viewModel.workoutData.value.toJson())
                context.startService(serviceIntent)

                viewModel.disableMediaPlayer()
            }
            Lifecycle.Event.ON_START -> {
                serviceIntent.action = MusicPlayerService.Actions.Stop.toString()
                context.stopService(serviceIntent)

                viewModel.updateWorkoutDataWithTempWorkoutData()
            }
            else -> {}
        }
    }
}

private fun navigateBackToHome(navController: NavHostController) {
    navController.navigate(Destinations.Home) {
        popUpTo(Destinations.Home) {
            inclusive = true
        }
    }
}

@Composable
private fun CountDownViewState(workoutData: WorkoutData, notificationManager: CountdownTimerNotificationManager) {

    val timeLeft = if (workoutData.isRestModeActive) {
        stringResource(id = R.string.count_down_screen_rest_duration_info_text) + workoutData.restDuration
    } else {
        stringResource(id = R.string.count_down_screen_work_duration_info_text) + workoutData.workDuration
    }

    notificationManager.createNotificationChannel()
    notificationManager.createNotification(timeLeft)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        InfoText(text = stringResource(id = R.string.count_down_screen_set_count_info_text) + workoutData.setCount)
        InfoText(text = timeLeft)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun InfoText(text: String) {
    RoundedBox(widthFraction = 0.5f, action = null) {
        Text(
            text = text,
            color = Color.Black,
            fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
            fontSize = 20.sp,
        )
    }
}
