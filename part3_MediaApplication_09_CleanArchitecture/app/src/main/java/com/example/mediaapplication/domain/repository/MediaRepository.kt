package com.example.mediaapplication.domain.repository

import com.example.mediaapplication.domain.model.MediaItem

interface MediaRepository {
    fun loadMediaContent(): List<MediaItem>
}