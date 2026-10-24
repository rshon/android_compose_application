package com.example.mediaapplication.di

import com.example.mediaapplication.data.repository.MediaRepositoryImpl
import com.example.mediaapplication.domain.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {
//    @Provides
//    @Singleton
//    fun provideExoPlayer(@ApplicationContext ctx: Context): ExoPlayer =
//        ExoPlayer.Builder(ctx).build()
//
//    @Provides
//    @Singleton
//    fun providePlayerManager(player: ExoPlayer): PlayerManager =
//        PlayerManager(player)
//
//    @Provides
//    @Singleton
//    fun provideDataSource(@ApplicationContext ctx: Context): MediaStoreDataSource =
//        MediaStoreDataSource(ctx)

    @Provides
    @Singleton
    fun provideRepository(): MediaRepository =
        MediaRepositoryImpl()

//    @Provides
//    @Singleton
//    fun providePlayMusicUseCase(): PlayMusicUseCase =
//        PlayMusicUseCase()
}
