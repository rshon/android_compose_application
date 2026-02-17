package com.example.mediaapplication.domain.usecase

import com.example.mediaapplication.data.player.PlayerEngineType
import com.example.mediaapplication.di.VideoPlayerPortFactory
import com.example.mediaapplication.domain.model.VideoSurface
import com.example.mediaapplication.domain.port.VideoPlayerEventListener
import com.example.mediaapplication.domain.port.VideoPlayerPort
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class VideoPlayUseCase @Inject constructor(
    private val portFactory: VideoPlayerPortFactory
) {
    private var port: VideoPlayerPort? = null

    fun setPlayerType(type: PlayerEngineType) {
        port = portFactory.getPort(type)
    }

    fun play(uri: String, scope: CoroutineScope) = port?.play(uri, scope)
    fun play() = port?.play()
    fun pause()   = port?.pause()
    fun resume()  = port?.resume()
    fun seekTo(ms: Long) = port?.seekTo(ms)
    fun release() = port?.release()

    fun getPlayer(): Any = port?.getNativePlayer()!!

    fun getCurrentPosition(): Long = port?.getCurrentPlayerPosition() ?: 0L
    fun getDuration(): Long = port?.getTotalDuration() ?: 0L
    fun isPlaying(): Boolean = port?.isCurrentlyPlaying() ?: false

    fun setSurface(surface: VideoSurface?) = port?.setSurface(surface)

    fun addEventListener(listener: VideoPlayerEventListener) =
        port?.addEventListener(listener)
    fun removeEventListener(listener: VideoPlayerEventListener) =
        port?.removeEventListener(listener)
}