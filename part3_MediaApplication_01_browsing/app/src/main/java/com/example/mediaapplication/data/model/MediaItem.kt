package com.example.mediaapplication.data.model

data class MediaItem(
    val type: MediaType,
    val title: String,
    val metadata: Map<String, String>? = null
)