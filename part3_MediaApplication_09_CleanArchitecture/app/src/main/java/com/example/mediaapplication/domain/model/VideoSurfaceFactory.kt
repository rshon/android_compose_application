package com.example.mediaapplication.domain.model

interface VideoSurfaceFactory {
    /**
     * 플랫폼별 네이티브 객체로부터 VideoSurface를 생성
     */
    fun createSurface(nativeObject: Any?): VideoSurface?
}
