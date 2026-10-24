package com.example.mediaapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediaapplication.domain.usecase.VideoPlayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerState(
    val currentPosition: Long, // current position in ms
    val duration: Long,        // total duration in ms
    val isPlaying: Boolean     // playing status
)

enum class PlayerEngineType {
    EXO_PLAYER,
    MEDIA_PLAYER
}

private const val TAG = "VideoPlayerViewModel"

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val videoPlayUseCase: VideoPlayUseCase,
) : ViewModel() {

    private var currentUri: String? = null

    // change DP module as well
    val playerType: PlayerEngineType = PlayerEngineType.EXO_PLAYER

    private val _playerState = MutableStateFlow(PlayerState(0L, 0L, false))
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var positionUpdateJob: Job? = null

    private val _videoSize = MutableStateFlow(Pair(0, 0))
    val videoSize: StateFlow<Pair<Int, Int>> = _videoSize.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            updatePlayerState()

            if (playbackState == Player.STATE_READY && videoPlayUseCase.isPlaying()) {
                startPositionUpdates()
            } else {
                stopPositionUpdates()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayerState()

            if (isPlaying) {
                startPositionUpdates()
            } else {
                stopPositionUpdates()
            }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            Log.d(TAG, "Position discontinuity: ${oldPosition.positionMs} -> ${newPosition.positionMs}, reason: $reason")
            updatePlayerState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updatePlayerState()
        }

        override fun onPlayerError(error: PlaybackException) {
            stopPositionUpdates()
            updatePlayerState() // 에러 상태도 UI에 반영할 수 있도록
            Log.e(TAG, "Player Error: ${error.message}", error)
        }
    }

    init {
        videoPlayUseCase.setPlayerType(playerType)
        videoPlayUseCase.addListener(playerListener)

        videoPlayUseCase.addVideoSizeListener { width, height ->
            Log.d(TAG, "width, height = $width, $height")
            _videoSize.value = Pair(width, height)
        }
    }

    private fun updatePlayerState() {
        _playerState.value = PlayerState(
            currentPosition = videoPlayUseCase.getCurrentPosition(),
            duration = videoPlayUseCase.getDuration(),
            isPlaying = videoPlayUseCase.isPlaying()
        )
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                updatePlayerState()
                delay(1000)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    fun play(uri: String, scope: CoroutineScope) {
        currentUri = uri
        videoPlayUseCase.play(uri, scope)
        updatePlayerState()
        startPositionUpdates()
    }

    fun pause() {
        videoPlayUseCase.pause()
        updatePlayerState()
        stopPositionUpdates()
    }

    fun resume() {
        videoPlayUseCase.resume()
        updatePlayerState()
        startPositionUpdates()
    }

    fun next() {
        videoPlayUseCase.next()
        updatePlayerState()
        startPositionUpdates()
    }

    fun previous() {
        videoPlayUseCase.previous()
        updatePlayerState()
        startPositionUpdates()
    }

    fun seekTo(ms: Long) {
        videoPlayUseCase.seekTo(ms)
        updatePlayerState()

        if (videoPlayUseCase.isPlaying()) {
            startPositionUpdates()
        }
    }

    fun getPlayer(): ExoPlayer = videoPlayUseCase.getPlayer()

    override fun onCleared() {
        super.onCleared()
        videoPlayUseCase.removeListener(playerListener)
        stopPositionUpdates()
        videoPlayUseCase.release()
    }

    fun setSurface(surface: android.view.Surface?) {
        videoPlayUseCase.setSurface(surface)
    }

    fun getVideoSize(): Pair<Int, Int> = _videoSize.value
}
