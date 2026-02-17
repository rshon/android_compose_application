package com.example.mediaapplication.di

import com.example.mediaapplication.data.player.PlayerEngineType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import com.example.mediaapplication.BuildConfig

@Module
@InstallIn(ViewModelComponent::class)
object PlayerEngineTypeModule {

    @Provides
    @VideoPlayerEngineType
    fun providePlayerEngineType(): PlayerEngineType {
        // 설정값, SharedPreferences 등에서 읽어오게 구현 가능
//        return PlayerEngineType.EXO_PLAYER // or MEDIA_PLAYER

        return when (BuildConfig.PLAYER_ENGINE_TYPE.uppercase()) {
            "MEDIA_PLAYER" -> PlayerEngineType.MEDIA_PLAYER
            else -> PlayerEngineType.EXO_PLAYER
        }
    }
}