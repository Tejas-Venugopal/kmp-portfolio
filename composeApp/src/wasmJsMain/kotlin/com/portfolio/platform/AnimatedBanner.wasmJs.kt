package com.portfolio.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * On Wasm the Compose renderer draws everything onto a <canvas> — Coil can only deliver
 * a static bitmap frame from a GIF.  We work around this by falling back to a standard
 * Coil AsyncImage; the browser will at least show the first frame cleanly.
 *
 * If full GIF animation is required on Web, replace the canvas-based window with a
 * mixed HTML/Compose layout and position a real <img> element underneath.
 */
@Composable
actual fun AnimatedBanner(
    url: String,
    contentDescription: String?,
    modifier: Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

