package com.portfolio.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.portfolio.feature.portfolio.PortfolioIntent
import com.portfolio.feature.portfolio.PortfolioState
import com.portfolio.feature.portfolio.PortfolioViewModel
import com.portfolio.feature.portfolio.Project
import com.portfolio.platform.openUrl
import com.portfolio.ui.theme.ElectricEmerald
import com.portfolio.ui.theme.MinimalTokens

// ─── Nav destinations ─────────────────────────────────────────────────────────

private enum class NavTab { Profile, Work, Contact }

// ─── Root screen ──────────────────────────────────────────────────────────────

@Composable
fun MainScreen(viewModel: PortfolioViewModel = remember { PortfolioViewModel() }) {
    val state by viewModel.state.collectAsState()
    val tabs = NavTab.entries
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }

    // Tab tap → scroll pager
    var pendingTab by remember { mutableStateOf<NavTab?>(null) }
    LaunchedEffect(pendingTab) {
        pendingTab?.let { tab ->
            pagerState.animateScrollToPage(tabs.indexOf(tab))
            pendingTab = null
        }
    }

    // Pager swipe → update selected tab indicator
    val selectedTab = tabs[pagerState.currentPage]

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            StickyNavBar(
                selected = selectedTab,
                onSelect = { pendingTab = it },
            )
        },
        bottomBar = { Footer() },
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            beyondViewportPageCount = 1,
        ) { page ->
            when (tabs[page]) {
                NavTab.Profile -> ProfileSection()
                NavTab.Work    -> WorkSection(state, viewModel::dispatch)
                NavTab.Contact -> ContactSection()
            }
        }
    }
}

// ─── Sticky navigation bar ────────────────────────────────────────────────────

@Composable
private fun StickyNavBar(selected: NavTab, onSelect: (NavTab) -> Unit) {
    val systemTop = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .border(width = MinimalTokens.BorderWidth, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            .padding(top = systemTop)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
            NavTab.entries.forEach { tab ->
            val isSelected = tab == selected
            Text(
                text = tab.name.uppercase(),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable { onSelect(tab) }
                    .padding(vertical = 4.dp),
            )
        }
    }
}

// ─── Profile section ──────────────────────────────────────────────────────────

@Composable
fun ProfileSection() {
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 40.dp),
    ) {
        val isDesktop = maxWidth >= 800.dp
        if (isDesktop) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.Top,
            ) {
                ProfileBio(Modifier.weight(1f))
                StatsWidget(Modifier.width(320.dp))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(40.dp)) {
                ProfileBio(Modifier.fillMaxWidth())
                StatsWidget(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ProfileBio(modifier: Modifier = Modifier) {
    Column(modifier) {
        // Profile picture — circular editorial cutout
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data("https://avatars.githubusercontent.com/u/583231") // replace with your own
                .crossfade(true)
                .crossfade(durationMillis = 400)
                .build(),
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(MinimalTokens.BorderWidth, MaterialTheme.colorScheme.primary, CircleShape),
        )
        Spacer(Modifier.height(28.dp))
        Text(
            text = "Hey, I'm Tejas.\nA KMP & Android Engineer.",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
            fontSize = 42.sp,
            lineHeight = 48.sp,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "I'm passionate about clean architecture, unidirectional data flow, and shipping " +
                    "pixel-perfect experiences across Android, iOS and Web from a single Kotlin codebase. " +
                    "3+ years turning complex product requirements into maintainable, performant apps.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            fontFamily = FontFamily.Monospace,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            modifier = Modifier.widthIn(max = 560.dp),
        )
        Spacer(Modifier.height(32.dp))
        OutlinedButton(
            onClick = { openUrl("mailto:tejasvenugopal.offical@example.com") },
            border = BorderStroke(MinimalTokens.BorderWidth, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text(
                "Contact Me",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun StatsWidget(modifier: Modifier = Modifier) {
    val stats = listOf(
        "3+"     to "Years Exp",
        "15+"    to "Projects",
        "100%"   to "KMP Focus",
        "120fps" to "Target Perf",
    )
    Column(
        modifier
            .border(MinimalTokens.BorderWidth, MaterialTheme.colorScheme.primary)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Text(
            "// stats",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 20.dp),
        )
        stats.chunked(2).forEach { pair ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                pair.forEach { (value, label) ->
                    Column(Modifier.weight(1f).padding(bottom = 24.dp)) {
                        Text(
                            text = value,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black,
                            fontSize = 36.sp,
                        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

// ─── Work section (existing project grid) ─────────────────────────────────────

@Composable
fun WorkSection(
    state: PortfolioState,
    onIntent: (PortfolioIntent) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        if (state.isLoading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    Modifier.fillMaxWidth().padding(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        items(state.projects, key = { it.id }) { project ->
            ProjectCard(
                project = project,
                onClick = {
                    onIntent(PortfolioIntent.OpenProject(project))
                    openUrl(project.githubUrl)
                },
            )
        }
    }
}

// ─── Contact section ──────────────────────────────────────────────────────────

@Composable
fun ContactSection() {
    val links = listOf(
        "LinkedIn" to "https://www.linkedin.com/in/tejas-v-71b3aa23b?utm_source=share_via&utm_content=profile&utm_medium=member_ios",
        "GitHub"   to "https://github.com/Tejas-Venugopal",
        "Email"    to "mailto:tejasvenugopal.official@example.com",
    )
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 48.dp),
    ) {
        Text(
            text = "Let's Connect.",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
            fontSize = 56.sp,
            lineHeight = 60.sp,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "I'm actively open to new Android & KMP engineering roles — full-time or contract. " +
                    "Whether it's a startup moving fast or a team tackling hard architectural problems, " +
                    "I'd love to hear about it.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            fontFamily = FontFamily.Monospace,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            modifier = Modifier.widthIn(max = 640.dp),
        )
        Spacer(Modifier.height(48.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            links.forEach { (label, url) ->
                ContactLink(label = label, url = url)
            }
        }
    }
}

@Composable
private fun ContactLink(label: String, url: String) {
    var pressed by remember { mutableStateOf(false) }
    Box(
        Modifier
            .border(
                width = MinimalTokens.BorderWidth,
                color = if (pressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )
            .background(Color.Transparent)
            .clickable(
                onClick = { openUrl(url) },
                onClickLabel = label,
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = if (pressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        )
    }
}

// ─── Project card ─────────────────────────────────────────────────────────────

@Composable
private fun ProjectCard(project: Project, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MinimalTokens.Elevation,
            pressedElevation = MinimalTokens.Elevation,
            hoveredElevation = MinimalTokens.Elevation,
            focusedElevation = MinimalTokens.Elevation,
        ),
        border = BorderStroke(MinimalTokens.BorderWidth, MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(24.dp)) {
            project.imageUrl?.let { url ->
                ProjectImage(url = url)
                Spacer(Modifier.height(16.dp))
            }
            Text(
                text = project.title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = project.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                project.tools.forEach { tool ->
                    Box(
                        Modifier
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(
                            tool,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

// ─── Footer ───────────────────────────────────────────────────────────────────

@Composable
private fun Footer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf("Android", "iOS", "Web (Wasm)").forEach { label ->
            Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// ─── Shared image composable ──────────────────────────────────────────────────

@Composable
fun ProjectImage(url: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .border(MinimalTokens.BorderWidth, MaterialTheme.colorScheme.primary)
            .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.Center,
    ) {
        val painter = coil3.compose.rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(url)
                .crossfade(true)
                .crossfade(durationMillis = 350)
                .build(),
        )
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(url)
                .crossfade(true)
                .crossfade(durationMillis = 350)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        val painterState = painter.state.collectAsState().value
        val isLoading = painterState is coil3.compose.AsyncImagePainter.State.Loading ||
                painterState is coil3.compose.AsyncImagePainter.State.Empty
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(36.dp),
            )
        }
    }
}
