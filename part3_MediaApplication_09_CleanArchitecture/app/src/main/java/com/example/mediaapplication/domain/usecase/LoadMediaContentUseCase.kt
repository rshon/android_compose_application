package com.example.mediaapplication.domain.usecase

import android.content.Context
import com.example.mediaapplication.domain.repository.MediaRepository
import com.example.mediaapplication.domain.model.MediaItem
import javax.inject.Inject

class LoadMediaContentUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    operator fun invoke(): List<MediaItem> {
        return repository.loadMediaContent()
    }
}
