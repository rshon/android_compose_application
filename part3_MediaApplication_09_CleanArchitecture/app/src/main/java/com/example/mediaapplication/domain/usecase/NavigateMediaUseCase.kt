package com.example.mediaapplication.domain.usecase

import com.example.mediaapplication.constants.MediaConstants
import com.example.mediaapplication.domain.model.MediaItem
import com.example.mediaapplication.domain.model.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class NavigateMediaUseCase @Inject constructor() {

    private val _isShuffled = MutableStateFlow(false)
    var isShuffled: Boolean
        get() = _isShuffled.value
        private set(value) { _isShuffled.value = value }

    private var contentFilter: MediaTypeFilter = MediaTypeFilter.VIDEO

    fun setFilter(filter: MediaTypeFilter) {
        contentFilter = filter
    }

    fun setShuffle(shuffle: Boolean) {
        isShuffled = shuffle
    }

    operator fun invoke(
        items: List<MediaItem>,
        currentIndex: Int,
        direction: Direction
    ): Int = when {
        items.isEmpty() -> MediaConstants.INVALID_INDEX
        else -> {
            val filteredIndices = getFilteredIndices(items)
            if (filteredIndices.isEmpty()) currentIndex
            else navigateWithinFilteredList(filteredIndices, currentIndex, direction)
        }
    }

    private fun getFilteredIndices(items: List<MediaItem>): List<Int> {
        return items
            .withIndex()
            .filter { (_, item) ->
                when (contentFilter) {
                    MediaTypeFilter.MUSIC -> item.type == MediaType.MUSIC
                    MediaTypeFilter.VIDEO -> item.type == MediaType.VIDEO
                    MediaTypeFilter.MUSIC_VIDEO -> true
                }
            }
            .map { it.index }
    }

    private fun navigateWithinFilteredList(
        indices: List<Int>,
        currentIndex: Int,
        direction: Direction
    ): Int {
        val currentPos = indices.indexOf(currentIndex).takeIf { it >= 0 } ?: 0

        return if (isShuffled) {
            generateSequence { indices.random() }
                .first { it != indices[currentPos] || indices.size == 1 }
        } else {
            val newPos = when (direction) {
                Direction.NEXT -> (currentPos + 1) % indices.size
                Direction.PREVIOUS -> (currentPos - 1 + indices.size) % indices.size
            }
            indices[newPos]
        }
    }

    enum class Direction { NEXT, PREVIOUS }
    enum class MediaTypeFilter { MUSIC, VIDEO, MUSIC_VIDEO }
}