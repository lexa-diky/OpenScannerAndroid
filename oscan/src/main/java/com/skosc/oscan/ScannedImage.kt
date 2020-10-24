package com.skosc.oscan

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

interface ScannedImage {
    val width: Int
    val height: Int
    val bytes: ByteArray
    val format: Int

    fun getPlane(index: Int): ByteArray
}
