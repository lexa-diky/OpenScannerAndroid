package com.skosc.oscan.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.skosc.oscan.*
import com.skosc.oscan.zxing.ZxingOpenScanner
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OpenScanner.Owner<String?> {

    override val openScanner = buildScanner {
        scanner = ZxingOpenScanner()
        executor = OpenScannerSimpleCoroutineExecutor(this@MainActivity)
        filter = Filter<String?> { it != null } + Filter { it.isNullOrBlank() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .add(R.id.scanner_frame, openScanner.fragment)
            .commit()
    }

    override fun onScanResult(result: String?) {
        if (result != null) {
            scanner_result.text = result
        }
    }
}