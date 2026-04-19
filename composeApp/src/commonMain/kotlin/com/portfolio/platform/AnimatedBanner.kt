package com.portfolio.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific animated banner image.
 *
 * • Android / iOS  → Coil AsyncImage (coil-gif supplies the GIF decoder on Android)
 * • Wasm / Web     → native HTML <img> element so the browser animates the GIF itself
 */
@Composable
expect fun AnimatedBanner(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
)

