package com.example.mediaapplication.domain.port

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope

interface VideoPlayerPort {
    fun play(uri: String, scope: CoroutineScope)
    fun play()
    fun pause()
    fun resume()
    fun seekTo(ms: Long)
    fun next()
    fun previous()
    fun release()
    fun getPlayWhenReady(): Boolean
    fun setPlayWhenReady()
    fun getExoPlayer(): ExoPlayer

    fun addPlayerListener(listener: Player.Listener)
    fun removePlayerListener(listener: Player.Listener)

    fun getCurrentPlayerPosition(): Long
    fun getTotalDuration(): Long
    fun isCurrentlyPlaying(): Boolean

    fun setSurface(surface: android.view.Surface?)
    fun getVideoSize(): Pair<Int, Int>

    fun addVideoSizeListener(listener: (width: Int, height: Int) -> Unit)
    fun removeVideoSizeListener(listener: (width: Int, height: Int) -> Unit)
}