package com.example.mediaapplication.ui.video

import android.media.MediaPlayer
import android.util.Log
import com.example.mediaapplication.domain.model.NativeSurfaceHandler
import com.example.mediaapplication.domain.model.VideoSurface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidNativeSurfaceHandler @Inject constructor() : NativeSurfaceHandler {

    override fun applySurfaceToPlayer(surface: VideoSurface?, player: Any?) {
        if (player !is MediaPlayer) return
        // null clears the surface; a non-null surface must be an AndroidVideoSurface
        if (surface != null && surface !is AndroidVideoSurface) return

        try {
            val nativeSurface = (surface as? AndroidVideoSurface)?.getNativeSurface()
            player.setSurface(nativeSurface)
            Log.d("AndroidNativeSurfaceHandler", "Surface applied to MediaPlayer")
        } catch (e: Exception) {
            Log.e("AndroidNativeSurfaceHandler", "Error setting surface", e)
        }
    }
}
