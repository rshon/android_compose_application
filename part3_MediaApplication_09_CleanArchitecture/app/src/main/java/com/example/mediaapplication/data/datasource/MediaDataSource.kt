package com.example.mediaapplication.data.datasource

import java.io.File

interface MediaDataSource {
    fun listMediaFiles(): List<File>
}
