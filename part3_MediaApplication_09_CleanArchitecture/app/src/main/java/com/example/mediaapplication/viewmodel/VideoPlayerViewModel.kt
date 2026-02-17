package com.example.mediaapplication.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mediaapplication.constants.MediaConstants
import com.example.mediaapplication.constants.PlayerConstants
import com.example.mediaapplication.data.player.PlayerEngineType
import com.example.mediaapplication.di.VideoPlayerEngineType
import com.example.mediaapplication.domain.model.PlaybackState
import com.example.mediaapplication.domain.model.PlayerState
import com.example.mediaapplication.domain.model.VideoSurfaceFactory
import com.example.mediaapplication.domain.port.VideoPlayerEventListener
import com.example.mediaapplication.domain.usecase.NavigateMediaUseCase
import com.example.mediaapplication.domain.usecase.VideoPlayUseCase
import com.example.mediaapplication.ui.player.VideoPlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val videoPlayUseCase: VideoPlayUseCase,
    private val surfaceFactory: VideoSurfaceFactory,
    navigateUseCase: NavigateMediaUseCase,
    @VideoPlayerEngineType val playerType: PlayerEngineType
) : BaseMediaViewModel(navigateUseCase), VideoPlayerEventListener {

    companion object {
        private const val TAG = "VideoPlayerViewModel"
    }

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()
    private var positionUpdateJob: Job? = null

    override fun onPlaybackStateChanged(state: PlaybackState) {
        Log.d(TAG, "Playback state changed: $state")

        when (state) {
            PlaybackState.BUFFERING -> {
                Log.d(TAG, "Player buffering - showing loading")
                updateUiState { it.copy(isLoading = true, error = null) }
            }
            PlaybackState.READY -> {
                Log.d(TAG, "Player ready - hiding loading")
                updateUiState { it.copy(isLoading = false, error = null) }

                updatePlayerState()
                handlePlaybackStateUpdate(videoPlayUseCase.isPlaying())
            }
            PlaybackState.ENDED -> {
                Log.d(TAG, "Player ended")
                updateUiState { it.copy(isLoading = false) }
                updatePlayerState()
                stopPositionUpdates()
            }
            PlaybackState.IDLE -> {
                Log.d(TAG, "Player idle")
                stopPositionUpdates()
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.d(TAG, "Is playing changed: $isPlaying")
        updatePlayerState()
        handlePlaybackStateUpdate(isPlaying)
    }

    override fun onPositionDiscontinuity(oldPosition: Long, newPosition: Long) {
        Log.d(TAG, "Position discontinuity: ${oldPosition} -> ${newPosition}")
        updatePlayerState()
    }

    override fun onPlayerError(message: String, cause: Throwable?) {
        Log.e(TAG, "onPlayerError: ${message}")
        stopPositionUpdates()
        updatePlayerState()

        updateUiState {
            it.copy(
                isLoading = false,
                error = message
            )
        }

        viewModelScope.launch {
            showToast("Player Error: $message")
        }
    }

    override fun onVideoSizeChanged(width: Int, height: Int) {
        Log.d(TAG, "Video Resolution: $width x $height")
        updateUiState { currentState ->
            currentState.copy(videoSize = Pair(width, height))
        }
    }

    init {
        Log.d(TAG, "+init:")

        videoPlayUseCase.setPlayerType(playerType)
        videoPlayUseCase.addEventListener(this)

        setFilter(NavigateMediaUseCase.MediaTypeFilter.VIDEO)
    }

    private fun initializePlaybackState(uri: String) {
        updateUiState {
            it.copy(
                currentUri = uri,
                isLoading = true,
                error = null,
                playerState = PlayerState(0L, 0L, false)
            )
        }
        stopPositionUpdates()
    }

    private fun updateMediaIndex(uri: String) {
        val index = mediaItems.value.indexOfFirst { it.filePath == uri }
        if (index != MediaConstants.INVALID_INDEX) {
            updateCurrentIndex(mediaItems.value[index])
        }
    }

    private fun startPlayback(uri: String, scope: CoroutineScope) {
        viewModelScope.launch {
            try {
                videoPlayUseCase.play(uri, scope)
                updatePlayerState()
                startPositionUpdates()
            } catch (e: Exception) {
                Log.e(TAG, "Error starting playback", e)
                updateUiState {
                    it.copy(
                        isLoading = false,
                        error = "Playback Error: ${e.message}",
                        playerState = PlayerState(0L, 0L, false)
                    )
                }
            }
        }
    }

    private fun handlePlaybackStateUpdate(isPlaying: Boolean) {
        updatePlayerState()
        if (isPlaying) {
            startPositionUpdates()
        } else {
            stopPositionUpdates()
        }
    }

    private fun updatePlayerState() {
        val currentPosition = videoPlayUseCase.getCurrentPosition()
        val duration = videoPlayUseCase.getDuration()
        val isPlaying = videoPlayUseCase.isPlaying()

//        Log.d(TAG, "currentPosition: $currentPosition, isPlaying: $isPlaying")

        val currentPlayerState = _uiState.value.playerState

        if (currentPlayerState.currentPosition != currentPosition ||
            currentPlayerState.duration != duration ||
            currentPlayerState.isPlaying != isPlaying) {

            val newPlayerState = PlayerState(currentPosition, duration, isPlaying)
            updateUiState { it.copy(playerState = newPlayerState) }
        }
    }

    private inline fun updateUiState(update: (VideoPlayerUiState) -> VideoPlayerUiState) {
        _uiState.value = update(_uiState.value)
    }

    private fun getUpdateInterval(): Long {
        return if (videoPlayUseCase.isPlaying()) {
            PlayerConstants.POSITION_UPDATE_INTERVAL_PLAYING
        } else {
            PlayerConstants.POSITION_UPDATE_INTERVAL_PAUSED
        }
    }

    @OptIn(FlowPreview::class)
    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch(Dispatchers.Main) {
            flow {
                while (currentCoroutineContext().isActive) {
                    emit(Unit)
                    delay(getUpdateInterval())
                }
            }
            .sample(PlayerConstants.SAMPLES_PER_PROGRESS_BAR_UPDATE)
            .collect { updatePlayerState() }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    fun play(uri: String, scope: CoroutineScope = viewModelScope) {
        require(uri.isNotBlank()) { "URI cannot be empty" }
        Log.d(TAG, "+play: $uri")

        updateUiState {
            it.copy(
                playerState = PlayerState(0L, 0L, false),
                isLoading = true,
                error = null
            )
        }

        videoPlayUseCase.pause()
        stopPositionUpdates()

        initializePlaybackState(uri)
        updateMediaIndex(uri)
        startPlayback(uri, viewModelScope)
    }

    fun pause() {
        Log.d(TAG, "+pause")
        videoPlayUseCase.pause()
        updatePlayerState()
        stopPositionUpdates()
    }

    fun resume() {
        Log.d(TAG, "+resume")
        videoPlayUseCase.resume()
        updatePlayerState()
        startPositionUpdates()
    }

    fun playNext() {
        Log.d(TAG, "Playing next video")

        val newIndex = navigateToNext()
        mediaItems.value.getOrNull(newIndex)?.let { mediaItem ->
            mediaItem.filePath?.let { filePath ->
                play(filePath)
            }
        }
    }

    fun playPrevious() {
        Log.d(TAG, "Playing previous video")

        val newIndex = navigateToPrevious()
        mediaItems.value.getOrNull(newIndex)?.let { mediaItem ->
            mediaItem.filePath?.let { filePath ->
                play(filePath)
            }
        }
    }

    fun seekTo(ms: Long) {
        Log.d(TAG, "+seekTo: $ms")
        videoPlayUseCase.seekTo(ms)
        updatePlayerState()

        if (videoPlayUseCase.isPlaying()) {
            startPositionUpdates()
        }
    }

    fun clearError() {
        updateUiState { it.copy(error = null) }
    }

    fun retry() {
        Log.d(TAG, "+retry")
        _uiState.value.currentUri?.let { uri ->
            clearError()
            play(uri)
        }
    }

    fun getPlayer(): Any = videoPlayUseCase.getPlayer()

    fun setSurfaceFromNative(nativeObject: Any?) {
        val videoSurface = surfaceFactory.createSurface(nativeObject)
        videoPlayUseCase.setSurface(videoSurface)
    }

    override fun onCleared() {
        Log.d(TAG, "+onCleared")

        try {
            stopPositionUpdates()
            videoPlayUseCase.removeEventListener(this)
            videoPlayUseCase.release()
        } catch (e: Exception) {
            Log.w(TAG, "Error during cleanup", e)
        } finally {
            super.onCleared()
        }
    }
}