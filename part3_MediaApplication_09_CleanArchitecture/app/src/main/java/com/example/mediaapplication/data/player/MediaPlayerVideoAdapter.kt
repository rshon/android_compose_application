package com.example.mediaapplication.data.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import com.example.mediaapplication.domain.model.NativeSurfaceHandler
import com.example.mediaapplication.domain.model.PlaybackState
import com.example.mediaapplication.domain.model.VideoSurface
import com.example.mediaapplication.domain.port.VideoPlayerEventListener
import com.example.mediaapplication.domain.port.VideoPlayerPort
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlayerVideoAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val surfaceHandler: NativeSurfaceHandler
) : VideoPlayerPort {

    private var mediaPlayer: MediaPlayer? = null
    private var currentUri: String? = null
    private var isPrepared: Boolean = false
    private var playWhenReady: Boolean = false
    private var currentSurface: VideoSurface? = null
    private var contentVideoWidth: Int = 0
    private var contentVideoHeight: Int = 0

    private companion object {
        const val MEDIA_PLAYER_STATE_IDLE = 0
        const val MEDIA_PLAYER_STATE_PREPARING = 1
        const val MEDIA_PLAYER_STATE_PREPARED = 2
        const val MEDIA_PLAYER_STATE_PLAYING = 3
        const val MEDIA_PLAYER_STATE_PAUSED = 4
        const val MEDIA_PLAYER_STATE_PLAYBACK_COMPLETED = 5
        const val MEDIA_PLAYER_STATE_ERROR = -1
        const val TAG = "MediaPlayerVideoAdapter"
    }

    private var internalMediaPlayerState = MEDIA_PLAYER_STATE_IDLE
        set(value) {
            field = value
            val playbackState = when (value) {
                MEDIA_PLAYER_STATE_IDLE -> PlaybackState.IDLE
                MEDIA_PLAYER_STATE_PREPARING -> PlaybackState.BUFFERING
                MEDIA_PLAYER_STATE_PREPARED,
                MEDIA_PLAYER_STATE_PLAYING,
                MEDIA_PLAYER_STATE_PAUSED -> PlaybackState.READY
                MEDIA_PLAYER_STATE_PLAYBACK_COMPLETED -> PlaybackState.ENDED
                MEDIA_PLAYER_STATE_ERROR -> PlaybackState.IDLE
                else -> PlaybackState.IDLE
            }
            eventListeners.forEach { it.onPlaybackStateChanged(playbackState) }
        }

    private val eventListeners = mutableSetOf<VideoPlayerEventListener>()

    @OptIn(UnstableApi::class)
    private fun initializeMediaPlayer() {
        releasePlayer()

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            currentSurface?.let { surface ->
                surfaceHandler.applySurfaceToPlayer(surface, this)
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

                    eventListeners.forEach { it.onVideoSizeChanged(newVideoWidth, newVideoHeight) }
                }

                Log.d(TAG, "MediaPlayer video dimensions: ${contentVideoWidth}x${contentVideoHeight}")

                currentSurface?.let { surface ->
                    surfaceHandler.applySurfaceToPlayer(surface, mp)
                    Log.d(TAG, "Surface re-set after preparation")
                }

                if (playWhenReady) {
                    startPlayback()
                }
            }

            setOnCompletionListener {
                Log.d(TAG, "MediaPlayer playback completed.")
                internalMediaPlayerState = MEDIA_PLAYER_STATE_PLAYBACK_COMPLETED
                playWhenReady = false
                eventListeners.forEach { it.onIsPlayingChanged(false) }
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

                val msg = "MediaPlayer error: what=$what, extra=$extra"
                eventListeners.forEach { it.onPlayerError(msg, playbackException) }
                true
            }

            setOnInfoListener { mp, what, extra ->
                when (what) {
                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                        Log.d(TAG, "MediaPlayer buffering start...")
                        eventListeners.forEach { it.onPlaybackStateChanged(PlaybackState.BUFFERING) }
                        internalMediaPlayerState = MEDIA_PLAYER_STATE_PREPARING
                    }
                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                        Log.d(TAG, "MediaPlayer buffering end.")
                        eventListeners.forEach { it.onPlaybackStateChanged(PlaybackState.READY) }
                        internalMediaPlayerState = if (isPrepared) MEDIA_PLAYER_STATE_PREPARED else MEDIA_PLAYER_STATE_PREPARING
                    }
                    MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                        Log.d(TAG, "Video rendering started")
                        eventListeners.forEach { it.onIsPlayingChanged(true) }
                    }
                    MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> {
                        Log.d(TAG, "media is not seekable")
                    }
                }
                true
            }

            setOnVideoSizeChangedListener { mp, width, height ->
                Log.d(TAG, "Video size changed: ${width}x${height}")
                contentVideoWidth = width
                contentVideoHeight = height

                Log.d(TAG, "setOnVideoSizeChangedListener")
                eventListeners.forEach { it.onVideoSizeChanged(width, height) }
            }
        }

        internalMediaPlayerState = MEDIA_PLAYER_STATE_IDLE
        isPrepared = false
        contentVideoWidth = 0
        contentVideoHeight = 0
    }

    @OptIn(UnstableApi::class)
    override fun play(uri: String, scope: CoroutineScope) {
        scope.launch(Dispatchers.Main) {
            if (currentUri == uri && mediaPlayer != null && isPrepared) {
                play()
            } else {
                currentUri = uri
                initializeMediaPlayer()
                try {
                    mediaPlayer?.setDataSource(context, Uri.parse(uri))
                    internalMediaPlayerState = MEDIA_PLAYER_STATE_PREPARING
                    mediaPlayer?.prepareAsync()
                    Log.d(TAG, "MediaPlayer preparing for URI: $uri")
                    playWhenReady = true
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting data source or preparing MediaPlayer", e)
                    internalMediaPlayerState = MEDIA_PLAYER_STATE_ERROR
                    val playbackException = PlaybackException("Error setting data source: ${e.message}", e, PlaybackException.ERROR_CODE_IO_UNSPECIFIED)
                    eventListeners.forEach { it.onPlayerError("${e.message}", playbackException) }
                }
            }
        }
    }

    override fun play() {
        playWhenReady = true

        if (isPrepared) {
            startPlayback()
        } else if (mediaPlayer == null && currentUri != null) {
            play(currentUri!!, CoroutineScope(Dispatchers.Main + SupervisorJob()))
        }
    }

    private fun startPlayback() {
        if (isPrepared && mediaPlayer?.isPlaying == false) {
            currentSurface?.let { surface ->
                surfaceHandler.applySurfaceToPlayer(surface, mediaPlayer)
                Log.d(TAG, "Surface ensured before playback")
            }

            mediaPlayer?.start()
            internalMediaPlayerState = MEDIA_PLAYER_STATE_PLAYING
            Log.d(TAG, "MediaPlayer playback started.")
            eventListeners.forEach { it.onIsPlayingChanged(true) }
            playWhenReady = true
        }
    }

    override fun pause() {
        playWhenReady = false
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            internalMediaPlayerState = MEDIA_PLAYER_STATE_PAUSED
            Log.d(TAG, "MediaPlayer playback paused.")
            eventListeners.forEach { it.onIsPlayingChanged(false) }
        }
    }

    override fun resume() {
        play()
    }

    @OptIn(UnstableApi::class)
    override fun seekTo(ms: Long) {
        if (isPrepared) {
            val currentPos = getCurrentPlayerPosition()
            val seekPosition = ms.coerceAtMost(mediaPlayer?.duration?.toLong() ?: ms)
                .coerceAtLeast(0L)
            Log.d(TAG, "MediaPlayer seeking to: $seekPosition ms from $currentPos ms")
            mediaPlayer?.seekTo(seekPosition.toInt())

            eventListeners.forEach { it.onPositionDiscontinuity(currentPos, seekPosition) }
        }
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
        } catch (e: Exception) {
            // stop() throws IllegalStateException in Idle/Error state; release() must still run
            Log.w(TAG, "Error stopping MediaPlayer", e)
        }
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaPlayer", e)
        }
        mediaPlayer = null
        Log.d(TAG, "MediaPlayer released.")

        isPrepared = false
        playWhenReady = false
        internalMediaPlayerState = MEDIA_PLAYER_STATE_IDLE
    }

    override fun release() {
        releasePlayer()
        currentSurface = null
    }

    override fun getPlayWhenReady(): Boolean = playWhenReady

    override fun setPlayWhenReady() {
        this.playWhenReady = true
        if (isPrepared && !(mediaPlayer?.isPlaying ?: false)) {
            startPlayback()
        }
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
    override fun getNativePlayer(): Any? {
        return null
    }

    override fun setSurface(surface: VideoSurface?) {
        currentSurface = surface
        Log.d(TAG, "VideoSurface ${if (surface != null) "set" else "cleared"}")

        mediaPlayer?.let { mp ->
            surfaceHandler.applySurfaceToPlayer(surface, mp)
            Log.d(TAG, "Surface applied to existing MediaPlayer")
        }
    }

    override fun getVideoSize(): Pair<Int, Int> {
        return Pair(contentVideoWidth, contentVideoHeight)
    }

    override fun addEventListener(listener: VideoPlayerEventListener) {
        eventListeners.add(listener)
    }

    override fun removeEventListener(listener: VideoPlayerEventListener) {
        eventListeners.remove(listener)
    }
}
