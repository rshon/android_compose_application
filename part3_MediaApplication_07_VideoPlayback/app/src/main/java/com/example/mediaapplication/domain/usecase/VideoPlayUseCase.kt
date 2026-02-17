package com.example.mediaapplication.domain.usecase

import com.example.mediaapplication.domain.port.VideoPlayerPort
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class VideoPlayUseCase @Inject constructor(
    private val port: VideoPlayerPort
) {
    fun play(uri: String, scope: CoroutineScope) = port.play(uri, scope)
    fun pause()   = port.pause()
    fun resume()  = port.resume()
    fun seekTo(ms: Long) = port.seekTo(ms)
    fun next()    = port.next()
    fun previous()= port.previous()
    fun release() = port.release()
}