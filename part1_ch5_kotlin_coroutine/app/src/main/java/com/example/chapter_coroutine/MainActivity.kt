package com.example.chapter_coroutine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.cancellation.CancellationException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
//            CoroutineExample()
//            ExExceptionPropagationWithSupervisorScope()
//            ExCancel()
//            ExTestCoroutineScope()

//            testOldStyleCallback()
//            testOldStyleCallbackWithAsyncClient()
//            val res = testSuspendCancellableCoroutine()
//            println("returned res = $res")

//            testChannel()
//            testBufferedChannel()
//            testProduceBuilder()
            testWithContext()
        }
    }
}

@Composable
fun ExTestCoroutineScope() {
    println("ExTestCoroutineScope")
    LaunchedEffect(Unit) {
        try {
            runCoroutineScope()
        } catch (e: Exception) {
            println("잡힌 예외: $e")
        }
    }
}

suspend fun runCoroutineScope() = coroutineScope {
    println("runCoroutineScope")

    launch {
        println("스레드: ${Thread.currentThread().name}")
        throw RuntimeException("오류")
    }
}

@Composable
fun ExCancel() {
    LaunchedEffect(Unit) {
        suspend fun exceptionHandlingExample() = coroutineScope {
            val handler = CoroutineExceptionHandler { _, exception ->
                println("Caught exception: ${exception.message}")
            }

            // 예외 처리를 위한 job
            val job = launch(handler) {
                delay(100)
                throw RuntimeException("코루틴 내부 오류")
            }

            // 취소 가능한 작업
            val cancelableJob = launch {
                try {
                    repeat(1000) { i ->
                        println("작업 $i 실행 중...")
                        delay(500)
                    }
                } catch (e: CancellationException) {
                    println("작업이 취소되었습니다.")
                } finally {
                    println("정리 작업 완료")
                }
            }

            job.join() // 예외 발생 후 완료 대기

            delay(1500) // 1.5초 후 취소
            cancelableJob.cancel()
            cancelableJob.join()
        }

        try {
            exceptionHandlingExample()
        } catch (e: Exception) {
            println("상위 스코프에서 잡힌 예외: ${e.message}")
        }
    }
}

@Composable
fun ExExceptionPropagationWithSupervisorScope() {
    LaunchedEffect(Unit) {
        suspend fun reliableOperation() = supervisorScope {
            val job1 = launch {
                delay(500)
                println("Job 1은 항상 완료됨")
            }

            val job2 = launch {
                try {
                    delay(300)
                    throw RuntimeException("이 예외는 job1에 영향을 주지 않음")
                } catch (e: Exception) {
                    println("Job 2 실패: ${e.message}")
                }
            }
        }

        reliableOperation()
    }
}

@Composable
fun CoroutineExample() {
    val scope = rememberCoroutineScope() // Composable에서 사용할 코루틴 스코프 생성

    LaunchedEffect(Unit) { // Composable이 처음 구성될 때 실행
        val parentJob = Job() // 부모 Job 생성

        scope.launch(parentJob) {
            println("부모 코루틴 시작")
            launch {
                delay(2000)
                println("자식 코루틴 1: 아직 실행 중")
            }
            launch {
                delay(3000)
                println("자식 코루틴 2: 아직 실행 중")
            }
            delay(1000)
            println("부모 코루틴 끝나기 전")
        }

        // 1.5초 후 부모 Job 취소
        scope.launch {
            delay(1500)
            parentJob.cancel() // 부모 취소 -> 모든 자식도 취소됨
            println("부모 Job 취소됨")
        }
    }
}
