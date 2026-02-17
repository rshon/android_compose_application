package com.example.mediaapplication.domain.usecase

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.mediaapplication.data.model.MediaItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException

class PlayMusicUseCase {

    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: Flow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0)
    val progress: Flow<Int> = _progress

    private val _duration = MutableStateFlow(0)
    val duration: Flow<Int> = _duration

    private var job: Job? = null
    private val handler = Handler(Looper.getMainLooper())
    private var updateProgressRunnable: Runnable? = null

    fun playMusic(mediaItem: MediaItem, scope: CoroutineScope) {
        if (mediaItem.filePath == null) {
            println("Error: filePath is null")
            return
        }

        job?.cancel()
        handler.removeCallbacksAndMessages(null)
        job = scope.launch(Dispatchers.IO) {
            try {
                mediaPlayer?.release()
                println("setDataSource file path: $mediaItem.filePath")
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(mediaItem.filePath)
                    prepare()
                    start()
                }
                withContext(Dispatchers.Main) {
                    println("Music started, duration: ${mediaPlayer?.duration}")
                    _isPlaying.value = true
                    _duration.value = mediaPlayer?.duration ?: 0
                }

                mediaPlayer?.setOnCompletionListener {
                    handler.post {
                        _isPlaying.value = false
                        _progress.value = 0
                    }
                    handler.removeCallbacksAndMessages(null)
                }

                // Handler를 사용한 진행 상태 업데이트
                updateProgressRunnable = object : Runnable {
                    override fun run() {
                        val position = mediaPlayer?.currentPosition ?: 0
                        println("Handler updating: position=$position, isPlaying=${mediaPlayer?.isPlaying}")
                        _progress.value = position
                        if (mediaPlayer?.isPlaying == true) {
                            handler.postDelayed(this, 500)
                        }
                    }
                }.also {
                    handler.post(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    println("Error playing music: ${e.message}")
                    _isPlaying.value = false
                }
            }
        }
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
        _isPlaying.value = false
        handler.removeCallbacksAndMessages(null)
    }

    fun resumeMusic() {
        mediaPlayer?.start()
        _isPlaying.value = true
        updateProgressRunnable?.let { handler.post(it) }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _progress.value = position
    }

    fun release() {
        job?.cancel()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _progress.value = 0
        _duration.value = 0
    }
}