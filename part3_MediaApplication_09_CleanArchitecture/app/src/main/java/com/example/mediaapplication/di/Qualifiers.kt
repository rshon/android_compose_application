package com.example.mediaapplication.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MediaPlayerPortImpl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExoPlayerPortImpl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VideoPlayerEngineType