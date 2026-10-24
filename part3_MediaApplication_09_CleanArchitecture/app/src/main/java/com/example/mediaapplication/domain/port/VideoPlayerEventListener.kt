package com.example.mediaapplication.domain.port

import com.example.mediaapplication.domain.model.PlaybackState

interface VideoPlayerEventListener {
    fun onPlaybackStateChanged(state: PlaybackState)
    fun onIsPlayingChanged(isPlaying: Boolean)
    fun onPositionDiscontinuity(oldPosition: Long, newPosition: Long)
    fun onPlayerError(message: String, cause: Throwable?)
    fun onVideoSizeChanged(width: Int, height: Int)
}
