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
 * Wasm/Web implementation — animated GIF via a real HTML <img>.
 *
 * WHY NOT BlendMode.Clear:
 *   Compose draws every composable into an offscreen layer before compositing it
 *   onto the main WebGL surface.  BlendMode.Clear only clears the offscreen layer,
 *   not the final canvas pixels — so the canvas always remains opaque and hides
 *   any element placed behind it.
 *
 * ACTUAL FIX:
 *   Place the <img> element ABOVE the canvas (z-index 2 > canvas z-index 1).
 *   The browser animates <img src="*.gif"> natively — no Coil involved.
 *   A Spacer with the same modifier constraints keeps the Compose layout correct.
 *
 * NO-CROP:
 *   object-fit: fill  → stretches the image to fill the exact Compose bounds.
 *   Because the caller now passes aspectRatio(2000f/600f), the container already
 *   matches the GIF's natural proportions, so there is zero stretching or cropping.
 *
 * CORS:
 *   <img> tags are not subject to CORS, so any public URL works — unlike
 *   Coil's Fetch API which requires Access-Control-Allow-Origin headers.
 */
@Composable
actual fun AnimatedBanner(
    url: String,
    contentDescription: String?,
    modifier: Modifier,
) {
    val density = LocalDensity.current.density

    // ── Inject a <img> into the DOM once ────────────────────────────────────
    val imgEl: HTMLImageElement = remember {
        (document.createElement("img") as HTMLImageElement).apply {
            this.src    = url
            this.alt    = contentDescription ?: ""
            style.cssText = """
                position: fixed;
                object-fit: fill;
                z-index: 2;
                pointer-events: none;
                display: none;
                border-bottom: 1px solid #00FF88;
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
    // We use it as a hard ceiling so the banner never overlaps the navbar.
    var minTop by remember { mutableStateOf(-1) }

    // ── Invisible Spacer holds the layout space; syncs <img> position ────────
    Spacer(
        modifier = modifier
            .onGloballyPositioned { coords ->
                val pos    = coords.positionInWindow()
                val size   = coords.size
                val left   = (pos.x  / density).toInt()
                val top    = (pos.y  / density).toInt()
                val width  = (size.width  / density).toInt()
                val height = (size.height / density).toInt()

                // Capture the initial top (= navbar height) on the first valid layout
                if (minTop == -1 && top > 0) minTop = top

                val clampedMinTop = if (minTop > 0) minTop else 0
                // How many CSS px of the img are hidden above the clamp line
                val clipTopPx = maxOf(0, clampedMinTop - top)
                val visibleHeight = height - clipTopPx

                when {
                    // Fully scrolled behind / above the navbar → hide
                    visibleHeight <= 0 || top + height <= 0 -> {
                        imgEl.style.display = "none"
                    }
                    else -> {
                        imgEl.style.left     = "${left}px"
                        // Never let the img sit above the navbar
                        imgEl.style.top      = "${maxOf(clampedMinTop, top)}px"
                        imgEl.style.width    = "${width}px"
                        imgEl.style.height   = "${height}px"
                        // Clip the portion that has scrolled behind the navbar
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
