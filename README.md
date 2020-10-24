# Open Scanner Android

## Get Started

Inherent Activity/Fragment/... from OpenScanner.Owner

Implement openScanner property
``` kotlin
override val openScanner = buildScanner {
    scanner = ZxingOpenScanner()
    executor = OpenScannerSimpleCoroutineExecutor(this@MainActivity)
}
```

To receive scanner result override
``` kotlin
override fun onScanResult(result: String?) {
    if (result != null) {
        scanner_result.text = result
    }
}
```