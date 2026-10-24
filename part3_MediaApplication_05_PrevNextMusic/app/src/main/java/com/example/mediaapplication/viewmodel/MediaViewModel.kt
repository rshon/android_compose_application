package com.example.mediaapplication.viewmodel

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapplication.data.model.MediaItem
import com.example.mediaapplication.data.model.MediaType
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

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    // 현재 곡 인덱스 상태 추가
    private val currentIndex = mutableStateOf(-1) // 초기값: -1 (선택된 곡 없음)

    val currentMediaItem = derivedStateOf {
        if (currentIndex.value >= 0 && currentIndex.value < mediaItems.value.size) {
            mediaItems.value[currentIndex.value]
        } else {
            null
        }
    }

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
            // 현재 곡 인덱스 업데이트
            val index = mediaItems.value.indexOfFirst { it.filePath == mediaItem.filePath }
            if (index != -1) {
                currentIndex.value = index
            }

            if (mediaItem.type == MediaType.MUSIC) {
                playMusicUseCase.playMusic(mediaItem, viewModelScope)
            } else {
                _toastMessage.value = "No music type! ${mediaItem.type}"
            }
        }
    }

    fun playNext(context: Context) {
        viewModelScope.launch {
            if (mediaItems.value.isEmpty()) return@launch

            var nextIndex = (currentIndex.value + 1) % mediaItems.value.size
            var attempts = 0
            val maxAttempts = mediaItems.value.size

            // 다음 음악 파일을 찾을 때까지 인덱스 이동
            while (attempts < maxAttempts) {
                val nextItem = mediaItems.value[nextIndex]
                if (nextItem.type == MediaType.MUSIC) {
                    currentIndex.value = nextIndex
                    playMusicUseCase.playMusic(nextItem, viewModelScope)
                    return@launch
                }
                nextIndex = (nextIndex + 1) % mediaItems.value.size
                attempts++
            }

            println("No music files found in the list")
        }
    }

    fun playPrevious(context: Context) {
        viewModelScope.launch {
            if (mediaItems.value.isEmpty()) return@launch
            var prevIndex = if (currentIndex.value - 1 < 0) {
                mediaItems.value.size - 1
            } else {
                currentIndex.value - 1
            }
            var attempts = 0
            val maxAttempts = mediaItems.value.size

            while (attempts < maxAttempts) {
                val prevItem = mediaItems.value[prevIndex]
                if (prevItem.type == MediaType.MUSIC) {
                    currentIndex.value = prevIndex
                    playMusicUseCase.playMusic(prevItem, viewModelScope)
                    return@launch
                }
                prevIndex = if (prevIndex - 1 < 0) {
                    mediaItems.value.size - 1
                } else {
                    prevIndex - 1
                }
                attempts++
            }

            println("No music files found in the list")
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
