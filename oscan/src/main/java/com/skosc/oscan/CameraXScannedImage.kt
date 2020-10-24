package com.skosc.oscan

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

@SuppressLint("UnsafeExperimentalUsageError")
internal class CameraXScannedImage(private val imageProxy: ImageProxy) : ScannedImage {

    override val width: Int get() = imageProxy.width
    override val height: Int get() = imageProxy.height
    override val bytes: ByteArray by lazy { imageProxy.image!!.toByteArray() }
    override val format: Int get() = imageProxy.format

    init {
        requireNotNull(imageProxy.format)
    }

    override fun getPlane(index: Int): ByteArray {
        val buffer = imageProxy.planes[index].buffer
        val array = ByteArray(buffer.capacity())
        buffer.get(array)
        return array
    }

    private fun Image.toByteArray(): ByteArray {
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        return out.toByteArray()
    }
}
