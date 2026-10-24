package com.example.mediaapplication.domain

import android.content.Context
import com.example.mediaapplication.domain.model.MediaItem

interface MediaRepository {
    fun loadMediaContent(context: Context): List<MediaItem>
}
