package com.skosc.oscan

import android.os.Build
import androidx.fragment.app.Fragment
import java.util.concurrent.Executor

class OpenScanner<T> internal constructor(
    val executor: Executor,
    val scanner: ImageScanner<T>
) {

    val fragment: Fragment get() = OpenScannerFragment.newInstance<T>()

    fun builder(): OpenScannerBuilder<T> = OpenScannerBuilder<T>()
        .executor(executor)
        .scanner(scanner)

    interface Owner <T> {

        val openScanner: OpenScanner<T>

        fun onScanResult(result: T): Unit = Unit
    }

    companion object {

        private const val VERSION = "indev"

        fun <T> builder(): OpenScannerBuilder<T> = OpenScannerBuilder()
    }
}

fun <T> OpenScanner.Owner<T>.buildScanner(modifications: OpenScannerBuilder<T>.() -> Unit): OpenScanner<T> {
    val builder = OpenScanner.builder<T>()
    modifications(builder)
    return builder.build()
}