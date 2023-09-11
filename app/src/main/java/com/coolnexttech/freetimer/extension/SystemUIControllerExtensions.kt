package com.coolnexttech.freetimer.extension

import androidx.compose.ui.graphics.Color
import com.coolnexttech.freetimer.ui.theme.PrimaryColor
import com.google.accompanist.systemuicontroller.SystemUiController

fun SystemUiController.hideSystemBar(hide: Boolean) {
    if (hide) {
        setSystemBarsColor(
            isNavigationBarContrastEnforced = false,
            darkIcons = true,
            color = Color.Black
        )
    } else {
        setSystemBarsColor(
            darkIcons = false,
            color = PrimaryColor
        )
    }
}