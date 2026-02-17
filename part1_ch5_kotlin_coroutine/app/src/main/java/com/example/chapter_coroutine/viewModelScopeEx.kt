package com.example.chapter_coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel : ViewModel() {
    fun loadData() {
        viewModelScope.launch {
            val result = fetchSomething()
            println("result: $result")
        }

        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                var sum = 0L
                for (i in 1..100_000_000) {
                    sum += i
                }
                sum
            }

            println("result: $result")
        }
    }

    private suspend fun fetchSomething(): String {
        delay(1000)
        return "dummy data"
    }
}