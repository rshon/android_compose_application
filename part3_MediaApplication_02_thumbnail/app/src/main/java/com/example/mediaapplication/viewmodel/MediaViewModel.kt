package com.example.mediaapplication.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapplication.data.model.MediaItem
import com.example.mediaapplication.data.repository.MediaRepository
import kotlinx.coroutines.launch

class MediaViewModel(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    val mediaItems = mutableStateOf<List<MediaItem>>(emptyList())
    val hasPermissions = mutableStateOf(false)

    fun requestPermissions(context: Context, permissionsGranted: (Boolean) -> Unit) {
        permissionsGranted(hasPermissions.value)
    }

    fun loadMediaContent(context: Context) {
        viewModelScope.launch {
            if (hasPermissions.value) {
                mediaItems.value = mediaRepository.loadMediaContent(context)
            }
        }
    }
}