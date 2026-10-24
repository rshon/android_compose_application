package com.example.mediaapplication.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapplication.data.model.MediaItem
import com.example.mediaapplication.data.repository.MediaRepository
import com.example.mediaapplication.domain.usecase.PlayMusicUseCase
import kotlinx.coroutines.launch

class MediaViewModel(
    private val mediaRepository: MediaRepository,
    private val playMusicUseCase: PlayMusicUseCase
) : ViewModel() {

    val mediaItems = mutableStateOf<List<MediaItem>>(emptyList())
    val hasPermissions = mutableStateOf(false)

    val isPlaying = mutableStateOf(false)
    val progress = mutableStateOf(0)
    val duration = mutableStateOf(0)

    init {
        viewModelScope.launch {
            playMusicUseCase.isPlaying.collect { playing ->
                println("MediaViewModel: isPlaying updated to $playing")
                isPlaying.value = playing
            }
        }

        viewModelScope.launch {
            playMusicUseCase.progress.collect { prog ->
                println("MediaViewModel: progress updated to $prog")
                progress.value = prog
            }
        }

        viewModelScope.launch {
            playMusicUseCase.duration.collect { dur ->
                println("MediaViewModel: duration updated to $dur")
                duration.value = dur
            }
        }
    }

    fun loadMediaContent(context: Context) {
        viewModelScope.launch {
            if (hasPermissions.value) {
                mediaItems.value = mediaRepository.loadMediaContent(context)
            }
        }
    }

    fun playMusic(mediaItem: MediaItem) {
        viewModelScope.launch {
            playMusicUseCase.playMusic(mediaItem, viewModelScope)
        }
    }

    fun pauseMusic() {
        playMusicUseCase.pauseMusic()
    }

    fun resumeMusic() {
        playMusicUseCase.resumeMusic()
    }

    fun seekTo(position: Int) {
        playMusicUseCase.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        playMusicUseCase.release()
    }
}
