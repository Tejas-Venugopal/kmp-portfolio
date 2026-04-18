package com.portfolio.platform

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Holds the application [Context] needed to launch a browser Intent from
 * shared (non-Composable) code. Initialise this from your `Application`
 * class:  `AppContextHolder.context = applicationContext`.
 */
object AppContextHolder {
    lateinit var context: Context
}

actual fun openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    AppContextHolder.context.startActivity(intent)
}

