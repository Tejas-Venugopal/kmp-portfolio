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
    internal val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
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
            id = "welldoc",
            title = "Welldoc — Lilly Health",
            description = "Production Android healthcare app for diabetes management " +
                "built for Eli Lilly patients (Lilly Health) at Welldoc . Owned "+
                "and drove migration of core module from Java/MVP to Kotlin + Jetpack " +
                "Compose with StateBridge/MVVM architecture and StateFlow . " +
                "Contributing to live releases serving real patients (2023 - Present).",
            tools = listOf("Kotlin", "Jetpack Compose", "StateBridge/MVVM", "StateFlow"),
            githubUrl = "",
            imageUrl = null,
        ),
        Project(
            id = "kmp-portfolio",
            title = "This Portfolio — KMP × Wasm",
            description = "A developer portfolio built entirely in Kotlin using Compose Multiplatform. " +
                "One codebase shipping to Android, iOS and Web (Wasm). " +
                "Built with MVI architecture, Kotlin 2.0, Coil 3 for image loading, and Ktor 3.0 " +
                "for networking. Sub-300KB gzipped Wasm payload.",
            tools = listOf("Kotlin 2.0", "KMP", "Compose", "Wasm", "MVI", "Ktor"),
            githubUrl = "https://github.com/Tejas-Venugopal/kmp-portfolio",
            imageUrl = null,
        ),
        Project(
            id = "resume-parser",
            title = "Resume Parser Using NLP",
            description = "Award-winning internship project at VCNR Technologies. " +
                "Python + NLP based resume parsing and analysis system. Recognised " +
                "at 5th National Conference (NCVCS-2023) and Innovate2K23 " +
                "Prototype Project Exhibition.",
            tools = listOf("Python", "NLP", "Machine Learning", "AI"),
            githubUrl = "https://github.com/Tejas-Venugopal/Resume-parser-using-NLP",
            imageUrl = null,
        ),
    )
}

