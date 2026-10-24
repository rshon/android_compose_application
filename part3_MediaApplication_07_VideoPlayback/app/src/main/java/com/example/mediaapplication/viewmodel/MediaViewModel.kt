package com.example.mediaapplication.viewmodel

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapplication.domain.MediaRepository
import com.example.mediaapplication.domain.model.MediaItem
import com.example.mediaapplication.domain.model.MediaType
import com.example.mediaapplication.domain.usecase.PlayMusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val playMusicUseCase: PlayMusicUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "MediaViewModel"
    }

    val mediaItems = mutableStateOf<List<MediaItem>>(emptyList())
    val hasPermissions = mutableStateOf(false)

    val isPlaying = mutableStateOf(false)
    val progress = mutableStateOf(0)
    val duration = mutableStateOf(0)

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val currentIndex = mutableStateOf(-1)

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
//                println("MediaViewModel: progress updated to $prog")
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
        println("+loadMediaContent")

        viewModelScope.launch {
            println("hasPermissions.value = ${hasPermissions.value}")

            if (hasPermissions.value) {
                mediaItems.value = mediaRepository.loadMediaContent(context)
            }

            println("mediaItems.value.size = ${mediaItems.value.size}")
        }
    }

    fun playMusic(mediaItem: MediaItem) {
        viewModelScope.launch {
            // updates current index
            val index = mediaItems.value.indexOfFirst { it.filePath == mediaItem.filePath }
            if (index != -1) {
                currentIndex.value = index
            }

            if (mediaItem.type == MediaType.MUSIC && mediaItem.filePath != null) {
                playMusicUseCase(mediaItem.filePath, viewModelScope)
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
                var randomMusicIndex: Int
                do {
                    randomMusicIndex = Random.nextInt(musicIndices.size)
                } while (randomMusicIndex == currentMusicIndex && musicIndices.size > 1)
                musicIndices[randomMusicIndex]
            } else {
                val nextMusicIndex = (currentMusicIndex + 1) % musicIndices.size
                musicIndices[nextMusicIndex]
            }

            currentIndex.value = nextIndex
            val nextItem = mediaItems.value[nextIndex]
            if (nextItem.type == MediaType.MUSIC && nextItem.filePath != null) {
                playMusicUseCase(nextItem.filePath, viewModelScope)
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

            val prevIndex = if (isShuffled.value) {
                var randomMusicIndex: Int
                do {
                    randomMusicIndex = Random.nextInt(musicIndices.size)
                } while (randomMusicIndex == currentMusicIndex && musicIndices.size > 1)
                musicIndices[randomMusicIndex]
            } else {
                val prevMusicIndex = (currentMusicIndex - 1 + musicIndices.size) % musicIndices.size
                musicIndices[prevMusicIndex]
            }

            currentIndex.value = prevIndex
            val prevItem = mediaItems.value[prevIndex]
            if (prevItem.type == MediaType.MUSIC && prevItem.filePath != null) {
                playMusicUseCase(prevItem.filePath, viewModelScope)
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
