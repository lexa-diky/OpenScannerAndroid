package com.skosc.oscan

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.concurrent.Executor

internal class OpenScannerCameraXAnalyzer<T>(
    private val executor: Executor,
    private val scanner: ImageScanner<T>,
    private val filter: Filter<T>,
    private val callback: (T) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        executor.execute {
            val scanned = scanner.scan(CameraXScannedImage(image))
            if (filter.filter(scanned)) {
                callback(scanned)
            }
            image.close()
        }
    }
}