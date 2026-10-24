package com.example.mediaapplication.domain.model

interface NativeSurfaceHandler {
    /**
     * VideoSurface를 받아서 실제 플랫폼별 Surface를 MediaPlayer에 설정
     */
    fun applySurfaceToPlayer(surface: VideoSurface?, player: Any?)
}
