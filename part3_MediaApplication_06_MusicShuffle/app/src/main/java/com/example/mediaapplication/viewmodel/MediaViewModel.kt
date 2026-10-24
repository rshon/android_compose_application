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
import kotlin.random.Random

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

    val isShuffled = mutableStateOf(false)

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
            if (mediaItems.value.isEmpty()) {
                _toastMessage.value = "No media items available"
                return@launch
            }

            val musicItems = mediaItems.value.filter { it.type == MediaType.MUSIC }
            if (musicItems.isEmpty()) {
                _toastMessage.value = "No music files found in the list"
                return@launch
            }

            val musicIndices = mediaItems.value.indices.filter { mediaItems.value[it].type == MediaType.MUSIC }
            if (musicIndices.isEmpty()) {
                _toastMessage.value = "No music files found in the list"
                return@launch
            }

            val currentMusicIndex = musicIndices.indexOf(currentIndex.value)
            val nextIndex = if (isShuffled.value) {
                // 셔플 모드: 음악 파일 중에서 랜덤 인덱스 선택
                var randomMusicIndex: Int
                do {
                    randomMusicIndex = Random.nextInt(musicIndices.size)
                } while (randomMusicIndex == currentMusicIndex && musicIndices.size > 1)
                musicIndices[randomMusicIndex]
            } else {
                // 순차 모드: 다음 음악 파일 찾기
                val nextMusicIndex = (currentMusicIndex + 1) % musicIndices.size
                musicIndices[nextMusicIndex]
            }

            currentIndex.value = nextIndex
            val nextItem = mediaItems.value[nextIndex]
            if (nextItem.type == MediaType.MUSIC) {
                playMusicUseCase.playMusic(nextItem, viewModelScope)
            } else {
                _toastMessage.value = "Cannot play: Not a music file! Type: ${nextItem.type}"
            }
        }
    }

    fun playPrevious(context: Context) {
        viewModelScope.launch {
            if (mediaItems.value.isEmpty()) {
                _toastMessage.value = "No media items available"
                return@launch
            }

            val musicIndices = mediaItems.value.indices.filter { mediaItems.value[it].type == MediaType.MUSIC }
            if (musicIndices.isEmpty()) {
                _toastMessage.value = "No music files found in the list"
                return@launch
            }

            val currentMusicIndex = musicIndices.indexOf(currentIndex.value)
            val prevMusicIndex = if (currentMusicIndex - 1 < 0) {
                musicIndices.size - 1
            } else {
                currentMusicIndex - 1
            }

            val prevIndex = musicIndices[prevMusicIndex]
            currentIndex.value = prevIndex
            val prevItem = mediaItems.value[prevIndex]
            if (prevItem.type == MediaType.MUSIC) {
                playMusicUseCase.playMusic(prevItem, viewModelScope)
            } else {
                _toastMessage.value = "Cannot play: Not a music file! Type: ${prevItem.type}"
            }
        }
    }

    fun toggleShuffle() {
        isShuffled.value = !isShuffled.value
        println("Shuffle mode: ${if (isShuffled.value) "ON" else "OFF"}")
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
