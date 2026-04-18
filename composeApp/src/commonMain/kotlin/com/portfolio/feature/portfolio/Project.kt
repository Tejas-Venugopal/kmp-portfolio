package com.portfolio.feature.portfolio

/**
 * Domain model representing a single portfolio project.
 */
data class Project(
    val id: String,
    val title: String,
    val description: String,
    val tools: List<String>,
    val githubUrl: String,
    val imageUrl: String? = null,
)

