package com.coolnexttech.freetimer.view.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.navigation.Destinations
import com.coolnexttech.freetimer.ui.theme.BorderColor
import com.coolnexttech.freetimer.ui.theme.TertiaryColor
import com.coolnexttech.freetimer.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(navController: NavHostController, viewModel: HomeViewModel) {
    val workoutData: WorkoutData by viewModel.workoutData.collectAsState()
    val showSaveWorkoutAlert by viewModel.showSaveWorkoutAlert.collectAsState()

    val context = LocalContext.current

    DisposableEffect(Unit) {
        viewModel.initDb(context)
        onDispose { }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Destinations.WorkoutDataList)
                    }) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "Navigate To Workout Data List"
                        )
                    }
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
                Destinations.navigateToCountDownView(workoutData, context, navController)
            }) {
                Text(text = "Start Timer", color = Color.Black)
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (showSaveWorkoutAlert) {
            SaveWorkoutAlert(context, viewModel)
        }
    }
}

@Composable
private fun SaveWorkoutAlert(context: Context, viewModel: HomeViewModel) {
    var name by remember { mutableStateOf("") }
    var warningMessage: String? = null

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = "Save Current Workout Data")
        },
        text = {
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedContainerColor = TertiaryColor,
                    unfocusedContainerColor = TertiaryColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(30.dp),
                placeholder = { Text(text = "Workout Name") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                value = name,
                onValueChange = {
                    name = it
                    viewModel.updateWorkoutDataName(it)
                },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (!viewModel.workoutData.value.isValid()) {
                    warningMessage = "Please fill workout data for saving"
                }
                if (name.isEmpty()) {
                    warningMessage = "Please enter name for workout data"
                }

                if (warningMessage != null) {
                    Toast.makeText(context, warningMessage, Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.saveWorkout()
                }
            }) {
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