package com.coolnexttech.freetimer.view.countdown

import android.content.Context
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.navigation.Destinations
import com.coolnexttech.freetimer.util.NotificationService
import com.coolnexttech.freetimer.util.OnLifecycleEvent
import com.coolnexttech.freetimer.viewmodel.CountDownViewModel

@Composable
fun CountDownView(
    navController: NavHostController,
    viewModel: CountDownViewModel,
    initialWorkoutData: WorkoutData
) {
    val context: Context = LocalContext.current

    val workoutData by viewModel.workoutData.collectAsState()
    val isRestModeActive by viewModel.isRestModeActive.collectAsState()
    val isTrainingCompleted by viewModel.isCountDownCompleted.collectAsState()

    BackHandler {
        viewModel.removeTempWorkoutData()
        navController.popBackStack()
    }

    ObserveWorkoutData(viewModel)

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

@Composable
private fun ObserveWorkoutData(viewModel: CountDownViewModel) {
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                viewModel.saveTempWorkoutData()
            }
            Lifecycle.Event.ON_START -> {
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
private fun CountDownViewState(workoutData: WorkoutData, isRestModeActive: Boolean) {
    val context: Context = LocalContext.current
    val notificationService = NotificationService(context)

    val timeLeft = if (isRestModeActive) {
        stringResource(id = R.string.count_down_screen_rest_duration_info_text) + workoutData.restDuration
    } else {
        stringResource(id = R.string.count_down_screen_work_duration_info_text) + workoutData.workDuration
    }

    notificationService.createNotificationChannel()
    notificationService.createNotification(timeLeft)

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
