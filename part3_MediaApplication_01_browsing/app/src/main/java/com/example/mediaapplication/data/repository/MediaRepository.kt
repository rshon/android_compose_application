package com.example.mediaapplication.data.repository

import android.content.Context
import android.os.Environment
import com.example.mediaapplication.data.model.MediaItem
import com.example.mediaapplication.data.model.MediaType
import java.io.File

class MediaRepository {

    fun loadMediaContent(context: Context): List<MediaItem> {
        val mediaList = mutableListOf<MediaItem>()
        val contentResolver = context.contentResolver

        // 확장자와 미디어 타입 매핑
        val extensionToMediaType = mapOf(
            "jpg" to MediaType.IMAGE,
            "jpeg" to MediaType.IMAGE,
            "png" to MediaType.IMAGE,
            "mp3" to MediaType.MUSIC,
            "wav" to MediaType.MUSIC,
            "mp4" to MediaType.VIDEO,
            "mkv" to MediaType.VIDEO
        )

        // 핸들러 매핑
        val mediaHandlers = mapOf(
            MediaType.IMAGE to { file: File ->
                mediaList.add(MediaItem(MediaType.IMAGE, file.name))
            },
            MediaType.MUSIC to { file: File ->
                val artist = "Unknown Artist" // 실제로는 파일 메타데이터에서 읽어와야 함
                val album = "Unknown Album" // 실제로는 파일 메타데이터에서 읽어와야 함
                mediaList.add(MediaItem(MediaType.MUSIC, file.name, mapOf("artist" to artist, "album" to album)))
            },
            MediaType.VIDEO to { file: File ->
                mediaList.add(MediaItem(MediaType.VIDEO, file.name))
            }
        )

        // 에뮬레이터의 파일 복사 위치에서 미디어 파일 읽기
        val emulatorImageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        emulatorImageDir.listFiles()?.forEach { file ->
            val mediaType = extensionToMediaType[file.extension.lowercase()]
            if (file.isFile && mediaType != null) {
                mediaHandlers[mediaType]?.invoke(file)
            }
        }

        return mediaList
    }
}