package com.example.mediaapplication.data.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.mediaapplication.domain.port.MediaPlayerPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
class AndroidMediaPlayerPort @Inject constructor(): MediaPlayerPort {
    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying  = MutableStateFlow(false)
    private val _progress   = MutableStateFlow(0)
    private val _duration   = MutableStateFlow(0)
    private val handler     = Handler(Looper.getMainLooper())
    private var updateJob: Job? = null

    override val isPlaying: Flow<Boolean> = _isPlaying
    override val progress:  Flow<Int>     = _progress
    override val duration:  Flow<Int>     = _duration

    override fun play(path: String, scope: CoroutineScope) {
        updateJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            start()
        }
        _isPlaying.value = true
        _duration.value  = mediaPlayer?.duration ?: 0

        updateJob = scope.launch(Dispatchers.Main) {
            while (mediaPlayer?.isPlaying == true) {
                _progress.value = mediaPlayer?.currentPosition ?: 0
                kotlinx.coroutines.delay(500)
            }
        }
        mediaPlayer?.setOnCompletionListener {
            _isPlaying.value = false
            handler.removeCallbacksAndMessages(null)
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    override fun resume() {
        mediaPlayer?.start()
        _isPlaying.value = true
    }

    override fun seekTo(ms: Int) {
        mediaPlayer?.seekTo(ms)
        _progress.value = ms
    }

    override fun release() {
        updateJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _progress.value  = 0
        _duration.value  = 0
    }
}