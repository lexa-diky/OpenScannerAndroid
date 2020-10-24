package com.skosc.oscan.zxing

import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.skosc.oscan.ImageScanner
import com.skosc.oscan.ScannedImage
import java.lang.Exception

class ZxingOpenScanner : ImageScanner<String?> {

    private val reader = MultiFormatReader()
    private var rotationStep = 0

    override fun scan(image: ScannedImage): String? {
        val bytes = image.getPlane(0)

        val rotatedImage = RotatedImage(bytes, image.width, image.height)

        rotateImageArray(rotatedImage, rotationStep * 90)
        rotationStep++
        if (rotationStep > 3) {
            rotationStep = 0
        }

        val source = PlanarYUVLuminanceSource(
            rotatedImage.byteArray,
            rotatedImage.width,
            rotatedImage.height,
            0,
            0,
            rotatedImage.width,
            rotatedImage.height,
            false
        )

        val binarizer = HybridBinarizer(source)
        val binaryBitmap = BinaryBitmap(binarizer)
        return try {
             reader.decode(binaryBitmap).text
        } catch (e: Exception) {
            return null
        }
    }

    private fun rotateImageArray(imageToRotate: RotatedImage, rotationDegrees: Int) {
        if (rotationDegrees == 0) return // no rotation
        if (rotationDegrees % 90 != 0) return // only 90 degree times rotations

        val width = imageToRotate.width
        val height = imageToRotate.height

        val rotatedData = ByteArray(imageToRotate.byteArray.size)
        for (y in 0 until height) { // we scan the array by rows
            for (x in 0 until width) {
                when (rotationDegrees) {
                    90 -> rotatedData[x * height + height - y - 1] =
                        imageToRotate.byteArray[x + y * width] // Fill from top-right toward left (CW)
                    180 -> rotatedData[width * (height - y - 1) + width - x - 1] =
                        imageToRotate.byteArray[x + y * width] // Fill from bottom-right toward up (CW)
                    270 -> rotatedData[y + x * height] =
                        imageToRotate.byteArray[y * width + width - x - 1] // The opposite (CCW) of 90 degrees
                }
            }
        }

        imageToRotate.byteArray = rotatedData

        if (rotationDegrees != 180) {
            imageToRotate.height = width
            imageToRotate.width = height
        }
    }

    private class RotatedImage(var byteArray: ByteArray, var width: Int, var height: Int)
}
