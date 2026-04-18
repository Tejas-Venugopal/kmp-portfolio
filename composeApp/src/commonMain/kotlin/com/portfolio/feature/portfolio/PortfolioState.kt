package com.portfolio.feature.portfolio

/**
 * Immutable UI state for the Portfolio screen (MVI-style).
 */
data class PortfolioState(
    val isLoading: Boolean = false,
    val projects: List<Project> = emptyList(),
    val allProjects: List<Project> = emptyList(),
    val selectedTab: String = "All",
    val error: String? = null,
)

