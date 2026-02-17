package com.example.mediaapplication.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mediaapplication.constants.MediaConstants
import com.example.mediaapplication.domain.model.MediaItem
import com.example.mediaapplication.domain.usecase.NavigateMediaUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class BaseMediaViewModel(
    protected val navigateUseCase: NavigateMediaUseCase
) : ViewModel() {

    val mediaItems = mutableStateOf<List<MediaItem>>(emptyList())
    private val currentIndex = mutableStateOf(MediaConstants.INVALID_INDEX)
    val isShuffled = mutableStateOf(navigateUseCase.isShuffled)

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    val currentMediaItem = derivedStateOf {
        if (currentIndex.value >= 0 && currentIndex.value < mediaItems.value.size) {
            mediaItems.value[currentIndex.value]
        } else {
            null
        }
    }

    fun setMediaItems(items: List<MediaItem>) {
        mediaItems.value = items
        println("${this::class.simpleName}: item size = ${items.size}")
    }

    fun setFilter(filter: NavigateMediaUseCase.MediaTypeFilter) {
        navigateUseCase.setFilter(filter)
    }

    fun toggleShuffle() {
        val newShuffle = !navigateUseCase.isShuffled
        println("shuffle mode: ${if (newShuffle) "ON" else "OFF"}")
        navigateUseCase.setShuffle(newShuffle)
        isShuffled.value = newShuffle
    }

    protected fun navigateToNext(): Int {
        val newIndex = navigateUseCase(
            items = mediaItems.value,
            currentIndex = currentIndex.value,
            direction = NavigateMediaUseCase.Direction.NEXT
        )
        currentIndex.value = newIndex
        return newIndex
    }

    protected fun navigateToPrevious(): Int {
        val newIndex = navigateUseCase(
            items = mediaItems.value,
            currentIndex = currentIndex.value,
            direction = NavigateMediaUseCase.Direction.PREVIOUS
        )
        currentIndex.value = newIndex
        return newIndex
    }

    protected fun updateCurrentIndex(mediaItem: MediaItem) {
        val index = mediaItems.value.indexOfFirst { it.filePath == mediaItem.filePath }
        if (index != -1) {
            currentIndex.value = index
        }
    }

    protected suspend fun showToast(message: String) {
        _toastMessage.emit(message)
    }
}