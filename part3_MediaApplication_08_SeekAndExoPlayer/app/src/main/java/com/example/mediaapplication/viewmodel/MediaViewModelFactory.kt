package com.example.mediaapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
//import com.example.mediaapplication.data.repository.MediaRepository
import com.example.mediaapplication.domain.usecase.PlayMusicUseCase

// disabled due to MediaModule
//class MediaViewModelFactory(
//    private val playMusicUseCase: PlayMusicUseCase
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return MediaViewModel(mediaRepository, playMusicUseCase) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
