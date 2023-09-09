package com.coolnexttech.freetimer.view.workoutDataList

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.navigation.Destinations
import com.coolnexttech.freetimer.ui.theme.TertiaryColor
import com.coolnexttech.freetimer.viewmodel.WorkoutDataListViewModel

@Composable
fun WorkoutDataListView(navController: NavHostController, viewModel: WorkoutDataListViewModel) {
    val workoutDataList by viewModel.workoutDataList.collectAsState()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        viewModel.initDb(context)
        onDispose { }
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(workoutDataList) { item ->
            WorkoutDataListItemView(item, context, navController)
        }
    }
}

@Composable
private fun WorkoutDataListItemView(
    workoutData: WorkoutData,
    context: Context,
    navController: NavHostController
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
        Text(
            text = workoutData.name,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .height(100.dp)
                .wrapContentHeight()
        )
    }
}