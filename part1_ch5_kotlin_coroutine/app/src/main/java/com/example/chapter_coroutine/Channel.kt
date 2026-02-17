package com.example.chapter_coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.channels.produce

suspend fun produceValues(channel: Channel<Int>) {
    (1..10).forEach { it ->
        println("produced value: $it")
        channel.send(it)
    }
}

suspend fun consumeValues(channel: Channel<Int>) {
    repeat(10) {
        println("received value: ${channel.receive()}")
    }
}

suspend fun testSimpleChannel() {
    val channel = Channel<Int>()

    coroutineScope {
        launch {
            produceValues(channel)
        }

        launch {
            consumeValues(channel)
        }
    }
}

fun testChannel() = runBlocking {
    testSimpleChannel()
}


fun main() = runBlocking {
    val channel = Channel<Int>()

    launch {
        for (x in 1..5) {
            println("sending: $x")
            channel.send(x)
        }
        channel.close()
    }

    for (y in channel) {
        println("received: $y")
    }
}

fun testBufferedChannel() = runBlocking {
    val channel = Channel<Int>(capacity = 3)

    launch {
        repeat(5) { i ->
            println("sending: $i")
            channel.send(i)
            println("sent: $i")
        }
        channel.close()
    }

    launch {
        for (value in channel) {
            delay(500)
            println("received: $value")
        }
    }
}

fun testProduceBuilder() = runBlocking {
    val numbers: ReceiveChannel<Int> = produce(capacity = 3) {
        for (i in 1..5) {
            delay(300)
            println("producing: $i")
            send(i)
        }
    }

    launch {
        for (num in numbers) {
            println("consumed: $num")
            delay(500)
        }
    }
}