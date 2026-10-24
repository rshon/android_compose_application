package com.example.mediaapplication.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.mediaapplication.data.model.MediaItem
import com.example.mediaapplication.data.model.MediaType
import java.io.File

class MediaRepository {
    companion object {
        private const val TAG = "MediaRepository"
    }

    private val mediaTypeHandlers = mapOf<MediaType, (Context, File) -> MediaItem>(
        MediaType.IMAGE to { ctx, file ->
            MediaItem(
                type = MediaType.IMAGE,
                title = file.name,
                metadata = mapOf("path" to file.absolutePath),
                thumbnail = createImageThumbnail(file),
                filePath = file.absolutePath
            )
        },
        MediaType.MUSIC to { ctx, file ->
            val metadata = extractMusicMetadata(ctx, file)
            val albumArt = extractAlbumArt(ctx, file)
            MediaItem(
                type = MediaType.MUSIC,
                title = metadata["title"] ?: file.name,
                metadata = metadata,
                thumbnail = albumArt,
                filePath = file.absolutePath
            )
        },
        MediaType.VIDEO to { ctx, file ->
            MediaItem(
                type = MediaType.VIDEO,
                title = file.name,
                metadata = mapOf("path" to file.absolutePath),
                thumbnail = extractVideoThumbnail(ctx, file),
                filePath = file.absolutePath
            )
        }
    )

    fun loadMediaContent(context: Context): List<MediaItem> {
        val mediaList = mutableListOf<MediaItem>()

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

        // 에뮬레이터의 파일 복사 위치에서 미디어 파일 읽기
        val emulatorImageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        emulatorImageDir.listFiles()
            ?.filter { file ->
                file.isFile && extensionToMediaType[file.extension.lowercase()] != null
            }
            ?.forEach { file ->
                val mediaType = extensionToMediaType[file.extension.lowercase()]
                mediaType?.let {
                    mediaTypeHandlers[it]?.invoke(context, file)?.let { mediaItem ->
                        mediaList.add(mediaItem)
                    } ?: Log.e(TAG, "No handler found for media type: $it for file: ${file.name}")
                } ?: Log.e(TAG, "Media type not found for file: ${file.name}")
            }

        return mediaList
    }

    private fun createImageThumbnail(file: File): ImageBitmap? {
        return try {
            // 이미지 파일을 비트맵으로 로드하고 썸네일 크기로 리사이징
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)

            // 이미지 사이즈 계산
            val targetSize = 200
            val scaleFactor = Math.max(
                options.outWidth / targetSize,
                options.outHeight / targetSize
            ).coerceAtLeast(1)

            options.apply {
                inJustDecodeBounds = false
                inSampleSize = scaleFactor
            }

            BitmapFactory.decodeFile(file.absolutePath, options)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    private fun extractMusicMetadata(context: Context, file: File): Map<String, String> {
        val metadata = mutableMapOf<String, String>()
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)

            // 메타데이터 추출
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            if (title != null) metadata["title"] = title
            if (artist != null) metadata["artist"] = artist else metadata["artist"] = "Unknown Artist"
            if (album != null) metadata["album"] = album else metadata["album"] = "Unknown Album"
            if (duration != null) {
                val durationInMs = duration.toLong()
                val minutes = durationInMs / 60000
                val seconds = (durationInMs % 60000) / 1000
                metadata["duration"] = String.format("%d:%02d", minutes, seconds)
            }

            retriever.release()
        } catch (e: Exception) {
            metadata["artist"] = "Unknown Artist"
            metadata["album"] = "Unknown Album"
        }
        return metadata
    }

    private fun extractAlbumArt(context: Context, file: File): ImageBitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)

            val albumArtBytes = retriever.embeddedPicture
            retriever.release()

            if (albumArtBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.size)
                bitmap?.asImageBitmap()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun extractVideoThumbnail(context: Context, file: File): ImageBitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)

            // 비디오의 첫 프레임을 썸네일로 추출
            val bitmap = retriever.getFrameAtTime(0)
            retriever.release()

            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}
