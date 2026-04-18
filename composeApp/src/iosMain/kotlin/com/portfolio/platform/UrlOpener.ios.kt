package com.portfolio.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    val app = UIApplication.sharedApplication
    if (app.canOpenURL(nsUrl)) {
        // Use the modern API; the trailing-lambda completion handler can be null.
        app.openURL(nsUrl, options = emptyMap<Any?, Any?>(), completionHandler = null)
    }
}

