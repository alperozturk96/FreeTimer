package com.coolnexttech.freetimer.view.countdown

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.coolnexttech.freetimer.model.CountDownControllerData
import com.coolnexttech.freetimer.model.CountdownController
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.ui.component.RoundedBox
import com.coolnexttech.freetimer.ui.theme.TertiaryColor
import com.coolnexttech.freetimer.viewmodel.CountdownViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun CountDownView(navController: NavHostController, viewModel: CountdownViewModel) {
    val context: Context = LocalContext.current

    val countdownData by viewModel.countdownData.collectAsState()
    val countDownControllerData by CountdownController.data.collectAsState()
    val dimScreen by viewModel.dimScreen.collectAsState()

    var showCancelCountdownAlert by remember { mutableStateOf(false) }
    val systemUiController = rememberSystemUiController()

    BackHandler {
        showCancelCountdownAlert = true
    }

    LaunchedEffect(true) {
        viewModel.init(context, navController)
    }

    if (showCancelCountdownAlert) {
        CancelCountdownAlertDialog(cancelCountdown = {
            showCancelCountdownAlert = false
            navController.popBackStack()
        }, dismiss = {
            showCancelCountdownAlert = false
        })
    }

    if (dimScreen) {
        BlackScreen(viewModel)
    } else {
        CountDownViewState(context, viewModel, countdownData, countDownControllerData)
    }

    systemUiController.hideSystemBar(dimScreen)
    context.findActivity().hideNavBar(dimScreen)
}

@Composable
private fun CountDownViewState(
    context: Context,
    viewModel: CountdownViewModel,
    countdownData: CountdownData,
    countDownControllerData: CountDownControllerData
) {
    val isCountdownStarted by viewModel.isCountdownStarted.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        InfoText(text = stringResource(id = R.string.count_down_screen_set_count_info_text) + countdownData.setCount)
        InfoText(
            text = if (isCountdownStarted) {
                countdownData.getTimeLeftInfo(context)
            } else {
                stringResource(
                    id = R.string.count_down_screen_ready_info_text
                )
            }
        )
        Row {
            IconButton(onClick = { CountdownController.toggle() }) {
                Icon(
                    painter = painterResource(id = countDownControllerData.controlButtonIconId),
                    tint = TertiaryColor,
                    modifier = Modifier.size(48.dp),
                    contentDescription = "Play and Pause",
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            IconButton(onClick = { viewModel.dimScreen(true) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sleep),
                    tint = TertiaryColor,
                    modifier = Modifier.size(48.dp),
                    contentDescription = "Dim Screen",
                )
            }
        }
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
private fun BlackScreen(viewModel: CountdownViewModel) {
    Box(modifier = Modifier
        .background(Color.Black)
        .fillMaxSize()
        .clickable {
            viewModel.dimScreen(false)
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