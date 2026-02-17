package com.example.mediaapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediaapplication.domain.port.VideoPlayerPort
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
    val currentPosition: Long, // 현재 재생 위치 (ms)
    val duration: Long,        // 총 길이 (ms)
    val isPlaying: Boolean     // 재생 중 여부
)

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val videoPlayUseCase: VideoPlayUseCase,
    private val videoPlayerPort : VideoPlayerPort
) : ViewModel() {

    private var currentUri: String? = null

    private val _playerState = MutableStateFlow(PlayerState(0L, 0L, false))
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var positionUpdateJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            // 재생 상태 변경 시 호출 (재생 중, 일시정지 등)
            updatePlayerState()

            if (playbackState == Player.STATE_READY && videoPlayerPort.getExoPlayer().isPlaying) {
                startPositionUpdates()
            } else {
                stopPositionUpdates()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            // 재생 중 여부 변경 시 호출
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
            // 탐색(seek) 등으로 인해 재생 위치가 불연속적으로 변경될 때 호출
            updatePlayerState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updatePlayerState() // 새 미디어 항목으로 전환될 때도 상태 업데이트
        }

        override fun onPlayerError(error: PlaybackException) {
            // 오류 발생 시에도 업데이트 중지 고려
            stopPositionUpdates()
            updatePlayerState() // 에러 상태도 UI에 반영할 수 있도록
            Log.e("VideoPlayerViewModel", "Player Error: ${error.message}", error)
        }
    }

    init {
        // ViewModel이 생성될 때 ExoPlayer에 리스너를 추가합니다.
        videoPlayerPort.getExoPlayer().addListener(playerListener)
    }

    private fun updatePlayerState() {
        val player = videoPlayerPort.getExoPlayer()
        _playerState.value = PlayerState(
            currentPosition = player.currentPosition,
            duration = player.duration.coerceAtLeast(0L), // 음수 방지
            isPlaying = player.isPlaying
        )
    }

    private fun startPositionUpdates() {
        // 이미 실행 중이라면 중지
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch(Dispatchers.Main) { // UI 업데이트는 Main 스레드에서
            while (isActive) { // 코루틴이 활성 상태인 동안 계속 실행
                updatePlayerState()
                delay(1000) // 1초마다 업데이트 (더 부드러운 UI를 원하면 500ms 등으로 줄일 수 있음)
            }
        }
    }

    // 주기적인 위치 업데이트를 중지하는 함수
    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel() // Job을 취소하여 코루틴 중지
        positionUpdateJob = null
    }

    fun play(uri: String, scope: CoroutineScope) {
        currentUri = uri
        videoPlayUseCase.play(uri, scope)
        updatePlayerState()
        startPositionUpdates()
    }

    fun pause()   {
        videoPlayUseCase.pause()
        updatePlayerState() // 일시정지 시 최종 상태 업데이트
        stopPositionUpdates() // 일시정지 시 주기적 업데이트 중지
    }
    fun resume()  {
        videoPlayUseCase.resume()
        updatePlayerState() // 재개 시 현재 상태 업데이트
        startPositionUpdates() // 재개 시 주기적 업데이트 시작
    }
    fun next()    {
        videoPlayUseCase.next()
        updatePlayerState()
        // next/previous는 seekTo와 유사하게 위치가 변경되므로, 주기적인 업데이트를 다시 시작할 필요는 없지만,
        // 혹시 모를 상황에 대비하여 호출하는 것이 안전합니다.
        startPositionUpdates()
    }
    fun previous(){
        videoPlayUseCase.previous()
        updatePlayerState()
        startPositionUpdates()
    }
    fun seekTo(ms: Long) {
        videoPlayUseCase.seekTo(ms)
        updatePlayerState() // seek 후 상태 업데이트
        // seek 후에도 재생 중이라면 주기적 업데이트를 유지해야 합니다.
        if (videoPlayerPort.getExoPlayer().isPlaying) {
            startPositionUpdates()
        }
    }

    fun getPlayer(): ExoPlayer = videoPlayerPort.getExoPlayer()

    override fun onCleared() {
        super.onCleared()
        videoPlayerPort.getExoPlayer().removeListener(playerListener)
        stopPositionUpdates()
        videoPlayUseCase.release()
    }
}