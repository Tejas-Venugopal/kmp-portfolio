package com.portfolio.platform

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import kotlinx.browser.document
import org.w3c.dom.HTMLImageElement

/**
 * Wasm/Web implementation.
 *
 * Compose renders everything onto a single <canvas>.  Coil can only deliver a static
 * bitmap snapshot of a GIF — it cannot drive frame-by-frame animation on the canvas.
 *
 * Solution:
 *  1. Inject a real <img> element (z-index 0) behind the canvas (z-index 1).
 *     The browser animates <img src="*.gif"> natively.
 *  2. Use BlendMode.Clear inside drawBehind to punch a transparent hole in the
 *     canvas at exactly the banner's position, letting the <img> show through.
 *  3. Track position with onGloballyPositioned — this fires on scroll too, so
 *     the <img> follows the Compose layout correctly and hides when off-screen.
 *
 * CORS note: <img> tags bypass CORS, so any public image URL works — unlike
 * Coil/Fetch which requires the server to send Access-Control-Allow-Origin headers.
 */
@Composable
actual fun AnimatedBanner(
    url: String,
    contentDescription: String?,
    modifier: Modifier,
) {
    val density = LocalDensity.current.density

    // ── Create the DOM <img> once ────────────────────────────────────────────
    val imgEl = remember {
        (document.createElement("img") as HTMLImageElement).apply {
            this.src = url
            alt = contentDescription ?: ""
            style.cssText = """
                position: fixed;
                object-fit: cover;
                z-index: 0;
                pointer-events: none;
                display: none;
                margin: 0;
                padding: 0;
            """.trimIndent()
            document.body!!.appendChild(this)
        }
    }

    // Update src if the URL changes; clean up on disposal
    DisposableEffect(url) {
        imgEl.src = url
        onDispose { imgEl.remove() }
    }

    // ── Invisible Spacer occupies the same space in the Compose layout ───────
    Spacer(
        modifier = Modifier
            // 1. Punch a transparent hole in the canvas FIRST (outermost draw)
            .drawBehind {
                drawRect(color = Color.Transparent, blendMode = BlendMode.Clear)
            }
            // 2. Then apply all caller modifiers (size, align, border line, …)
            .then(modifier)
            // 3. Sync the <img> position with the Compose layout on every frame
            .onGloballyPositioned { coords ->
                val pos  = coords.positionInWindow()
                val size = coords.size

                val cssLeft   = (pos.x / density).toInt()
                val cssTop    = (pos.y / density).toInt()
                val cssWidth  = (size.width  / density).toInt()
                val cssHeight = (size.height / density).toInt()

                // Hide when fully scrolled off the top of the viewport
                if (cssTop + cssHeight <= 0) {
                    imgEl.style.display = "none"
                } else {
                    imgEl.style.left    = "${cssLeft}px"
                    imgEl.style.top     = "${cssTop}px"
                    imgEl.style.width   = "${cssWidth}px"
                    imgEl.style.height  = "${cssHeight}px"
                    imgEl.style.display = "block"
                }
            },
    )
}
