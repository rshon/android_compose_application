package com.example.mediaapplication.data.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.Surface
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi // ExoPlayer의 UnstableApi와 직접적인 관련은 없지만, Player 인터페이스를 참조하므로 추가
import androidx.media3.exoplayer.ExoPlayer // getExoPlayer() 반환 타입 때문에 필요
import com.example.mediaapplication.domain.port.VideoPlayerPort
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlayerVideoPort @Inject constructor(
    @ApplicationContext private val context: Context
) : VideoPlayerPort {

    private var mediaPlayer: MediaPlayer? = null
    private var currentUri: String? = null
    private var isPrepared: Boolean = false
    private var playWhenReady: Boolean = false // MediaPlayer에는 playWhenReady가 없으므로 직접 관리
    private var currentSurface: Surface? = null
    private var contentVideoWidth: Int = 0
    private var contentVideoHeight: Int = 0

    // ExoPlayer의 Player.Listener와 유사한 기능을 MediaPlayer 콜백으로 구현하기 위한 리스너 관리
    private val listeners = mutableSetOf<Player.Listener>()
    private var playerState: Int = Player.STATE_IDLE

    private val videoSizeListeners = mutableSetOf<(width: Int, height: Int) -> Unit>()



    // MediaPlayer의 상태를 ExoPlayer의 Player 상태와 매핑
    private fun mapMediaPlayerStateToPlayerState(mpState: Int, buffering: Boolean = false): Int {
        return when (mpState) {
            MEDIA_PLAYER_STATE_IDLE -> Player.STATE_IDLE
            MEDIA_PLAYER_STATE_ERROR -> Player.STATE_IDLE // 에러 시 IDLE로 간주 (ExoPlayer는 별도 에러 상태 있음)
            MEDIA_PLAYER_STATE_PREPARING -> Player.STATE_BUFFERING // 준비 중을 버퍼링으로 간주
            MEDIA_PLAYER_STATE_PREPARED -> if (buffering) Player.STATE_BUFFERING else Player.STATE_READY
            MEDIA_PLAYER_STATE_PLAYING -> Player.STATE_READY // 재생 중은 준비 완료
            MEDIA_PLAYER_STATE_PAUSED -> Player.STATE_READY // 일시정지도 준비 완료
            MEDIA_PLAYER_STATE_PLAYBACK_COMPLETED -> Player.STATE_ENDED
            else -> Player.STATE_IDLE
        }
    }

    // MediaPlayer 내부 상태 추적용 (실제 MediaPlayer에는 이런 상태 상수가 없음)
    private companion object {
        const val MEDIA_PLAYER_STATE_IDLE = 0
        const val MEDIA_PLAYER_STATE_PREPARING = 1
        const val MEDIA_PLAYER_STATE_PREPARED = 2
        const val MEDIA_PLAYER_STATE_PLAYING = 3
        const val MEDIA_PLAYER_STATE_PAUSED = 4
        const val MEDIA_PLAYER_STATE_PLAYBACK_COMPLETED = 5
        const val MEDIA_PLAYER_STATE_ERROR = -1
        const val TAG = "MediaPlayerVideoPort"
    }

    private var internalMediaPlayerState = MEDIA_PLAYER_STATE_IDLE
        set(value) {
            field = value
            playerState = mapMediaPlayerStateToPlayerState(value)
            Log.d(TAG, "MediaPlayer internal state changed to: $value, Player state: $playerState")
            listeners.forEach { it.onPlaybackStateChanged(playerState) }
        }


    @OptIn(UnstableApi::class)
    private fun initializeMediaPlayer() {
        releasePlayer()

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            currentSurface?.let { surface ->
                setSurface(surface)
                Log.d(TAG, "Surface set during MediaPlayer initialization")
            }

            setOnPreparedListener { mp ->
                Log.d(TAG, "MediaPlayer prepared.")
                isPrepared = true
                internalMediaPlayerState = MEDIA_PLAYER_STATE_PREPARED

                val newVideoWidth = mp.videoWidth
                val newVideoHeight = mp.videoHeight

                if (newVideoWidth > 0 && newVideoHeight > 0 &&
                    (contentVideoWidth != newVideoWidth || contentVideoHeight != newVideoHeight)) {
                    contentVideoWidth = newVideoWidth
                    contentVideoHeight = newVideoHeight
                    Log.d(TAG, "MediaPlayer video dimensions (onPrepared): ${contentVideoWidth}x${contentVideoHeight}")
                    // Notify video size listeners
                    videoSizeListeners.forEach { it(contentVideoWidth, contentVideoHeight) }
                }

                Log.d(TAG, "MediaPlayer video dimensions: ${contentVideoWidth}x${contentVideoHeight}")

                currentSurface?.let { surface ->
                    mp.setSurface(surface)
                    Log.d(TAG, "Surface re-set after preparation")
                }

                if (playWhenReady) {
                    startPlayback()
                }
                // duration 업데이트 알림 (ExoPlayer의 onTimelineChanged 또는 onMediaItemTransition 후 유사)
                listeners.forEach { it.onMediaItemTransition(null, Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) }
            }

            setOnCompletionListener {
                Log.d(TAG, "MediaPlayer playback completed.")
                internalMediaPlayerState = MEDIA_PLAYER_STATE_PLAYBACK_COMPLETED
                playWhenReady = false // 재생 완료 시 playWhenReady 초기화
            }

            setOnErrorListener { mp, what, extra ->
                Log.e(TAG, "MediaPlayer Error: what=$what, extra=$extra")
                internalMediaPlayerState = MEDIA_PLAYER_STATE_ERROR
                isPrepared = false
                playWhenReady = false

                val playbackException = PlaybackException(
                    "MediaPlayer error: what=$what, extra=$extra",
                    null,
                    what
                )
                listeners.forEach { it.onPlayerError(playbackException) }
                true  // true를 반환해야 onCompletionListener가 호출되지 않음
            }

            setOnBufferingUpdateListener { mp, percent ->
                // Buffering progress
            }

            setOnInfoListener { mp, what, extra ->
                when (what) {
                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                        Log.d(TAG, "MediaPlayer buffering start...")
                        playerState = mapMediaPlayerStateToPlayerState(internalMediaPlayerState, true)
                        listeners.forEach { it.onPlaybackStateChanged(playerState) }
                    }
                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                        Log.d(TAG, "MediaPlayer buffering end.")
                        playerState = mapMediaPlayerStateToPlayerState(internalMediaPlayerState, false)
                        listeners.forEach { it.onPlaybackStateChanged(playerState) }
                    }
                    MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                        Log.d(TAG, "Video rendering started")
                    }
                }
                true
            }

            setOnVideoSizeChangedListener { mp, width, height ->
                Log.d(TAG, "Video size changed: ${width}x${height}")
                contentVideoWidth = width
                contentVideoHeight = height
                // Notify listeners about video size change for UI updates
                listeners.forEach { listener ->
                    // Since ExoPlayer doesn't have direct video size callback in Player.Listener,
                    // we can trigger a media item transition to update UI
                    listener.onMediaItemTransition(null, Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
                }

                Log.d(TAG, "...")

                videoSizeListeners.forEach {
                    Log.d(TAG, "calling videoSizeListeners with ${width}x${height}")

                    it(contentVideoWidth, contentVideoHeight) }
            }
        }

        internalMediaPlayerState = MEDIA_PLAYER_STATE_IDLE
        isPrepared = false

        contentVideoWidth = 0
        contentVideoHeight = 0
    }

    private fun startPlayback() {
        if (isPrepared && mediaPlayer?.isPlaying == false) {
            currentSurface?.let { surface ->
                mediaPlayer?.setSurface(surface)
                Log.d(TAG, "Surface ensured before playback")
            }

            mediaPlayer?.start()
            internalMediaPlayerState = MEDIA_PLAYER_STATE_PLAYING
            playWhenReady = true
            Log.d(TAG, "MediaPlayer playback started.")
            listeners.forEach { it.onIsPlayingChanged(true) }
        }
    }

    @OptIn(UnstableApi::class)
    override fun play(uri: String, scope: CoroutineScope) {
        scope.launch(Dispatchers.Main) { // MediaPlayer는 UI 스레드에서 다루는 것이 안전
            if (currentUri == uri && mediaPlayer != null && isPrepared) {
                play()
            } else {
                currentUri = uri
                initializeMediaPlayer()
                try {
                    mediaPlayer?.setDataSource(context, Uri.parse(uri))
                    internalMediaPlayerState = MEDIA_PLAYER_STATE_PREPARING
                    mediaPlayer?.prepareAsync() // 비동기 준비
                    playWhenReady = true // 재생 준비되면 바로 시작하도록 설정
                    Log.d(TAG, "MediaPlayer preparing for URI: $uri")
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting data source or preparing MediaPlayer", e)
                    internalMediaPlayerState = MEDIA_PLAYER_STATE_ERROR
                    val playbackException = PlaybackException("Error setting data source: ${e.message}", e, PlaybackException.ERROR_CODE_IO_UNSPECIFIED)
                    listeners.forEach { it.onPlayerError(playbackException) }
                }
            }
        }
    }

    override fun play() {
        playWhenReady = true
        if (isPrepared) {
            startPlayback()
        } else if (mediaPlayer == null && currentUri != null) {
            // 플레이어가 아예 없는 상태에서 play()가 호출되면 (예: release 후) 다시 준비
            play(currentUri!!, CoroutineScope(Dispatchers.Main + SupervisorJob()))
        }
        // mediaPlayer가 있고 isPrepared=false 인 경우는 prepareAsync()가 진행중인 경우이므로,
        // setOnPreparedListener에서 playWhenReady 상태를 보고 재생할 것임.
    }

    override fun pause() {
        playWhenReady = false
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            internalMediaPlayerState = MEDIA_PLAYER_STATE_PAUSED
            Log.d(TAG, "MediaPlayer playback paused.")
            listeners.forEach { it.onIsPlayingChanged(false) }
        }
    }

    override fun resume() {
        play() // play()가 내부적으로 playWhenReady=true로 설정하고 재생 시도
    }

    @OptIn(UnstableApi::class)
    override fun seekTo(ms: Long) {
        if (isPrepared) {
            val currentPos = getCurrentPlayerPosition()
            val seekPosition = ms.coerceAtMost(mediaPlayer?.duration?.toLong() ?: ms)
                .coerceAtLeast(0L)
            Log.d(TAG, "MediaPlayer seeking to: $seekPosition ms from $currentPos ms")
            mediaPlayer?.seekTo(seekPosition.toInt())

            val oldPos = Player.PositionInfo(
                null, C.INDEX_UNSET, null, C.INDEX_UNSET,
                currentPos, currentPos, C.INDEX_UNSET, C.INDEX_UNSET
            )
            val newPos = Player.PositionInfo(
                null, C.INDEX_UNSET, null, C.INDEX_UNSET,
                seekPosition, seekPosition, C.INDEX_UNSET, C.INDEX_UNSET
            )
            listeners.forEach {
                it.onPositionDiscontinuity(oldPos, newPos, Player.DISCONTINUITY_REASON_SEEK)
            }
        }
    }

    override fun next() {
        // MediaPlayer는 Playlist 기능이 내장되어 있지 않음.
        // 이 예제에서는 아무 동작 안 함 또는 에러 로깅.
        Log.w(TAG, "next() is not supported by MediaPlayerVideoPort without playlist management.")
    }

    override fun previous() {
        // MediaPlayer는 Playlist 기능이 내장되어 있지 않음.
        Log.w(TAG, "previous() is not supported by MediaPlayerVideoPort without playlist management.")
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaPlayer", e)
        }
        mediaPlayer = null
        isPrepared = false
        playWhenReady = false
        internalMediaPlayerState = MEDIA_PLAYER_STATE_IDLE
        Log.d(TAG, "MediaPlayer released.")
    }

    override fun release() {
        releasePlayer()
        listeners.clear()
        videoSizeListeners.clear()
        currentSurface = null
    }

    override fun getPlayWhenReady(): Boolean = playWhenReady

    override fun setPlayWhenReady() {
        this.playWhenReady = true
        if (isPrepared && !(mediaPlayer?.isPlaying ?: false)) {
            startPlayback()
        }
    }

    override fun addPlayerListener(listener: Player.Listener) {
        listeners.add(listener)
        listener.onPlaybackStateChanged(playerState)
        listener.onIsPlayingChanged(mediaPlayer?.isPlaying ?: false)
    }

    override fun removePlayerListener(listener: Player.Listener) {
        listeners.remove(listener)
    }

    override fun getCurrentPlayerPosition(): Long {
        return if (isPrepared && mediaPlayer != null) {
            try {
                mediaPlayer!!.currentPosition.toLong()
            } catch (e: IllegalStateException) {
                0L
            }
        } else {
            0L
        }
    }

    override fun getTotalDuration(): Long {
        return if (isPrepared && mediaPlayer != null) {
            try {
                mediaPlayer!!.duration.toLong()
            } catch (e: IllegalStateException) {
                0L
            }
        } else {
            0L
        }
    }

    override fun isCurrentlyPlaying(): Boolean {
        return if (isPrepared && mediaPlayer != null) {
            try {
                mediaPlayer!!.isPlaying
            } catch (e: IllegalStateException) {
                false
            }
        } else {
            false
        }
    }

    @OptIn(UnstableApi::class)
    override fun getExoPlayer(): ExoPlayer {
        throw UnsupportedOperationException("MediaPlayerVideoPort does not provide an ExoPlayer instance.")
    }

    override fun setSurface(surface: android.view.Surface?) {
        currentSurface = surface
        Log.d(TAG, "Surface ${if (surface != null) "set" else "cleared"}")

        // Set surface to MediaPlayer if it's already initialized
        mediaPlayer?.let { mp ->
            try {
                mp.setSurface(surface)
                Log.d(TAG, "Surface applied to existing MediaPlayer")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting surface to MediaPlayer", e)
            }
        }
    }

    override fun getVideoSize(): Pair<Int, Int> {
        return Pair(contentVideoWidth, contentVideoHeight)
    }

    override fun addVideoSizeListener(listener: (width: Int, height: Int) -> Unit) {
        videoSizeListeners.add(listener)
        // Immediately notify with current size if available
        if (contentVideoWidth > 0 && contentVideoHeight > 0) {
            listener(contentVideoWidth, contentVideoHeight)
        }
    }

    override fun removeVideoSizeListener(listener: (width: Int, height: Int) -> Unit) {
        videoSizeListeners.remove(listener)
    }
}