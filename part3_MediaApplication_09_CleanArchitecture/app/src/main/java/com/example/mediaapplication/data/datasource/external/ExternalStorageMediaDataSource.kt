package com.example.mediaapplication.data.datasource.external

import android.content.Context
import android.os.Environment
import com.example.mediaapplication.data.datasource.MediaDataSource
import java.io.File

class ExternalStorageMediaDataSource() : MediaDataSource {
    override fun listMediaFiles(): List<File> {
        val extensionToMediaType = setOf("jpg", "jpeg", "png", "mp3", "wav", "mp4", "mkv")

        val emulatorImageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return emulatorImageDir
            .listFiles()
            ?.filter { file ->
                file.isFile && extensionToMediaType.contains(file.extension.lowercase())
            }
            ?: emptyList()
    }
}