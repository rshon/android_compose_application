package com.example.mediaapplication.di

import com.example.mediaapplication.domain.model.NativeSurfaceHandler
import com.example.mediaapplication.domain.model.VideoSurfaceFactory
import com.example.mediaapplication.ui.video.AndroidNativeSurfaceHandler
import com.example.mediaapplication.ui.video.AndroidVideoSurfaceFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NativeSurfaceHandlerModule {

    @Provides
    @Singleton
    fun provideNativeSurfaceHandler(
        androidNativeSurfaceHandler: AndroidNativeSurfaceHandler
    ): NativeSurfaceHandler {
        return androidNativeSurfaceHandler
    }

    @Provides
    @Singleton
    fun provideVideoSurfaceFactory(
        androidVideoSurfaceFactory: AndroidVideoSurfaceFactory
    ): VideoSurfaceFactory {
        return androidVideoSurfaceFactory
    }
}