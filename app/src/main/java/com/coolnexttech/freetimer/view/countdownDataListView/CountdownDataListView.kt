package com.coolnexttech.freetimer.view.countdownDataListView

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.ui.component.RoundedBox
import com.coolnexttech.freetimer.ui.navigation.Destinations
import com.coolnexttech.freetimer.ui.theme.DangerColor
import com.coolnexttech.freetimer.ui.theme.TertiaryColor
import com.coolnexttech.freetimer.viewmodel.CountdownDataListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownDataListView(navController: NavHostController, viewModel: CountdownDataListViewModel) {
    val countdownDataList by viewModel.countdownDataList.collectAsState()
    var showDeleteAlert by remember { mutableStateOf(false) }
    var selectedCountdownData by remember { mutableStateOf(CountdownData()) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initDb(context)
    }

    if (countdownDataList.isEmpty()) {
        NoCountdownDataText()
    } else {
        LazyColumn(state = rememberLazyListState(), modifier = Modifier.padding(16.dp)) {
            items(countdownDataList) { item ->
                val state = rememberDismissState(
                    confirmValueChange = {
                        if (it == DismissValue.DismissedToStart) {
                            showDeleteAlert = true
                            selectedCountdownData = item
                        }
                        false
                    }
                )

                SwipeToDismiss(
                    state = state,
                    background = {
                        val color = when (state.dismissDirection) {
                            DismissDirection.EndToStart -> DangerColor
                            DismissDirection.StartToEnd -> TertiaryColor
                            null -> Color.Transparent
                        }
                        RoundedBox(
                            backgroundColor = color
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete, contentDescription = "Delete",
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 20.dp)
                            )
                        }
                    },
                    dismissContent = {
                        Column {
                            Spacer(modifier = Modifier.padding(top = 10.dp))
                            CountdownDataListItemView(item, context, navController)
                        }
                    })
            }
        }
    }

    if (showDeleteAlert) {
        DeleteCountdownDataAlertDialog(viewModel, selectedCountdownData) {
            showDeleteAlert = false
        }
    }
}

@Composable
private fun CountdownDataListItemView(
    countdownData: CountdownData,
    context: Context,
    navController: NavHostController
) {
    RoundedBox(action = {
        Destinations.navigateToCountDownView(countdownData, context, navController)
    }) {
        Text(
            text = countdownData.name,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .height(100.dp)
                .wrapContentHeight()
        )
    }
}

@Composable
private fun NoCountdownDataText() {
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(id = R.string.countdown_data_list_screen_no_workout_data_text),
            color = Color.Black,
            fontSize = 20.sp
        )
    }
}

@Composable
private fun DeleteCountdownDataAlertDialog(
    viewModel: CountdownDataListViewModel,
    countdownData: CountdownData,
    dismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = stringResource(id = R.string.countdown_data_list_screen_delete_workout_alert_title))
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.deleteCountdownData(countdownData)
                dismiss()
            }) {
                Text(
                    stringResource(id = R.string.countdown_data_list_screen_delete_workout_confirm_button),
                    color = Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) {
                Text(
                    stringResource(id = R.string.countdown_data_list_screen_delete_workout_dismiss_button),
                    color = Color.Black
                )
            }
        }
    )
}