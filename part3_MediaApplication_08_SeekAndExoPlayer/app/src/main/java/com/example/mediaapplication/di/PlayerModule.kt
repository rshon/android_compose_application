package com.example.mediaapplication.di


import com.example.mediaapplication.data.player.AndroidMediaPlayerPort
import com.example.mediaapplication.data.player.AndroidVideoPlayerPort
import com.example.mediaapplication.data.player.MediaPlayerVideoPort
import com.example.mediaapplication.domain.port.MediaPlayerPort
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
        impl: AndroidMediaPlayerPort
    ): MediaPlayerPort

    // 1) MediaPlayer 기반 Port 바인딩 (Qualifier: @MediaPlayerPortImpl)
    @Binds
    @Singleton
    @MediaPlayerPortImpl
    abstract fun bindMediaPlayerVideoPort(
        impl: MediaPlayerVideoPort
    ): VideoPlayerPort

    // 2) ExoPlayer 기반 Port 바인딩 (Qualifier: @ExoPlayerPortImpl)
    @Binds
    @Singleton
    @ExoPlayerPortImpl
    abstract fun bindExoVideoPlayerPort(
        impl: AndroidVideoPlayerPort
    ): VideoPlayerPort

    // 3) “런타임에 조건에 따라” VideoPlayerPort를 선택해 주입하기 위한 Provides 메서드
    companion object {
        @Provides
        @Singleton
        fun provideVideoPlayerPort(
            // Qualifier별로 두 구현체를 모두 주입받아서
            @MediaPlayerPortImpl mediaImpl: VideoPlayerPort,
            @ExoPlayerPortImpl exoImpl: VideoPlayerPort
        ): VideoPlayerPortFactory {
            // VideoPlayerPortFactory를 통해 런타임에 선택할 수 있도록 감싸서 반환
            return VideoPlayerPortFactory(mediaImpl, exoImpl)
        }
    }
}