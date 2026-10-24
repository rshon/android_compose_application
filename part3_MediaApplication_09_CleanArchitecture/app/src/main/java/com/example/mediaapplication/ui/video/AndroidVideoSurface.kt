package com.example.mediaapplication.ui.video

import android.view.Surface
import com.example.mediaapplication.domain.model.VideoSurface
import java.util.UUID

class AndroidVideoSurface private constructor(
    override val id: String,
    private val nativeSurface: Surface?
) : VideoSurface {

    companion object {
        /**
         * 외부에서 직접 생성하지 않고 팩토리를 통해서만 생성
         */
        internal fun create(surface: Surface?): AndroidVideoSurface {
            return AndroidVideoSurface(
                id = UUID.randomUUID().toString(),
                nativeSurface = surface
            )
        }
    }

    /**
     * 내부에서만 사용되는 네이티브 Surface 접근 메서드
     */
    internal fun getNativeSurface(): Surface? = nativeSurface

    override fun isValid(): Boolean = nativeSurface?.isValid ?: false

    override fun release() {
        nativeSurface?.release()
    }

    override fun getSize(): Pair<Int, Int>? {
        // Android Surface에서는 크기 정보를 직접 가져올 수 없음
        return null
    }
}
