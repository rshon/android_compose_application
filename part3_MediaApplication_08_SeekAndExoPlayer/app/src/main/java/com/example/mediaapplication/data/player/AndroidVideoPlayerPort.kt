package com.example.mediaapplication.data.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import com.example.mediaapplication.domain.port.VideoPlayerPort
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidVideoPlayerPort @Inject constructor(
    @ApplicationContext
    private val context: Context
) : VideoPlayerPort {
    @Volatile
    private var internalPlayer: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    private fun getOrCreatePlayer(): ExoPlayer {
        return internalPlayer ?: synchronized(this) { // 스레드 안전성을 위해 synchronized 블록 사용
            // synchronized 블록 안에서 다시 한 번 null 체크 (double-checked locking)
            internalPlayer ?: run {
                ExoPlayer.Builder(context)
                    .setSeekBackIncrementMs(10000)
                    .setSeekForwardIncrementMs(10000)
//                    .setRenderersFactory(
//                        DefaultRenderersFactory(context).setForceEnableSoftwareDecoding(true)
//                    )
//                    // 또는 특정 코덱 선택 로직 추가
//                    .setMediaCodecSelector { mimeType, requiresSecureDecoder ->
//                        MediaCodecSelector.DEFAULT.getDecoderInfos(mimeType, requiresSecureDecoder)
//                    }
                    .build()
                    .apply {
                        setSeekParameters(SeekParameters.EXACT)
                    }
                    .also {
                        internalPlayer = it // 새로 생성된 인스턴스를 할당
                    }
            }
        }
    }

    override fun play(uri: String, scope: CoroutineScope) {
        scope.launch(Dispatchers.Main) {
            val player = getOrCreatePlayer() // ExoPlayer를 가져오거나 새로 생성
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.play()
        }
    }

    override fun play() {
        val player = getOrCreatePlayer()
        player.play()
    }

    // 기존 메서드들은 null 안전 호출(?. )을 사용하도록 변경
    override fun pause() = internalPlayer?.pause() ?: Unit
    override fun resume() = internalPlayer?.play() ?: Unit
    override fun seekTo(ms: Long) = internalPlayer?.seekTo(ms) ?: Unit
    override fun next() = internalPlayer?.seekToNext() ?: Unit
    override fun previous() = internalPlayer?.seekToPrevious() ?: Unit

    override fun release() {
        internalPlayer?.apply {
            stop()
            release()
        }
        internalPlayer = null
    }

    override fun getPlayWhenReady(): Boolean {
        return internalPlayer?.playWhenReady ?: false
    }

    override fun setPlayWhenReady() {
        internalPlayer?.playWhenReady = true
    }

    override fun getExoPlayer(): ExoPlayer = getOrCreatePlayer() // 항상 유효한 플레이어 인스턴스 반환

    override fun addPlayerListener(listener: Player.Listener) {
        internalPlayer?.addListener(listener)
    }

    override fun removePlayerListener(listener: Player.Listener) {
        internalPlayer?.removeListener(listener)
    }

    override fun getCurrentPlayerPosition(): Long = internalPlayer?.currentPosition ?: 0L
    override fun getTotalDuration(): Long = internalPlayer?.duration?.coerceAtLeast(0L) ?: 0L
    override fun isCurrentlyPlaying(): Boolean = internalPlayer?.isPlaying ?: false

    override fun setSurface(surface: android.view.Surface?) {
        // No-op
    }

    @OptIn(UnstableApi::class)
    override fun getVideoSize(): Pair<Int, Int> {
        val videoFormat = internalPlayer?.videoFormat
        return if (videoFormat != null) {
            Pair(videoFormat.width, videoFormat.height)
        } else {
            Pair(0, 0) // 비디오가 로드되지 않았거나 오디오만 있는 경우
        }
    }

    override fun addVideoSizeListener(listener: (width: Int, height: Int) -> Unit) {
    }

    override fun removeVideoSizeListener(listener: (width: Int, height: Int) -> Unit) {
    }
}
