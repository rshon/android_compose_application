package com.example.mediaapplication.di

import com.example.mediaapplication.data.player.PlayerEngineType
import com.example.mediaapplication.domain.port.VideoPlayerPort
import javax.inject.Inject
import javax.inject.Singleton
import com.example.mediaapplication.BuildConfig

@Singleton
class VideoPlayerPortFactory @Inject constructor(
    @MediaPlayerPortImpl private val mediaPlayerPort: VideoPlayerPort,
    @ExoPlayerPortImpl private val exoPlayerPort: VideoPlayerPort
) {
    fun getPort(type: PlayerEngineType): VideoPlayerPort {
        return when (type) {
            PlayerEngineType.MEDIA_PLAYER -> mediaPlayerPort
            PlayerEngineType.EXO_PLAYER   -> exoPlayerPort
        }
    }

    fun getPort(): VideoPlayerPort {
        // BuildConfig.PLAYER_ENGINE_TYPE 값 읽기
        val engineType = when (BuildConfig.PLAYER_ENGINE_TYPE.uppercase()) {
            "MEDIA_PLAYER" -> PlayerEngineType.MEDIA_PLAYER
            else -> PlayerEngineType.EXO_PLAYER
        }

        return when (engineType) {
            PlayerEngineType.MEDIA_PLAYER -> mediaPlayerPort
            PlayerEngineType.EXO_PLAYER   -> exoPlayerPort
        }
    }
}