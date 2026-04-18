package com.portfolio.feature.portfolio

/**
 * All user/system intents that can mutate the [PortfolioState].
 */
sealed class PortfolioIntent {
    data object LoadData : PortfolioIntent()
    data class FilterProjects(val tab: String) : PortfolioIntent()
    data class OpenProject(val project: Project) : PortfolioIntent()
    data object Retry : PortfolioIntent()
}

