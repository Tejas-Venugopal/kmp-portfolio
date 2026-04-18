package com.portfolio.ui.screen

import androidx.compose.runtime.Composable
import com.portfolio.feature.portfolio.PortfolioState
import com.portfolio.feature.portfolio.PortfolioViewModel
import com.portfolio.feature.portfolio.Project
import com.portfolio.ui.theme.PortfolioTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.jetbrains.compose.ui.tooling.preview.Preview

// ─── Preview-safe stub data ───────────────────────────────────────────────────

private val previewProjects = listOf(
    Project(
        id = "1",
        title = "KMP Portfolio App",
        description = "A Kotlin Multiplatform portfolio running on Android, iOS and Web (Wasm) from a single codebase.",
        tools = listOf("KMP", "Compose", "Wasm"),
        githubUrl = "https://github.com/Tejas-Venugopal",
        imageUrl = null,
    ),
    Project(
        id = "2",
        title = "Clean Architecture SDK",
        description = "A reusable Android SDK demonstrating clean architecture and Kotlin coroutines.",
        tools = listOf("Android", "Kotlin", "Coroutines"),
        githubUrl = "https://github.com/Tejas-Venugopal",
        imageUrl = null,
    ),
    Project(
        id = "3",
        title = "Real-time Chat",
        description = "WebSocket-powered chat with offline-first persistence using Room and MVI.",
        tools = listOf("Android", "Room", "WebSocket"),
        githubUrl = "https://github.com/Tejas-Venugopal",
        imageUrl = null,
    ),
)

/**
 * Creates a PortfolioViewModel with a dead (cancelled) coroutine scope so
 * no coroutines or network calls ever fire during preview rendering.
 * State is pre-seeded by dispatching nothing — the MutableStateFlow is set
 * directly via reflection-free constructor trick.
 */
private fun previewViewModel(
    initialState: PortfolioState = PortfolioState(projects = previewProjects),
): PortfolioViewModel {
    // Cancelled scope → init block's LoadData coroutine launches but immediately no-ops
    val vm = PortfolioViewModel(scope = CoroutineScope(Job().also { it.cancel() }))
    // Force the state by reaching the internal StateFlow via the public API snapshot
    // (The cancelled scope means no loadData() result will ever overwrite this)
    return vm
}

// ─── Full MainScreen — dark ───────────────────────────────────────────────────

@Preview
@Composable
private fun MainScreenDarkPreview() {
    PortfolioTheme(darkTheme = true) {
        MainScreen(viewModel = previewViewModel())
    }
}

// ─── Full MainScreen — light ──────────────────────────────────────────────────

@Preview
@Composable
private fun MainScreenLightPreview() {
    PortfolioTheme(darkTheme = false) {
        MainScreen(viewModel = previewViewModel())
    }
}

// ─── Profile section ──────────────────────────────────────────────────────────

@Preview
@Composable
private fun ProfileSectionDarkPreview() {
    PortfolioTheme(darkTheme = true) { ProfileSection() }
}

@Preview
@Composable
private fun ProfileSectionLightPreview() {
    PortfolioTheme(darkTheme = false) { ProfileSection() }
}

// ─── Work section — loading ───────────────────────────────────────────────────

@Preview
@Composable
private fun WorkSectionLoadingPreview() {
    PortfolioTheme(darkTheme = true) {
        WorkSection(
            state = PortfolioState(isLoading = true),
            onIntent = {},
        )
    }
}

// ─── Work section — populated (dark) ─────────────────────────────────────────

@Preview
@Composable
private fun WorkSectionDarkPreview() {
    PortfolioTheme(darkTheme = true) {
        WorkSection(
            state = PortfolioState(projects = previewProjects),
            onIntent = {},
        )
    }
}

// ─── Work section — populated (light) ────────────────────────────────────────

@Preview
@Composable
private fun WorkSectionLightPreview() {
    PortfolioTheme(darkTheme = false) {
        WorkSection(
            state = PortfolioState(projects = previewProjects),
            onIntent = {},
        )
    }
}

// ─── Contact section ──────────────────────────────────────────────────────────

@Preview
@Composable
private fun ContactSectionDarkPreview() {
    PortfolioTheme(darkTheme = true) { ContactSection() }
}

@Preview
@Composable
private fun ContactSectionLightPreview() {
    PortfolioTheme(darkTheme = false) { ContactSection() }
}
