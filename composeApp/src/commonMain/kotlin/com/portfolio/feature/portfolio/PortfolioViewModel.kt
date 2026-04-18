package com.portfolio.feature.portfolio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * KMP-friendly ViewModel (no AndroidX dependency) that drives the Portfolio screen
 * using a unidirectional MVI loop:  Intent -> Reducer -> State.
 */
class PortfolioViewModel(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {

    private val _state = MutableStateFlow(PortfolioState())
    val state: StateFlow<PortfolioState> = _state.asStateFlow()

    init {
        dispatch(PortfolioIntent.LoadData)
    }

    fun dispatch(intent: PortfolioIntent) {
        when (intent) {
            PortfolioIntent.LoadData, PortfolioIntent.Retry -> loadData()
            is PortfolioIntent.FilterProjects -> filter(intent.tab)
            is PortfolioIntent.OpenProject -> { /* handled by platform side-effects */ }
        }
    }

    private fun loadData() {
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            // Simulated repository call — replace with real data source.
            delay(300)
            val data = sampleProjects()
            _state.update {
                it.copy(
                    isLoading = false,
                    allProjects = data,
                    projects = applyFilter(data, it.selectedTab),
                )
            }
        }
    }

    private fun filter(tab: String) {
        _state.update {
            it.copy(
                selectedTab = tab,
                projects = applyFilter(it.allProjects, tab),
            )
        }
    }

    private fun applyFilter(all: List<Project>, tab: String): List<Project> =
        if (tab == "All") all else all.filter { tab in it.tools }

    private fun sampleProjects(): List<Project> = listOf(
        Project(
            id = "vault-fintech",
            title = "Vault — Encrypted Fintech App",
            description = "Production Android banking client serving 120K+ MAU. " +
                "Strict MVI core with unidirectional state, AES-256 (Tink) at-rest encryption, " +
                "biometric-gated sessions, and a fully offline-first transaction ledger. " +
                "Reduced p95 cold-start by 38% and crash-free sessions to 99.94%.",
            tools = listOf("Kotlin", "MVI", "Jetpack Compose", "Tink", "Hilt", "Room"),
            githubUrl = "https://github.com/example/vault-fintech",
            imageUrl = "https://images.unsplash.com/photo-1556742400-b5b7c5121f2e?w=1280&q=80",
        ),
        Project(
            id = "kmp-portfolio",
            title = "This Portfolio — KMP × Wasm",
            description = "The site you're reading. One Kotlin codebase shipping native Android, " +
                "native iOS and a Kotlin/Wasm web build via Compose Multiplatform. " +
                "Sub-300KB gzipped Wasm payload, 0dp-elevation design system, and a strict " +
                "MVI feature module reused across all three targets.",
            tools = listOf("Kotlin", "Compose", "Wasm", "MVI", "Coil 3"),
            githubUrl = "https://github.com/example/kmp-portfolio",
            imageUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=1280&q=80",
        ),
        Project(
            id = "flowgrid-oss",
            title = "FlowGrid — Open-Source Reactive Grid",
            description = "OSS Compose Multiplatform virtualised grid handling 1M+ cells at 120fps " +
                "via incremental layout and StateFlow-driven diffing. Shipped as a KMP library " +
                "with zero reflection, used in 30+ production apps. Featured in Android Weekly #612.",
            tools = listOf("Kotlin", "Compose", "Coroutines", "KMP", "Library"),
            githubUrl = "https://github.com/example/flowgrid",
            imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=1280&q=80",
        ),
    )
}

