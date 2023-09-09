package com.coolnexttech.freetimer.navigation

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.model.WorkoutData
import com.google.gson.Gson

object Destinations {
    const val Home = "Home"
    const val CountDown = "CountDown"
    const val WorkoutDataList = "WorkoutDataList"

    fun navigateToCountDownView(
        workoutData: WorkoutData,
        context: Context,
        navController: NavHostController
    ) {
        if (workoutData.isValid()) {
            val json = Gson().toJson(workoutData)
            navController.navigate(Destinations.CountDown + "/" + json)
        } else {
            Toast.makeText(context, "Please enter valid workout duration", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

