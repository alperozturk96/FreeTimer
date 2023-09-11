package com.coolnexttech.freetimer.extension

import android.app.Activity
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.hideNavBar(hide: Boolean) {
    val decorView: View = this.window.decorView
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controllerCompat = WindowInsetsControllerCompat(window, decorView)
    if (hide) {
        controllerCompat.hide(WindowInsetsCompat.Type.navigationBars())
    } else {
        controllerCompat.show(WindowInsetsCompat.Type.navigationBars())
    }
    controllerCompat.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}