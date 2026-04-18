package com.portfolio

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.portfolio.ui.screen.MainScreen
import com.portfolio.ui.theme.PortfolioTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Ensures the Compose canvas fills the browser viewport completely.
    val rootId = "composeApplication"
    document.title = "Android & KMP Engineer — Portfolio"

    CanvasBasedWindow(canvasElementId = rootId, title = "Portfolio") {
        PortfolioTheme(darkTheme = true) {
            MainScreen()
        }
    }
}

