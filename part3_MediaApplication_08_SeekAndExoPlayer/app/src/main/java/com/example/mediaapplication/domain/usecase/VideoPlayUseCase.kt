package com.example.mediaapplication.domain.usecase

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediaapplication.di.VideoPlayerPortFactory
import com.example.mediaapplication.domain.port.VideoPlayerPort
import com.example.mediaapplication.viewmodel.PlayerEngineType
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class VideoPlayUseCase @Inject constructor(
//    private val port: VideoPlayerPort
    private val portFactory: VideoPlayerPortFactory
) {
    private var port: VideoPlayerPort? = null

    fun setPlayerType(type: PlayerEngineType) {
        port = portFactory.getPort(type)
    }

    fun play(uri: String, scope: CoroutineScope) = port?.play(uri, scope)
    fun play() = port?.play()
    fun pause() = port?.pause()
    fun resume() = port?.resume()
    fun seekTo(ms: Long) = port?.seekTo(ms)
    fun next() = port?.next()
    fun previous() = port?.previous()
    fun release() = port?.release()

    fun getPlayer(): ExoPlayer = port?.getExoPlayer()!!

    fun addListener(listener: Player.Listener) = port?.addPlayerListener(listener)
    fun removeListener(listener: Player.Listener) = port?.removePlayerListener(listener)

    fun getCurrentPosition(): Long = port?.getCurrentPlayerPosition()!!
    fun getDuration(): Long = port?.getTotalDuration()!!
    fun isPlaying(): Boolean = port?.isCurrentlyPlaying()!!

    fun getPlayWhenReady() = port?.getPlayWhenReady()
    fun setPlayWhenReady() = port?.setPlayWhenReady()

    fun setSurface(surface: android.view.Surface?) = port?.setSurface(surface)

    fun getVideoSize(): Pair<Int, Int> = port?.getVideoSize()!!

    fun addVideoSizeListener(listener: (width: Int, height: Int) -> Unit) = port?.addVideoSizeListener(listener)
    fun removeVideoSizeListener(listener: (width: Int, height: Int) -> Unit) = port?.removeVideoSizeListener(listener)
}
