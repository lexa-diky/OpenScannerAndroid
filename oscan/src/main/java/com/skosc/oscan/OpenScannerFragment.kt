package com.skosc.oscan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.osan_fragment.*
import java.util.concurrent.Executor

@Suppress("UNCHECKED_CAST")
class OpenScannerFragment<T> private constructor() : Fragment(), OpenScanner.Owner<T> {

    private val cameraExecutor by lazy(LazyThreadSafetyMode.NONE) { ContextCompat.getMainExecutor(requireContext()) }

    private val subOwner: OpenScanner.Owner<T> by lazy(LazyThreadSafetyMode.NONE) {
        ((parentFragment as? OpenScanner.Owner<*>)
            ?: activity as? OpenScanner.Owner<*>
            ?: error("OpenScannerFragment was attached not to ${OpenScanner.Owner::class.java}"))
                as OpenScanner.Owner<T>
    }

    override val openScanner: OpenScanner<T> get() = subOwner.openScanner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.osan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        val osca = OpenScannerCameraXAnalyzer(
            openScanner.executor,
            openScanner.scanner,
            subOwner::onScanResult
        )

        val analyzer = ImageAnalysis.Builder()
            .build()
            .apply { setAnalyzer(cameraExecutor, osca) }

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .apply { setSurfaceProvider(preview_view.createSurfaceProvider()) }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
            } catch(exc: Exception) {
                throw exc
            }
        }, cameraExecutor)
    }

    companion object {

        internal fun <T> newInstance(): Fragment = OpenScannerFragment<T>()
    }
}