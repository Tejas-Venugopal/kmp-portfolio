package com.portfolio.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Circular profile avatar.
 *
 * • Android / iOS  → Coil AsyncImage clipped to CircleShape with an emerald border
 * • Wasm / Web     → HTML <img> at z-index 3 (above the banner at z-index 2)
 *                    so the avatar correctly overlaps the animated banner
 */
@Composable
expect fun AvatarImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
)

