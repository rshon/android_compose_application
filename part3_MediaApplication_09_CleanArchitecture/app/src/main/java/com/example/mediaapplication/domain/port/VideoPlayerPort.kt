package com.example.mediaapplication.domain.port

import com.example.mediaapplication.domain.model.VideoSurface
import kotlinx.coroutines.CoroutineScope

interface VideoPlayerPort {
    fun play(uri: String, scope: CoroutineScope)
    fun play()
    fun pause()
    fun resume()
    fun seekTo(ms: Long)
    fun release()
    fun getPlayWhenReady(): Boolean
    fun setPlayWhenReady()
    fun getNativePlayer(): Any?

    fun getCurrentPlayerPosition(): Long
    fun getTotalDuration(): Long
    fun isCurrentlyPlaying(): Boolean

    fun setSurface(surface: VideoSurface?)
    fun getVideoSize(): Pair<Int, Int>

    fun addEventListener(listener: VideoPlayerEventListener)
    fun removeEventListener(listener: VideoPlayerEventListener)
}