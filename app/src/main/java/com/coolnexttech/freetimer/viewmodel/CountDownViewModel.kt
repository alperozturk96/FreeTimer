package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.manager.StorageManager
import com.coolnexttech.freetimer.manager.MediaPlayerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CountDownViewModel : ViewModel() {

    // region Flows
    private val _isCountdownCompleted = MutableStateFlow(false)
    val isCountDownCompleted = _isCountdownCompleted.asStateFlow()

    private val _workoutData = NoCompareMutableStateFlow(WorkoutData())
    val workoutData = _workoutData.asStateFlow()
    // endregion

    // region Dependencies
    private var storageManager: StorageManager? = null
    private var mediaPlayerManager: MediaPlayerManager? = null
    private var _initialWorkoutDuration = 0
    private var _initialRestDuration = 0
    // endregion

    fun init(workoutData: WorkoutData, context: Context) {
        _workoutData.value = workoutData
        _initialWorkoutDuration = workoutData.workDuration
        _initialRestDuration = workoutData.restDuration
        mediaPlayerManager = MediaPlayerManager(context)
        storageManager = StorageManager(context)

        startCountDown()
    }

    private fun startCountDown() {
        viewModelScope.launch(Dispatchers.Main) {
            while (!_isCountdownCompleted.value) {
                _workoutData.value.print()
                handleWorkoutData()
                delay(1000)
            }
        }
    }

    private fun handleWorkoutData() {
        if (_workoutData.value.isRestModeActive) {
            handleRestMode()
        } else {
            handleWorkoutMode()
        }
    }

    fun disableMediaPlayer() {
        mediaPlayerManager?.canPlay = false
    }

    private fun releaseMediaPlayer() {
        disableMediaPlayer()
        mediaPlayerManager = null
    }

    fun finishCountDown() {
        _isCountdownCompleted.update {
            true
        }
        releaseMediaPlayer()
        removeTempWorkoutData()
    }

    // region Handle Lifecycle Changes & Update Workout Data
    fun saveTempWorkoutData() {
        storageManager?.saveTempWorkoutData(_workoutData.value)
        storageManager?.saveWhenAppInBackground(System.currentTimeMillis())
        println("Temp Workout Data Saved")
    }

    fun updateWorkoutDataWithTempWorkoutData() {
        val tempWorkoutData = storageManager?.readTempWorkoutData() ?: return
        val whenAppInForeground = storageManager?.readWhenAppInForeground() ?: return
        val timeDiffInMilliSecond = System.currentTimeMillis() - whenAppInForeground
        val timeDiffInSecond = (timeDiffInMilliSecond / 1000L).toInt()

        println("Time Difference In Second: $timeDiffInSecond")
        _workoutData.update {
            tempWorkoutData
        }

        repeat(timeDiffInSecond) {
            handleWorkoutData()
        }

        mediaPlayerManager?.canPlay = true
        println("Workout Data updated with Temp Workout Data")
    }

    private fun removeTempWorkoutData() {
        storageManager?.removeTempData()
    }
    // endregion

    // region Rest Mode
    private fun handleRestMode() {
        _workoutData.update {
            it.copy(restDuration = _workoutData.value.restDuration - 1)
        }

        if (_workoutData.value.isCurrentSetRestFinished()) {
            startNextSet()
        }
    }

    private fun startNextSet() {
        _workoutData.update {
            it.copy(
                isRestModeActive = false,
                setCount = _workoutData.value.setCount - 1,
                workDuration = _initialWorkoutDuration,
                restDuration = _initialRestDuration
            )
        }
        mediaPlayerManager?.playAudio(R.raw.boxing_bell)

        if (_workoutData.value.isWorkoutFinished()) {
            finishCountDown()
        }
    }
    // endregion

    // region Workout Mode
    private fun handleWorkoutMode() {
        _workoutData.update {
            it.copy(workDuration = _workoutData.value.workDuration - 1)
        }

        if (_workoutData.value.isCurrentSetWorkoutFinished()) {
            switchToRestMode()
        }
    }

    private fun switchToRestMode() {
        mediaPlayerManager?.playAudio(R.raw.boxing_bell)
        _workoutData.update {
            it.copy(isRestModeActive = true)
        }
    }
    // endregion
}

class NoCompareMutableStateFlow<T>(
    value: T
) : MutableStateFlow<T> {

    override var value: T = value
        set(value) {
            field = value
            innerFlow.tryEmit(value)
        }

    private val innerFlow = MutableSharedFlow<T>(replay = 1)

    override fun compareAndSet(expect: T, update: T): Boolean {
        value = update
        return true
    }

    override suspend fun emit(value: T) {
        this.value = value
    }

    override fun tryEmit(value: T): Boolean {
        this.value = value
        return true
    }

    override val subscriptionCount: StateFlow<Int> = innerFlow.subscriptionCount
    @ExperimentalCoroutinesApi
    override fun resetReplayCache() = innerFlow.resetReplayCache()
    override suspend fun collect(collector: FlowCollector<T>): Nothing = innerFlow.collect(collector)
    override val replayCache: List<T> = innerFlow.replayCache
}