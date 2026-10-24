package com.example.mediaapplication.data.model

import androidx.compose.ui.graphics.ImageBitmap

data class MediaItem(
    val type: MediaType,
    val title: String,
    val metadata: Map<String, String>? = null,
    val thumbnail: ImageBitmap? = null
)
