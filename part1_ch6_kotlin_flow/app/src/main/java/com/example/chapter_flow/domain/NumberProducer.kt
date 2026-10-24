package com.example.chapter_flow.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NumberProducer {
    fun produceNumbers(): Flow<Int> = flow {
        for (i in 1..3) {
            delay(100) // 각 숫자를 0.1초 간격으로 방출
            println("emitting $i")
            emit(i)
        }
    }
}
