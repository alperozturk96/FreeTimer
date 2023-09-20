package com.coolnexttech.freetimer.view.countdown

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.extension.findActivity
import com.coolnexttech.freetimer.extension.hideNavBar
import com.coolnexttech.freetimer.extension.hideSystemBar
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.model.NotificationData
import com.coolnexttech.freetimer.model.play
import com.coolnexttech.freetimer.model.togglePlayButton
import com.coolnexttech.freetimer.notification.CountdownNotificationService
import com.coolnexttech.freetimer.ui.component.RoundedBox
import com.coolnexttech.freetimer.ui.theme.TertiaryColor
import com.coolnexttech.freetimer.viewmodel.CountdownViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun CountDownView(navController: NavHostController, viewModel: CountdownViewModel) {
    val context: Context = LocalContext.current

    val countdownNotificationService = CountdownNotificationService(context)
    val countdownData by viewModel.countdownData.collectAsState()
    val play by play.collectAsState()

    val playAndPauseButtonIconId: Int = if (!play) {
        R.drawable.ic_play
    } else {
        R.drawable.ic_pause
    }

    val timeLeft: String = if (countdownData.isRestModeActive) {
        stringResource(id = R.string.count_down_screen_rest_duration_info_text) + countdownData.restDuration
    } else {
        stringResource(id = R.string.count_down_screen_work_duration_info_text) + countdownData.workDuration
    }
    var showCancelCountdownAlert by remember { mutableStateOf(false) }
    var dimScreen by remember { mutableStateOf(false) }
    val systemUiController = rememberSystemUiController()

    BackHandler {
        showCancelCountdownAlert = true
    }

    LaunchedEffect(true) {
        viewModel.init(context)
    }

    if (countdownData.isWorkoutFinished()) {
        finishCountdown(navController, countdownNotificationService)
    }

    if (showCancelCountdownAlert) {
        CancelCountdownAlertDialog(cancelCountdown = {
            showCancelCountdownAlert = false
            finishCountdown(navController, countdownNotificationService)
        }, dismiss = {
            showCancelCountdownAlert = false
        })
    }

    if (dimScreen) {
        BlackScreen {
            dimScreen = false
        }
    } else {
        CountDownViewState(timeLeft, countdownData, play, playAndPauseButtonIconId) {
            dimScreen = true
        }
    }

    systemUiController.hideSystemBar(dimScreen)
    context.findActivity().hideNavBar(dimScreen)

    countdownNotificationService.showNotification(
        getNotificationData(context, dimScreen, play, timeLeft, playAndPauseButtonIconId, countdownData)
    )
}

private fun getNotificationData(
    context: Context,
    dimScreen: Boolean,
    play: Boolean,
    timeLeft: String,
    playAndPauseButtonIconId: Int,
    countdownData: CountdownData
): NotificationData {
    val setCountInfo =
        context.getString(R.string.count_down_screen_set_count_info_text) + countdownData.setCount.toString()
    val notificationIconId = if (dimScreen) {
        R.drawable.ic_circle
    } else {
        R.drawable.ic_timer
    }
    val actionTitle: String = context.getString(
        if (!play) {
            R.string.count_down_screen_notification_resume_action_button_title
        } else {
            R.string.count_down_screen_notification_stop_action_button_title
        }
    )

    return NotificationData(
        setCountInfo,
        timeLeft,
        notificationIconId,
        playAndPauseButtonIconId,
        actionTitle
    )
}

@Composable
private fun CountDownViewState(
    timeLeft: String,
    countdownData: CountdownData,
    play: Boolean,
    playAndPauseButtonIconId: Int,
    lockScreen: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        InfoText(text = stringResource(id = R.string.count_down_screen_set_count_info_text) + countdownData.setCount)
        InfoText(text = timeLeft)
        IconButton(onClick = { togglePlayButton(!play) }) {
            Icon(
                painter = painterResource(id = playAndPauseButtonIconId),
                tint = TertiaryColor,
                modifier = Modifier.size(48.dp),
                contentDescription = "Play and Pause",
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = stringResource(id = R.string.count_down_screen_switch_text))
        Switch(
            checked = false,
            onCheckedChange = {
                lockScreen()
            }
        )
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

@Composable
private fun BlackScreen(unlockScreen: () -> Unit) {
    Box(modifier = Modifier
        .background(Color.Black)
        .fillMaxSize()
        .clickable {
            unlockScreen()
        }) {}
}

@Composable
private fun CancelCountdownAlertDialog(cancelCountdown: () -> Unit, dismiss: () -> Unit) {
    AlertDialog(onDismissRequest = { }, title = {
        Text(text = stringResource(id = R.string.count_down_screen_cancel_countdown_alert_title))
    }, confirmButton = {
        TextButton(onClick = {
            cancelCountdown()
        }) {
            Text(
                stringResource(id = R.string.count_down_screen_cancel_countdown_confirm_button),
                color = Color.Black
            )
        }
    }, dismissButton = {
        TextButton(onClick = { dismiss() }) {
            Text(
                stringResource(id = R.string.count_down_screen_cancel_countdown_dismiss_button),
                color = Color.Black
            )
        }
    })
}

private fun finishCountdown(
    navController: NavHostController,
    notificationManager: CountdownNotificationService
) {
    notificationManager.cancelNotification()
    navController.popBackStack()
}
