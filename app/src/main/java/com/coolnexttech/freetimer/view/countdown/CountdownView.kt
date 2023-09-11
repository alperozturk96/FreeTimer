package com.coolnexttech.freetimer.view.countdown

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.coolnexttech.freetimer.manager.CountdownTimerNotificationManager
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.model.toJson
import com.coolnexttech.freetimer.service.MusicPlayerService
import com.coolnexttech.freetimer.ui.component.LifecycleEventListener
import com.coolnexttech.freetimer.ui.component.RoundedBox
import com.coolnexttech.freetimer.ui.navigation.Destinations
import com.coolnexttech.freetimer.viewmodel.CountDownViewModel

@Composable
fun CountDownView(
    navController: NavHostController, viewModel: CountDownViewModel, initialCountdownData: CountdownData
) {
    val context: Context = LocalContext.current
    val notificationManager = CountdownTimerNotificationManager(context)
    val serviceIntent = Intent(context, MusicPlayerService::class.java)
    val countdownData by viewModel.countdownData.collectAsState()
    var showCancelWorkoutAlert by remember { mutableStateOf(false) }
    val isCountDownCompleted by viewModel.isCountDownCompleted.collectAsState()

    BackHandler {
        showCancelWorkoutAlert = true
    }

    ObserveWorkoutData(context, serviceIntent, countdownData, viewModel)

    DisposableEffect(Unit) {
        viewModel.init(initialCountdownData, context)
        onDispose {

        }
    }

    if (isCountDownCompleted) {
        navigateBackToHome(navController)
    }

    if (showCancelWorkoutAlert) {
        CancelCountdownAlertDialog(cancelCountdown = {
            MusicPlayerService.canStartService = false
            viewModel.finishCountDown()
            notificationManager.deleteNotificationChannel()
            stopMusicPlayerService(context, serviceIntent)
            navController.popBackStack()
        }, dismiss = {
            showCancelWorkoutAlert = false
        })
    }

    CountDownViewState(countdownData, notificationManager)
}

@Composable
private fun CancelCountdownAlertDialog(cancelCountdown: () -> Unit, dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = stringResource(id = R.string.count_down_screen_cancel_countdown_alert_title))
        },
        confirmButton = {
            TextButton(onClick = {
                cancelCountdown()
            }) {
                Text(
                    stringResource(id = R.string.count_down_screen_cancel_countdown_confirm_button),
                    color = Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) {
                Text(
                    stringResource(id = R.string.count_down_screen_cancel_countdown_dismiss_button),
                    color = Color.Black
                )
            }
        }
    )
}

@Composable
private fun ObserveWorkoutData(
    context: Context,
    serviceIntent: Intent,
    countdownData: CountdownData,
    viewModel: CountDownViewModel
) {
    LifecycleEventListener { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                viewModel.saveTempWorkoutData()
                startMusicPlayerService(context, serviceIntent, countdownData)
                viewModel.disableMediaPlayer()
            }

            Lifecycle.Event.ON_START -> {
                stopMusicPlayerService(context, serviceIntent)
                viewModel.updateWorkoutDataWithTempWorkoutData()
            }

            else -> {}
        }
    }
}

private fun startMusicPlayerService(
    context: Context, serviceIntent: Intent, countdownData: CountdownData
) {
    serviceIntent.action = MusicPlayerService.Actions.Start.toString()
    serviceIntent.putExtra(MusicPlayerService.serviceCountdownData, countdownData.toJson())
    context.startService(serviceIntent)
}

private fun stopMusicPlayerService(context: Context, serviceIntent: Intent) {
    serviceIntent.action = MusicPlayerService.Actions.Stop.toString()
    context.startService(serviceIntent)
}

private fun navigateBackToHome(navController: NavHostController) {
    navController.navigate(Destinations.Home) {
        popUpTo(Destinations.Home) {
            inclusive = true
        }
    }
}

@Composable
private fun CountDownViewState(
    countdownData: CountdownData, notificationManager: CountdownTimerNotificationManager
) {

    val timeLeft = if (countdownData.isRestModeActive) {
        stringResource(id = R.string.count_down_screen_rest_duration_info_text) + countdownData.restDuration
    } else {
        stringResource(id = R.string.count_down_screen_work_duration_info_text) + countdownData.workDuration
    }

    notificationManager.createNotificationChannel()
    notificationManager.createNotification(timeLeft)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        InfoText(text = stringResource(id = R.string.count_down_screen_set_count_info_text) + countdownData.setCount)
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
