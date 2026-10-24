package com.example.mediaapplication.data.datasource.external

import android.os.Environment
import com.example.mediaapplication.data.datasource.MediaDataSource
import java.io.File

class ExternalStorageMediaDataSource : MediaDataSource {
    override fun listMediaFiles(): List<File> {
        val supportedExtensions = setOf("jpg", "jpeg", "png", "mp3", "wav", "mp4", "mkv")

        val emulatorImageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return emulatorImageDir
            .listFiles()
            ?.filter { file ->
                file.isFile && supportedExtensions.contains(file.extension.lowercase())
            }
            ?: emptyList()
    }
}
