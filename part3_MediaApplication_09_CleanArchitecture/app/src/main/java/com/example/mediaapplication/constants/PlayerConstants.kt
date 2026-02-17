package com.example.mediaapplication.constants

object PlayerConstants {
    // ExoPlayer related
    const val SEEK_BACKWARD_INCREMENT_MS: Long = 10000
    const val SEEK_FORWARD_INCREMENT_MS: Long = 10000

    // playback
    const val POSITION_UPDATE_INTERVAL_PLAYING: Long = 100L
    const val POSITION_UPDATE_INTERVAL_PAUSED: Long = 1000L

    // controls
    const val AUTO_HIDING_MS: Long = 4000

    const val BACKGROUND_COLOR = 0x80000000

    // progress bar
    const val SAMPLES_PER_PROGRESS_BAR_UPDATE = 100L
}