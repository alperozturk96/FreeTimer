package com.coolnexttech.freetimer.app

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.coolnexttech.freetimer.extension.hideSystemBar
import com.coolnexttech.freetimer.ui.navigation.Destinations
import com.coolnexttech.freetimer.ui.navigation.Navigation
import com.coolnexttech.freetimer.ui.theme.FreeTimerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission(this)

        setContent {
            FreeTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetSystemBarColor()
                    keepScreenOn()
                    RootView()
                }
            }
        }
    }

    @Composable
    private fun RootView() {
        val navController = rememberNavController()
        Navigation(navController, startDestination = Destinations.Home)
    }

    private fun askNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    @Composable
    private fun SetSystemBarColor() {
        val systemUiController = rememberSystemUiController()
        systemUiController.hideSystemBar(false)
    }

    private fun keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}