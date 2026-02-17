package com.example.mediaapplication.data.player

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediaapplication.domain.port.VideoPlayerPort
import androidx. media3.common.MediaItem
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
    // internalPlayer를 null 허용으로 만들고, 처음에는 null로 초기화합니다.
    private var internalPlayer: ExoPlayer? = null

    // ExoPlayer 인스턴스를 가져오거나, 없다면 새로 생성하는 헬퍼 함수
    private fun getOrCreatePlayer(): ExoPlayer {
        // 이미 인스턴스가 있다면 반환
        return internalPlayer ?: synchronized(this) { // 스레드 안전성을 위해 synchronized 블록 사용
            // synchronized 블록 안에서 다시 한 번 null 체크 (double-checked locking)
            internalPlayer ?: run {
                ExoPlayer.Builder(context).build().also {
                    internalPlayer = it // 새로 생성된 인스턴스를 할당
                }
            }
        }
    }

    override fun play(uri: String, scope: CoroutineScope) {
        scope.launch(Dispatchers.Main) {
            val player = getOrCreatePlayer() // ExoPlayer를 가져오거나 새로 생성
            player.setMediaItem(androidx.media3.common.MediaItem.fromUri(uri))
            player.prepare()
            player.play()
        }
    }

    // 기존 메서드들은 null 안전 호출(?. )을 사용하도록 변경
    override fun pause() = internalPlayer?.pause() ?: Unit
    override fun resume() = internalPlayer?.play() ?: Unit
    override fun seekTo(ms: Long) = internalPlayer?.seekTo(ms) ?: Unit
    override fun next() = internalPlayer?.seekToNext() ?: Unit
    override fun previous() = internalPlayer?.seekToPrevious() ?: Unit

    override fun release() {
        internalPlayer?.release() // ExoPlayer 해제
        internalPlayer = null     // 참조를 null로 설정하여 다음 사용 시 새로 생성되도록 함
    }

    override fun getExoPlayer(): ExoPlayer = getOrCreatePlayer() // 항상 유효한 플레이어 인스턴스 반환
}