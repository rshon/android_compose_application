package com.example.chapter_coroutine

import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.runBlocking
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "OldStyleCallback"

interface NetworkListener {
    fun onSuccess(response: String)
    fun onFailure(error: Throwable)
}

class LegacyNetworkClient {
    private var listener: NetworkListener? = null

    fun setListener(listener: NetworkListener) {
        this.listener = listener
    }

    fun sendRequest(url: String) {
        Thread {
            try {
                Thread.sleep(1000)  // regarding server latency

                if (url.contains("success")) {
                    listener?.onSuccess("response: $url")
                } else {
                    listener?.onFailure(RuntimeException("Network Error"))
                }
            } catch (e: Exception) {
                listener?.onFailure(e)
            }
        }.start()
    }
}

fun testOldStyleCallback() {
    val client = LegacyNetworkClient()

    client.setListener(object : NetworkListener {
        override fun onSuccess(response: String) {
            println("on success: $response")
        }

        override fun onFailure(error: Throwable) {
            println("on error: ${error.message}")
        }
    })

    client.sendRequest("https://api.example.com/success")

    println("end of request")
}

class MyController {
    private val messageQueue: BlockingQueue<String> = LinkedBlockingQueue()

    fun start() {
        val client = LegacyNetworkClient()

        client.setListener(object : NetworkListener {
            override fun onSuccess(data: String) {
                messageQueue.put("SUCCESS:$data")
            }

            override fun onFailure(error: Throwable) {
                messageQueue.put("ERROR:${error.message}")
            }
        })

        client.sendRequest("https://example.com/good")

        Thread {
            while (true) {
                val message = messageQueue.take()
                when {
                    message.startsWith("SUCCESS:") -> {
                        println("on success: ${message.removePrefix("SUCCESS:")}")
                        break
                    }
                    message.startsWith("ERROR:") -> {
                        println("on error: ${message.removePrefix("ERROR:")}")
                        break
                    }
                }
            }
        }.start()
    }
}

fun testOldStyleCallbackWithAsyncClient() {
    val controller = MyController()
    controller.start()
}

suspend fun LegacyNetworkClient.sendRequestSuspend(url: String): String =
    suspendCancellableCoroutine { cont ->
        setListener(object : NetworkListener {
            override fun onSuccess(data: String) = cont.resume(data)
            override fun onFailure(error: Throwable) = cont.resumeWithException(error)
        })

        sendRequest(url)
    }


suspend fun testSuspendCancellableCoruotine2(): String {
    val client = LegacyNetworkClient()
    return client.sendRequestSuspend("https://example.com/success")
}


fun testSuspendCancellableCoruotine(): String? {
    val client = LegacyNetworkClient()
    var result: String? = null

    CoroutineScope(Dispatchers.IO).launch {
        try {
            result = client.sendRequestSuspend("https://example.com/success")
            println("result: $result")
        } catch (e: Exception) {
            println("error: ${e.message}")
        }
    }

    return result
}


