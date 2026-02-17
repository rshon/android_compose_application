package com.example.mediaapplication.di


import com.example.mediaapplication.data.player.AndroidMediaPlayerPort
import com.example.mediaapplication.data.player.AndroidVideoPlayerPort
import com.example.mediaapplication.domain.port.MediaPlayerPort
import com.example.mediaapplication.domain.port.VideoPlayerPort
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {
    @Binds
    @Singleton
    abstract fun bindMediaPlayerPort(
        impl: AndroidMediaPlayerPort
    ): MediaPlayerPort

    @Binds
    @Singleton
    abstract fun bindVideoPlayerPort(
        impl: AndroidVideoPlayerPort
    ): VideoPlayerPort
}