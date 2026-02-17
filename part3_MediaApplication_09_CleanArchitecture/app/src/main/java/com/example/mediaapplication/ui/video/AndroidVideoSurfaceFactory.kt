package com.example.mediaapplication.ui.video

import android.view.Surface
import com.example.mediaapplication.domain.model.VideoSurface
import com.example.mediaapplication.domain.model.VideoSurfaceFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidVideoSurfaceFactory @Inject constructor() : VideoSurfaceFactory {

    override fun createSurface(nativeObject: Any?): VideoSurface? {
        return when (nativeObject) {
            is Surface -> AndroidVideoSurface.create(nativeObject)
            else -> null
        }
    }
}