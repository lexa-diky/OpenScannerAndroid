package com.skosc.oscan

import java.io.Serializable
import kotlin.Result

fun interface ImageScanner<T> {

    fun scan(image: ScannedImage): T
}