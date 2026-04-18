package com.portfolio.ui.theme

import androidx.compose.ui.graphics.Color

// Minimalist base
val PureBlack = Color(0xFF000000)
val PureWhite = Color(0xFFFFFFFF)
val ElectricEmerald = Color(0xFF00FF88)
val DeepCharcoal = Color(0xFF121212)

// Convenience aliases
val BorderEmerald = ElectricEmerald
val OnDarkText = PureWhite
val OnLightText = PureBlack

// ── Light Theme (Jewel Emerald — ink on grey) ─────────────────────────────────
// Background: warm light grey — not pure white, avoids neon "vibration"
val EmeraldPrimaryLight      = Color(0xFF00563B)   // deep ink-emerald (dark on light)
val EmeraldOnPrimaryLight    = Color(0xFFFFFFFF)
val EmeraldContainerLight    = Color(0xFFB2F0D0)   // soft mint container
val EmeraldBackgroundLight   = Color(0xFFF2F4F2)   // warm light grey (not white)
val EmeraldOnBackgroundLight = Color(0xFF0F1510)   // near-black with green undertone
val EmeraldSurfaceLight      = Color(0xFFEFF1EF)   // slightly darker grey for cards/surfaces
val EmeraldOnSurfaceLight    = Color(0xFF0F1510)
val EmeraldSecondaryLight    = Color(0xFFDFE8E2)   // cool grey-mint for chips/tags
val EmeraldOutlineLight      = Color(0xFF00563B)   // same as primary — consistent ink tone

// ── Dark Theme (Electric Emerald — neon on dark) ──────────────────────────────
val EmeraldPrimaryDark       = Color(0xFF00FF88)   // electric neon — reads fine on dark
val EmeraldOnPrimaryDark     = Color(0xFF003921)
val EmeraldContainerDark     = Color(0xFF005232)
val EmeraldBackgroundDark    = Color(0xFF0D0F0E)   // near-black with green tint
val EmeraldOnBackgroundDark  = Color(0xFFE8F5EE)   // soft white (not pure) — easier on eyes
val EmeraldSurfaceDark       = Color(0xFF141716)   // slightly lighter than bg for cards
val EmeraldOnSurfaceDark     = Color(0xFFE8F5EE)
val EmeraldSecondaryDark     = Color(0xFF1A1F1C)   // dark chip/tag background
val EmeraldOutlineDark       = Color(0xFF00FF88)   // neon accent border
