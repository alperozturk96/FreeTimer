package com.coolnexttech.freetimer.model

import com.coolnexttech.freetimer.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class CountDownControllerData(
    val resume: Boolean = true,
    val controlButtonIconId: Int = R.drawable.ic_pause,
    val notificationActionTitleId: Int = R.string.count_down_screen_notification_pause_action_title
)

object CountdownController {
    var data = MutableStateFlow(CountDownControllerData())

    fun toggle() {
        data.update {
            if (it.resume) {
                getPauseState()
            } else {
                CountDownControllerData()
            }
        }
    }

    private fun getPauseState(): CountDownControllerData {
        return CountDownControllerData(false, R.drawable.ic_resume, R.string.count_down_screen_notification_resume_action_title)
    }
}