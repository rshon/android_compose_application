package com.example.mediaapplication.domain.usecase

import com.example.mediaapplication.domain.port.MediaPlayerPort
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayMusicUseCase @Inject constructor(
    private val player: MediaPlayerPort
) {
    operator fun invoke(path: String, scope: CoroutineScope) {
        player.play(path, scope)
    }

    fun pause() = player.pause()
    fun resume() = player.resume()
    fun seekTo(ms: Int) = player.seekTo(ms)
    fun release() = player.release()

    val isPlaying: Flow<Boolean> get() = player.isPlaying
    val progress: Flow<Int> get() = player.progress
    val duration:  Flow<Int> get() = player.duration
}