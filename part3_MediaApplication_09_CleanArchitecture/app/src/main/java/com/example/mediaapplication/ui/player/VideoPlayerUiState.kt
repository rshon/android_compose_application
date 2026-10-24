package com.example.mediaapplication.ui.player

import com.example.mediaapplication.domain.model.PlayerState

data class VideoPlayerUiState(
    val currentUri: String? = null,
    val playerState: PlayerState = PlayerState(0L, 0L, false),
    val videoSize: Pair<Int, Int> = Pair(0, 0),
    val isLoading: Boolean = false,
    val error: String? = null
)
