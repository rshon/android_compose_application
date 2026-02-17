package com.example.mediaapplication.domain.port

import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope

interface VideoPlayerPort {
    fun play(uri: String, scope: CoroutineScope)
    fun pause()
    fun resume()
    fun seekTo(ms: Long)
    fun next()
    fun previous()
    fun release()
    fun getExoPlayer(): ExoPlayer
}