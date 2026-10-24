package com.example.mediaapplication.domain.usecase

import com.example.mediaapplication.domain.model.MediaItem
import com.example.mediaapplication.domain.model.MediaType
import javax.inject.Inject

/**
 * 다음/이전 인덱스를 계산해 줍니다.
 *
 * @param items 전체 MediaItem 목록
 * @param currentIndex 현재 선택된 인덱스
 * @param shuffle 셔플 모드 여부
 * @param filterType MUSIC, VIDEO, MUSIC_VIDEO
 * @param direction +1 이면 다음, -1 이면 이전
 */
class NavigateMediaUseCase @Inject constructor() {

    operator fun invoke(
        items: List<MediaItem>,
        currentIndex: Int,
        shuffle: Boolean,
        filterType: MediaTypeFilter,
        direction: Direction
    ): Int {
        // 1) 해당 타입으로 필터
        val indices = items
            .withIndex()
            .filter { (_, item) ->
                when (filterType) {
                    MediaTypeFilter.MUSIC -> item.type == MediaType.MUSIC
                    MediaTypeFilter.VIDEO -> item.type == MediaType.VIDEO
                    MediaTypeFilter.MUSIC_VIDEO -> true
                }
            }
            .map { it.index }

        if (indices.isEmpty()) return currentIndex

        // 2) 현재 인덱스가 필터 대상에 없으면, 첫 요소로 이동
        val pos = indices.indexOf(currentIndex).takeIf { it >= 0 } ?: 0

        return if (shuffle) {
            // 3) 셔플 모드: 다른 인덱스를 랜덤 선택
            var next: Int
            do {
                next = indices.random()
            } while (next == indices[pos] && indices.size > 1)
            next
        } else {
            // 4) 순차 모드: 다음/이전
            val newPos = when (direction) {
                Direction.NEXT -> (pos + 1) % indices.size
                Direction.PREVIOUS -> (pos - 1 + indices.size) % indices.size
            }
            indices[newPos]
        }
    }

    enum class Direction { NEXT, PREVIOUS }

    enum class MediaTypeFilter { MUSIC, VIDEO, MUSIC_VIDEO }
}
