package com.example.ch_app_bind_service.service

import androidx.media3.exoplayer.ExoPlayer
import android.app.Service
import android.content.Intent
import android.media.session.MediaSession
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class MusicService : Service() {
    private val binder = MusicBinder()
    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession(this, "MusicService").apply {
            isActive = true
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        exoPlayer?.release()
        exoPlayer = null
        mediaSession?.release()
        mediaSession = null
        return super.onUnbind(intent)
    }

    fun playMusic(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
        _isPlaying.value = true
    }

    fun pauseMusic() {
        exoPlayer?.pause()
        _isPlaying.value = false
    }

    fun resumeMusic() {
        exoPlayer?.play()
        _isPlaying.value = true
    }

    fun stopMusic() {
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
        _isPlaying.value = false
    }

    fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }

    fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Long {
        return exoPlayer?.duration ?: 0
    }
}