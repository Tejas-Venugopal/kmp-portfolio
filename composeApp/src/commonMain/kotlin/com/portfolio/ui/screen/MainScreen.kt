@file:OptIn(ExperimentalLayoutApi::class)

package com.portfolio.ui.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
import com.portfolio.platform.AnimatedBanner
import com.portfolio.platform.openUrl
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
    // Avatar metrics — used to calculate the overlap offset
    val avatarSize   = 120.dp
    val avatarOverlap = avatarSize / 2   // 60dp hangs below the banner

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Banner + overlapping avatar ──────────────────────────────────────
        val emerald   = MaterialTheme.colorScheme.primary
        val borderPx  = with(androidx.compose.ui.platform.LocalDensity.current) {
            MinimalTokens.BorderWidth.toPx()
        }
        Box(
            Modifier
                .fillMaxWidth()
                // Reserve banner height PLUS the half-avatar that hangs below
                .height(120.dp + avatarOverlap),
        ) {
            // Banner
            AnimatedBanner(
                url = "https://1.bp.blogspot.com/-7A4WynwLsMw/XbBpCXG8fHI/AAAAAAAAMt4/uOa1bpLskYgrwGbllhSu2SDj_Mig8SXJQCLcBGAsYHQ/s1600/2000_600px.gif",
                contentDescription = "Profile banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .align(Alignment.TopStart)
                    .drawBehind {
                        drawLine(
                            color = emerald,
                            start = androidx.compose.ui.geometry.Offset(0f, size.height - borderPx / 2),
                            end   = androidx.compose.ui.geometry.Offset(size.width, size.height - borderPx / 2),
                            strokeWidth = borderPx,
                        )
                    },
            )

            // Profile picture — overlaps banner by avatarOverlap (60dp)
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data("https://avatars.githubusercontent.com/Tejas-Venugopal")
                    .crossfade(true)
                    .crossfade(durationMillis = 400)
                    .build(),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 32.dp)
                    .size(avatarSize)
                    .align(Alignment.BottomStart)
                    .clip(CircleShape)
                    .border(MinimalTokens.BorderWidth, emerald, CircleShape),
            )
        }

        // ── Bio + Stats ─────────────────────────────────────────────────────
        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                // Top padding = small gap below the avatar (no need to re-add avatarOverlap,
                // the Box already reserved that space)
                .padding(horizontal = 32.dp)
                .padding(top = 16.dp, bottom = 40.dp),
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
}

@Composable
private fun ProfileBio(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = "Hey, I'm Tejas.\nAn Android, kotlin & KMP Engineer.",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
            fontSize = 42.sp,
            lineHeight = 48.sp,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "3+ years building production Android apps at Welldoc." +
                    "Passionate about clean architecture, MVI, MVVM and Jetpack Compose. "+
                    "Currently shipping across Android, iOS and Web from a single Kotlin codebase ",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            fontFamily = FontFamily.Monospace,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            modifier = Modifier.widthIn(max = 560.dp),
        )
        Spacer(Modifier.height(32.dp))
        OutlinedButton(
            onClick = { openUrl("mailto:tejasvenugopal.official@gmail.com") },
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
        "3+"    to "Years Exp",
        "5+"   to "Projects",
        "100%"  to "MVI",
        "2.0"   to "Kotlin Ver",
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
                    if (project.githubUrl.isNotEmpty()) openUrl(project.githubUrl)
                },
            )
        }
    }
}

// ─── Contact section ──────────────────────────────────────────────────────────

@Composable
fun ContactSection() {
    val links = listOf(
        "LinkedIn" to "https://www.linkedin.com/in/tejas-v-71b3aa23b/",
        "GitHub"   to "https://github.com/Tejas-Venugopal",
        "Email"    to "mailto:tejasvenugopal.official@gmail.com",
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
            text = "Having an interesting Android or KMP project" +
                    " I'd love to hear about it.",
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
        Spacer(Modifier.height(16.dp))
        Row {
            ContactLink(
                label = "Download Resume",
                url = "https://drive.google.com/file/d/13FAq2jSo2JgCIryaPvWrlqCEBfLzJEyj/view?usp=drive_link",
            )
        }
    }
}

@Composable
private fun ContactLink(label: String, url: String) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Box(
        Modifier
            .border(
                width = MinimalTokens.BorderWidth,
                color = if (pressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )
            .background(Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
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
        Column(Modifier.padding(24.dp).fillMaxHeight()) {
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
            // Render description; bold any segment that starts with "Company:"
            val descriptionText = buildAnnotatedString {
                val boldLabel = "Company:"
                val text = project.description
                val idx = text.indexOf(boldLabel)
                if (idx == -1) {
                    append(text)
                } else {
                    append(text.substring(0, idx))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(boldLabel)
                    }
                    append(text.substring(idx + boldLabel.length))
                }
            }
            Text(
                text = descriptionText,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )
            // Absorbs any extra height (e.g. in equal-height grid rows) above the chips
            Spacer(Modifier.height(20.dp))
            Spacer(Modifier.weight(1f))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
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
        androidx.compose.foundation.Image(
            painter = painter,
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
