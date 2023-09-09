package com.coolnexttech.freetimer.view.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.navigation.Destinations
import com.coolnexttech.freetimer.ui.theme.BorderColor
import com.coolnexttech.freetimer.viewmodel.HomeViewModel
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(navController: NavHostController, viewModel: HomeViewModel) {
    val workoutData: WorkoutData by viewModel.workoutData.collectAsState()
    val showSaveWorkoutAlert by viewModel.showSaveWorkoutAlert.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Free Timer",
                        color = Color.Black,
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                            fontSize = 20.sp
                        )
                    )
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.showSaveWorkoutAlert()
                    }) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = "Save workout data"
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            TimerInput(label = "Set Count", onValueChange = {
                workoutData.setCount = it
            })

            TimerInput(label = "Workout Duration In Second", onValueChange = {
                workoutData.workDuration = it
            })

            TimerInput(label = "Rest Duration In Second", onValueChange = {
                workoutData.restDuration = it
            })

            Button(onClick = {
                navigateToCountDownView(workoutData, context, navController)
            }) {
                Text(text = "Start Timer", color = Color.Black)
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (showSaveWorkoutAlert) {
            SaveWorkoutAlert(viewModel)
        }
    }
}

@Composable
private fun SaveWorkoutAlert(viewModel: HomeViewModel) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = "Info")
        },
        text = { Text(text = "Would you like to save current workout data?") },
        confirmButton = {
            TextButton(onClick = { viewModel.saveWorkout() }) {
                Text("Save", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.hideSaveWorkoutAlert() }) {
                Text("Cancel", color = Color.Black)
            }
        }
    )
}

private fun navigateToCountDownView(
    workoutData: WorkoutData, context: Context, navController: NavHostController
) {
    if (workoutData.isValid()) {
        val json = Gson().toJson(workoutData)
        navController.navigate(Destinations.CountDown + "/" + json)
    } else {
        Toast.makeText(context, "Please enter valid workout duration", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun TimerInput(
    label: String, onValueChange: (Int) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }

    OutlinedTextField(colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Black,
        unfocusedBorderColor = BorderColor,
    ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Normal),
        maxLines = 1,
        value = text,
        onValueChange = { value ->
            if (value.length <= 3) {
                val sanitizedValue = value.replace(Regex("[^0-9]"), "")
                text = sanitizedValue.filter { it.isDigit() }
                try {
                    onValueChange(sanitizedValue.toInt())
                } catch (_: Throwable) {
                }
            }
        },
        label = { Text(label, color = Color.Black) })
}