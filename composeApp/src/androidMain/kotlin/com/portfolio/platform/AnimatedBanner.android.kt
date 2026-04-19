package com.portfolio.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.gif.repeatCount
import coil3.request.ImageRequest
import coil3.request.crossfade

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
            .repeatCount(Int.MAX_VALUE)   // loop the GIF forever
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.FillBounds,  // fills bounds exactly — no cropping
        modifier = modifier,
    )
}

