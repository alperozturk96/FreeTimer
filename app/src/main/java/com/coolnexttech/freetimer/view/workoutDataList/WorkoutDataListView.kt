package com.coolnexttech.freetimer.view.workoutDataList

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.navigation.Destinations
import com.coolnexttech.freetimer.ui.theme.TertiaryColor
import com.coolnexttech.freetimer.viewmodel.WorkoutDataListViewModel

@Composable
fun WorkoutDataListView(navController: NavHostController, viewModel: WorkoutDataListViewModel) {
    val workoutDataList by viewModel.workoutDataList.collectAsState()
    var showDeleteAlert by remember { mutableStateOf(false) }
    var selectedWorkoutData by remember { mutableStateOf(WorkoutData()) }

    val context = LocalContext.current

    DisposableEffect(Unit) {
        viewModel.initDb(context)
        onDispose { }
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(workoutDataList) { item ->
            WorkoutDataListItemView(item, context, navController) {
                showDeleteAlert = true
                selectedWorkoutData = item
            }
        }
    }

    if (showDeleteAlert) {
        DeleteWorkoutDataAlertDialog(viewModel, selectedWorkoutData) {
            showDeleteAlert = false
        }
    }
}

@Composable
private fun DeleteWorkoutDataAlertDialog(
    viewModel: WorkoutDataListViewModel,
    workoutData: WorkoutData,
    dismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = stringResource(id = R.string.count_down_screen_delete_workout_alert_title))
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.deleteWorkoutData(workoutData)
                dismiss()
            }) {
                Text(
                    stringResource(id = R.string.count_down_screen_delete_workout_confirm_button),
                    color = Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) {
                Text(
                    stringResource(id = R.string.count_down_screen_delete_workout_dismiss_button),
                    color = Color.Black
                )
            }
        }
    )
}

@Composable
private fun WorkoutDataListItemView(
    workoutData: WorkoutData,
    context: Context,
    navController: NavHostController,
    showDeleteAlert: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(30.dp))
            .background(TertiaryColor)
            .clickable {
                Destinations.navigateToCountDownView(workoutData, context, navController)
            }
    ) {
        Row {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = workoutData.name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .height(100.dp)
                    .wrapContentHeight()
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                modifier = Modifier.padding(vertical = 8.dp),
                onClick = {
                    showDeleteAlert()
                }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = Color.Black,
                    contentDescription = "Delete workout data"
                )
            }
        }
    }
}