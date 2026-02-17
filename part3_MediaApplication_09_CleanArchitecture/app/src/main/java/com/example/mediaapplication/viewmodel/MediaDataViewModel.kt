package com.example.mediaapplication.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapplication.domain.model.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.mediaapplication.domain.usecase.LoadMediaContentUseCase

@HiltViewModel
class MediaDataViewModel @Inject constructor(
    private val loadMediaContentUseCase: LoadMediaContentUseCase,
) : ViewModel() {

    val mediaItems = mutableStateOf<List<MediaItem>>(emptyList())
    val hasPermissions = mutableStateOf(false)

    fun loadMediaContent() {
        println("+loadMediaContent")

        viewModelScope.launch {
            println("hasPermissions.value = ${hasPermissions.value}")

            if (hasPermissions.value) {
                mediaItems.value = loadMediaContentUseCase()
            }

            println("mediaItems.value.size = ${mediaItems.value.size}")
        }
    }
}