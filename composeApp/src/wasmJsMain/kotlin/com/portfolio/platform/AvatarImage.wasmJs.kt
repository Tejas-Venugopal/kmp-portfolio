package com.portfolio.platform

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import kotlinx.browser.document
import org.w3c.dom.HTMLImageElement

/**
 * Wasm/Web — renders the avatar as an HTML <img> at z-index 3, above the
 * animated banner <img> (z-index 2) and the Compose canvas (z-index 1).
 * This ensures the avatar circle visually overlaps the banner correctly,
 * matching the layout that Android/iOS achieve on a single canvas.
 */
@Composable
actual fun AvatarImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier,
) {
    val density = LocalDensity.current.density

    val imgEl: HTMLImageElement = remember {
        (document.createElement("img") as HTMLImageElement).apply {
            this.src = url
            alt     = contentDescription ?: ""
            style.cssText = """
                position: fixed;
                border-radius: 50%;
                object-fit: cover;
                z-index: 3;
                pointer-events: none;
                display: none;
                border: 1px solid #00FF88;
                box-sizing: border-box;
                margin: 0;
                padding: 0;
            """.trimIndent()
            document.body!!.appendChild(this)
        }
    }

    DisposableEffect(url) {
        imgEl.src = url
        onDispose { imgEl.remove() }
    }

    // The first positive top value equals the navbar height in CSS px.
    // We use it as a hard ceiling so the avatar never overlaps the navbar.
    var minTop by remember { mutableStateOf(-1) }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { coords ->
                val pos    = coords.positionInWindow()
                val size   = coords.size
                val left   = (pos.x  / density).toInt()
                val top    = (pos.y  / density).toInt()
                val width  = (size.width  / density).toInt()
                val height = (size.height / density).toInt()

                if (minTop == -1 && top > 0) minTop = top

                val clampedMinTop = if (minTop > 0) minTop else 0
                val clipTopPx     = maxOf(0, clampedMinTop - top)
                val visibleHeight = height - clipTopPx

                when {
                    visibleHeight <= 0 || top + height <= 0 -> {
                        imgEl.style.display = "none"
                    }
                    else -> {
                        imgEl.style.left     = "${left}px"
                        imgEl.style.top      = "${maxOf(clampedMinTop, top)}px"
                        imgEl.style.width    = "${width}px"
                        imgEl.style.height   = "${height}px"
                        if (clipTopPx > 0)
                            imgEl.style.setProperty("clip-path", "inset(${clipTopPx}px 0 0 0)")
                        else
                            imgEl.style.removeProperty("clip-path")
                        imgEl.style.display  = "block"
                    }
                }
            },
    )
}
