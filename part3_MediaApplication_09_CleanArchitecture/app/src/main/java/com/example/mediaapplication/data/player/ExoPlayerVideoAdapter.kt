package com.example.mediaapplication.data.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import com.example.mediaapplication.constants.PlayerConstants
import com.example.mediaapplication.domain.model.PlaybackState
import com.example.mediaapplication.domain.model.VideoSurface
import com.example.mediaapplication.domain.port.VideoPlayerEventListener
import com.example.mediaapplication.domain.port.VideoPlayerPort
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoPlayerVideoAdapter @Inject constructor(
    @ApplicationContext
    private val context: Context
) : VideoPlayerPort {

    private companion object {
        const val TAG = "ExoPlayerVideoAdapter"
    }

    private var internalPlayer: ExoPlayer? = null
    private val eventListeners = mutableSetOf<VideoPlayerEventListener>()

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            Log.d(TAG, "+onPlaybackStateChanged: $playbackState")

            val state = when (playbackState) {
                Player.STATE_IDLE -> PlaybackState.IDLE
                Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                Player.STATE_READY -> PlaybackState.READY
                Player.STATE_ENDED -> PlaybackState.ENDED
                else -> PlaybackState.IDLE
            }

            Log.d(TAG, "onPlaybackStateChanged: $state")
            eventListeners.forEach { it.onPlaybackStateChanged(state) }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            eventListeners.forEach { it.onIsPlayingChanged(isPlaying) }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            eventListeners.forEach {
                it.onPositionDiscontinuity(oldPosition.positionMs, newPosition.positionMs)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            eventListeners.forEach {
                it.onPlayerError(error.message ?: "Unknown error", error)
            }
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            Log.d(TAG, "onVideoSizeChanged: ${videoSize.width} x ${videoSize.height}")
            eventListeners.forEach { it.onVideoSizeChanged(videoSize.width, videoSize.height) }
        }
    }

    init {
        Log.d(TAG, "+init")
    }

    override fun play(uri: String, scope: CoroutineScope) {
        Log.d(TAG, "+play with: $uri")

        scope.launch(Dispatchers.Main) {
            val player = getOrCreatePlayer()
            player.setMediaItem(androidx.media3.common.MediaItem.fromUri(uri))
            player.prepare()
            player.play()
        }
    }

    override fun play() {
        Log.d(TAG, "+play")
        val player = getOrCreatePlayer()
        player.play()
    }

    override fun pause() = internalPlayer?.pause() ?: Unit
    override fun resume() = internalPlayer?.play() ?: Unit
    override fun seekTo(ms: Long) = internalPlayer?.seekTo(ms) ?: Unit

    override fun getPlayWhenReady(): Boolean {
        Log.d(TAG, "+getPlayWhenReady: ${internalPlayer?.playWhenReady}")
        return internalPlayer?.playWhenReady ?: false
    }

    override fun setPlayWhenReady() {
        Log.d(TAG, "+setPlayWhenReady true")
        internalPlayer?.playWhenReady = true
    }

    override fun getCurrentPlayerPosition(): Long = internalPlayer?.currentPosition ?: 0L
    override fun getTotalDuration(): Long = internalPlayer?.duration?.coerceAtLeast(0L) ?: 0L
    override fun isCurrentlyPlaying(): Boolean = internalPlayer?.isPlaying ?: false

    override fun setSurface(surface: VideoSurface?) {
        // No-op: Intentionally empty!
    }

    @OptIn(UnstableApi::class)
    override fun getVideoSize(): Pair<Int, Int> {
        val videoFormat = internalPlayer?.videoFormat
        return if (videoFormat != null) {
            Pair(videoFormat.width, videoFormat.height)
        } else {
            Pair(0, 0)
        }
    }

    override fun getNativePlayer(): ExoPlayer = getOrCreatePlayer()

    override fun addEventListener(listener: VideoPlayerEventListener) {
        eventListeners.add(listener)
    }

    override fun removeEventListener(listener: VideoPlayerEventListener) {
        eventListeners.remove(listener)
    }

    override fun release() {
        Log.d(TAG, "+release")
        internalPlayer?.apply {
            stop()
            removeListener(playerListener)
            release()
        }
        internalPlayer = null
    }

    @OptIn(UnstableApi::class)
    private fun getOrCreatePlayer(): ExoPlayer {
        if (internalPlayer != null) {
            return internalPlayer!!
        }

        return internalPlayer ?: synchronized(this) {
            internalPlayer ?: run {
                val newPlayer = ExoPlayer.Builder(context)
                    .setSeekBackIncrementMs(PlayerConstants.SEEK_BACKWARD_INCREMENT_MS)
                    .setSeekForwardIncrementMs(PlayerConstants.SEEK_FORWARD_INCREMENT_MS)
                    .build()
                    .apply {
                        setSeekParameters(SeekParameters.CLOSEST_SYNC)
                        addListener(playerListener)
                    }

                internalPlayer = newPlayer
                newPlayer
            }
        }
    }
}
