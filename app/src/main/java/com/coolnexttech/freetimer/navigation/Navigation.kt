package com.coolnexttech.freetimer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.view.countdown.CountDownView
import com.coolnexttech.freetimer.view.home.HomeView
import com.google.gson.Gson

@Composable
fun Navigation(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination) {
        composable(Destinations.Home) {
            HomeView(navController = navController)
        }

        composable(
            Destinations.CountDown + "/" + "{workoutData}",
            arguments = listOf(navArgument("workoutData") { type = NavType.StringType })
        ) {
            val json = it.arguments?.getString("workoutData")
            val workoutData = Gson().fromJson(json, WorkoutData::class.java)
            CountDownView(navController, workoutData)
        }
    }
}