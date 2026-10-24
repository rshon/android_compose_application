package com.example.mediaapplication.di

import com.example.mediaapplication.domain.port.VideoPlayerPort
import com.example.mediaapplication.viewmodel.PlayerEngineType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoPlayerPortFactory @Inject constructor(
    @MediaPlayerPortImpl private val mediaPlayerPort: VideoPlayerPort,
    @ExoPlayerPortImpl private val exoPlayerPort: VideoPlayerPort
) {
    /**
     * playerType 에 따라 알맞은 VideoPlayerPort 구현체를 반환.
     */
    fun getPort(type: PlayerEngineType): VideoPlayerPort {
        return when (type) {
            PlayerEngineType.MEDIA_PLAYER -> mediaPlayerPort
            PlayerEngineType.EXO_PLAYER -> exoPlayerPort
        }
    }
}
