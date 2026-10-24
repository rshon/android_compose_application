package com.example.mediaapplication.domain.model

data class PlayerState(
    val currentPosition: Long, // current position in ms
    val duration: Long,        // total duration in ms
    val isPlaying: Boolean     // playing status
)
