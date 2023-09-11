package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.freetimer.db.CountdownDataStorage
import com.coolnexttech.freetimer.model.CountdownData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CountdownDataListViewModel: ViewModel() {
    private val _countdownDataList = MutableStateFlow<List<CountdownData>>(listOf())
    val countdownDataList: StateFlow<List<CountdownData>> = _countdownDataList

    private var countdownDataStorage: CountdownDataStorage? = null

    fun initDb(context: Context) {
        countdownDataStorage = CountdownDataStorage.getInstance(context)
        getCountdownDataList()
    }

    fun deleteCountdownData(countdownData: CountdownData) {
        viewModelScope.launch(Dispatchers.IO) {
            countdownDataStorage?.delete(countdownData)
            getCountdownDataList()
        }
    }

    private fun getCountdownDataList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = countdownDataStorage?.getAll() ?: return@launch

            launch(Dispatchers.Main) {
                _countdownDataList.update {
                    list
                }
            }

        }
    }
}