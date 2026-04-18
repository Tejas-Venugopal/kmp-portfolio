package com.portfolio.platform

/**
 * Opens [url] in the platform's preferred browser/handler.
 * Implementations live in androidMain / iosMain / wasmJsMain.
 */
expect fun openUrl(url: String)

