package com.example.pico_botella.ui.home

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeRepository {
    fun startCountdown(): Flow<Int> = flow {
        for (i in 3 downTo 0) {
            emit(i)
            delay(1000)
        }
    }
}