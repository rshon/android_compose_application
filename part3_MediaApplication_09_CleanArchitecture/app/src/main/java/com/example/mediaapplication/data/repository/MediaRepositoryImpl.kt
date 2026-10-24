package com.example.mediaapplication.data.repository

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.mediaapplication.constants.MediaConstants
import com.example.mediaapplication.data.datasource.MediaDataSource
import com.example.mediaapplication.domain.model.MediaItem
import com.example.mediaapplication.domain.model.MediaType
import com.example.mediaapplication.domain.repository.MediaRepository
import java.io.File
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val mediaDataSource: MediaDataSource
) : MediaRepository {

    companion object {
        private const val TAG = "MediaRepository"
    }

    private val mediaTypeHandlers = mapOf<MediaType, (File) -> MediaItem>(
        MediaType.IMAGE to { file ->
            MediaItem(
                type = MediaType.IMAGE,
                title = file.name,
                metadata = mapOf("path" to file.absolutePath),
                thumbnail = createImageThumbnail(file),
                filePath = file.absolutePath
            )
        },
        MediaType.MUSIC to { file ->
            val metadata = extractMusicMetadata(file)
            val albumArt = extractAlbumArt(file)
            MediaItem(
                type = MediaType.MUSIC,
                title = metadata["title"] ?: file.name,
                metadata = metadata,
                thumbnail = albumArt,
                filePath = file.absolutePath
            )
        },
        MediaType.VIDEO to { file ->
            MediaItem(
                type = MediaType.VIDEO,
                title = file.name,
                metadata = mapOf("path" to file.absolutePath),
                thumbnail = extractVideoThumbnail(file),
                filePath = file.absolutePath
            )
        }
    )

    override fun loadMediaContent(): List<MediaItem> {
        val mediaList = mutableListOf<MediaItem>()

        val extensionToMediaType = mapOf(
            "jpg" to MediaType.IMAGE,
            "jpeg" to MediaType.IMAGE,
            "png" to MediaType.IMAGE,
            "mp3" to MediaType.MUSIC,
            "wav" to MediaType.MUSIC,
            "mp4" to MediaType.VIDEO,
            "mkv" to MediaType.VIDEO
        )

        mediaDataSource.listMediaFiles().forEach { file ->
            extensionToMediaType[file.extension.lowercase()]?.let { mediaType ->
                mediaTypeHandlers[mediaType]?.invoke(file)?.let { mediaItem ->
                    mediaList.add(mediaItem)
                }
            }
        }

        return mediaList
    }

    private fun createImageThumbnail(file: File): ImageBitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)

            val targetSize = MediaConstants.THUMBNAIL_TARGET_SIZE
            val scaleFactor = maxOf(
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

    private fun extractMusicMetadata(file: File): Map<String, String> {
        val metadata = mutableMapOf<String, String>()
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(file.absolutePath)

            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            if (title != null) metadata["title"] = title
            if (artist != null) metadata["artist"] = artist else metadata["artist"] = MediaConstants.DEFAULT_ARTIST
            if (album != null) metadata["album"] = album else metadata["album"] = MediaConstants.DEFAULT_ALBUM
            if (duration != null) {
                val durationInMs = duration.toLong()
                val minutes = durationInMs / 60000
                val seconds = (durationInMs % 60000) / 1000
                metadata["duration"] = String.format("%d:%02d", minutes, seconds)
            }
        } catch (e: Exception) {
            metadata["artist"] = MediaConstants.DEFAULT_ARTIST
            metadata["album"] = MediaConstants.DEFAULT_ALBUM
        } finally {
            retriever.release()
        }
        return metadata
    }

    private fun extractAlbumArt(file: File): ImageBitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)

            val albumArtBytes = retriever.embeddedPicture

            if (albumArtBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.size)
                bitmap?.asImageBitmap()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    private fun extractVideoThumbnail(file: File): ImageBitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)

            // extracting the first frame as thumbnail
            val bitmap = retriever.getFrameAtTime(0)

            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }
}
