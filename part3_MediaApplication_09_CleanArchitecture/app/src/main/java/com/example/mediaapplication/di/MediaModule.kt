package com.example.mediaapplication.di

import android.content.Context
import com.example.mediaapplication.data.datasource.MediaDataSource
import com.example.mediaapplication.data.datasource.external.ExternalStorageMediaDataSource
import com.example.mediaapplication.data.repository.MediaRepositoryImpl
import com.example.mediaapplication.domain.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {
    @Provides
    @Singleton
    fun provideMediaDataSource(
    ): MediaDataSource {
        return ExternalStorageMediaDataSource()
    }

    @Provides
    @Singleton
    fun provideMediaRepository(
        mediaDataSource: MediaDataSource
    ): MediaRepository =
        MediaRepositoryImpl(mediaDataSource)
}