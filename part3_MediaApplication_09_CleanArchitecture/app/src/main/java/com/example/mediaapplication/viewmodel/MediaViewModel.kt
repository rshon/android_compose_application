package com.example.mediaapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.mediaapplication.domain.model.MediaItem
import com.example.mediaapplication.domain.model.MediaType
import com.example.mediaapplication.domain.usecase.NavigateMediaUseCase
import com.example.mediaapplication.domain.usecase.PlayMusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val playMusicUseCase: PlayMusicUseCase,
    navigateUseCase: NavigateMediaUseCase
) : BaseMediaViewModel(navigateUseCase) {

    companion object {
        private const val TAG = "MediaViewModel"
    }

    val isPlaying = mutableStateOf(false)
    val progress = mutableStateOf(0)
    val duration = mutableStateOf(0)

    init {
        viewModelScope.launch {
            playMusicUseCase.isPlaying.collect { playing ->
                println("MediaViewModel: update playing status $playing")
                isPlaying.value = playing
            }
        }

        viewModelScope.launch {
            playMusicUseCase.progress.collect { prog ->
                progress.value = prog
            }
        }

        viewModelScope.launch {
            playMusicUseCase.duration.collect { dur ->
                println("MediaViewModel: update duration $dur")
                duration.value = dur
            }
        }

        setFilter(NavigateMediaUseCase.MediaTypeFilter.MUSIC)
    }

    fun playMusic(mediaItem: MediaItem) {
        viewModelScope.launch {
            updateCurrentIndex(mediaItem)

            if (mediaItem.type == MediaType.MUSIC && mediaItem.filePath != null) {
                playMusicUseCase(mediaItem.filePath, viewModelScope)
            } else {
                showToast("Not a music type! ${mediaItem.type}")
            }
        }
    }

    fun playNext() {
        val newIndex = navigateToNext()
        mediaItems.value.getOrNull(newIndex)?.let { playMusic(it) }
    }

    fun playPrevious() {
        val newIndex = navigateToPrevious()
        mediaItems.value.getOrNull(newIndex)?.let { playMusic(it) }
    }

    fun pauseMusic() {
        playMusicUseCase.pause()
    }

    fun resumeMusic() {
        playMusicUseCase.resume()
    }

    fun seekTo(position: Int) {
        playMusicUseCase.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        playMusicUseCase.release()
    }
}