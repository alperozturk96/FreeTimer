package com.coolnexttech.freetimer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.model.toCountdownData
import com.coolnexttech.freetimer.view.countdown.CountDownView
import com.coolnexttech.freetimer.view.countdownDataListView.CountdownDataListView
import com.coolnexttech.freetimer.view.home.HomeView
import com.coolnexttech.freetimer.viewmodel.CountDownViewModel
import com.coolnexttech.freetimer.viewmodel.CountdownDataListViewModel
import com.coolnexttech.freetimer.viewmodel.HomeViewModel

@Composable
fun Navigation(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination) {
        composable(route = Destinations.Home) {
            val viewModel: HomeViewModel = viewModel()
            HomeView(navController, viewModel)
        }

        composable(Destinations.CountdownDataList) {
            val viewModel: CountdownDataListViewModel = viewModel()
            CountdownDataListView(navController, viewModel)
        }

        composable(
            route = Destinations.CountDown + "/" + "{countdownData}",
            arguments = listOf(navArgument("countdownData") { type = NavType.StringType })
        ) {
            val json = it.arguments?.getString("countdownData")
            val countdownData = json?.toCountdownData() ?: CountdownData()
            val viewModel: CountDownViewModel = viewModel()
            CountDownView(navController, viewModel, countdownData)
        }
    }
}