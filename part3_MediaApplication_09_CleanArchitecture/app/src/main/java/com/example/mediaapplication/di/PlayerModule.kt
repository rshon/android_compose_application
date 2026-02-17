package com.example.mediaapplication.di

import com.example.mediaapplication.data.player.MusicPlayerAdapter
import com.example.mediaapplication.data.player.ExoPlayerVideoAdapter
import com.example.mediaapplication.data.player.MediaPlayerVideoAdapter
import com.example.mediaapplication.domain.port.MusicPlayerPort
import com.example.mediaapplication.domain.port.VideoPlayerPort
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {
    @Binds
    @Singleton
    abstract fun bindMediaPlayerPort(
        impl: MusicPlayerAdapter
    ): MusicPlayerPort

    // port binding for MediaPlayer (Qualifier: @MediaPlayerPortImpl)
    @Binds
    @Singleton
    @MediaPlayerPortImpl
    abstract fun bindMediaPlayerVideoPort(
        impl: MediaPlayerVideoAdapter
    ): VideoPlayerPort

    // port binding for ExoPlayer (Qualifier: @ExoPlayerPortImpl)
    @Binds
    @Singleton
    @ExoPlayerPortImpl
    abstract fun bindExoVideoPlayerPort(
        impl: ExoPlayerVideoAdapter
    ): VideoPlayerPort

    // Provides VideoPlayerPortFactory
    companion object {
        @Provides
        @Singleton
        fun provideVideoPlayerPort(
            @MediaPlayerPortImpl mediaImpl: VideoPlayerPort,
            @ExoPlayerPortImpl exoImpl: VideoPlayerPort
        ): VideoPlayerPortFactory {
            return VideoPlayerPortFactory(mediaImpl, exoImpl)
        }
    }
}