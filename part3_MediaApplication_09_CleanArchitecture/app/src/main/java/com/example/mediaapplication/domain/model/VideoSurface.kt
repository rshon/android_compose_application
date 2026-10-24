package com.example.mediaapplication.domain.model

interface VideoSurface {
    /**
     * surface ID
     */
    val id: String

    /**
     * checks surface is valid
     */
    fun isValid(): Boolean

    /**
     * releases surface
     */
    fun release()

    /**
     * surface size
     */
    fun getSize(): Pair<Int, Int>?
}
