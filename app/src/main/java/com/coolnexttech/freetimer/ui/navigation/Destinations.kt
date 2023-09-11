package com.coolnexttech.freetimer.ui.navigation

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.model.toJson

object Destinations {
    const val Home = "Home"
    const val CountdownDataList = "CountdownDataList"
    const val CountDown = "CountDown"

    fun navigateToCountDownView(
        countdownData: CountdownData,
        context: Context,
        navController: NavHostController
    ) {
        if (countdownData.isValid()) {
            navController.navigate("$CountDown/${countdownData.toJson()}")
        } else {
            Toast.makeText(context, "Please enter valid countdown duration", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

