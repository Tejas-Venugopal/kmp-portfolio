package com.portfolio

import androidx.compose.ui.window.ComposeUIViewController
import com.portfolio.ui.screen.MainScreen
import com.portfolio.ui.theme.PortfolioTheme

/**
 * Exposed to Swift as `MainViewControllerKt.MainViewController()`.
 * Use it from `iOSApp.swift` inside a `UIViewControllerRepresentable`.
 */
fun MainViewController() = ComposeUIViewController {
    PortfolioTheme {
        MainScreen()
    }
}

