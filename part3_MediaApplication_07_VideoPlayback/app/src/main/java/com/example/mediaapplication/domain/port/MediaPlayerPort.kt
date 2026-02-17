package com.example.mediaapplication.domain.port

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface MediaPlayerPort {
    fun play(path: String, scope: CoroutineScope)
    fun pause()
    fun resume()
    fun seekTo(ms: Int)
    fun release()

    val isPlaying: Flow<Boolean>
    val progress: Flow<Int>
    val duration: Flow<Int>
}