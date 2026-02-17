package com.example.chapter_flow

import com.example.chapter_flow.domain.NumberProducer
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class NumberProducerTest {
    @Test
    fun produceNumbers_emitsCorrectSequence() = runTest { // 라이브러리에서 제공하는 runTest 사용
        // Given (준비)
        val producer = NumberProducer()

        // When (실행)
        val result = producer.produceNumbers().toList()

        println("result $result")

        // Then (검증)
        assertEquals(listOf(1, 2, 3), result)
    }


}