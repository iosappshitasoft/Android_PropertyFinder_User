package com.app.propertyfinder.ui.theme

import androidx.compose.ui.unit.dp

// =========================================================================
// NEW COMPONENT SIZING & DIMENSIONS (molumulk 2026 - Final Handoff Section 10 & 11)
// Dedicated token constants for native-style experience and component sizing.
// Delete legacy layout constants once full migration is complete.
// =========================================================================
object MolumulkDimens {
    // Top app bar height (excluding safe area)
    val TopAppBarHeight = 56.dp
    val TopAppBarHeightMax = 60.dp

    // Bottom navigation height (excluding safe area)
    val BottomNavHeight = 64.dp
    val BottomNavHeightMax = 72.dp

    // Inputs & Buttons
    val PrimaryButtonHeight = 52.dp
    val ButtonHeight = PrimaryButtonHeight
    val PrimaryButtonHeightMax = 56.dp
    val SearchFieldHeight = 52.dp
    val FilterChipHeight = 40.dp
    val FilterChipHeightMax = 44.dp

    // Corner Radii
    val CardCornerRadius = 16.dp
    val CardCornerRadiusMax = 20.dp
    val InputCornerRadius = 12.dp
    val InputCornerRadiusMax = 14.dp
    val PrimaryCtaRadius = 12.dp
    val PrimaryCtaRadiusMax = 16.dp

    // Spacing & Padding
    val ScreenHorizontalPadding = 16.dp
    val SectionGap = 24.dp
    val SectionGapMax = 32.dp

    // Touch Targets
    val MinTouchTarget = 44.dp

    // Card & Image Sizing
    val PropertyCardImageHeightMin = 220.dp
    val PropertyCardImageHeightMax = 260.dp
    val PropertyDetailsHeroRatio = 0.40f // 38-45% of first viewport

    // Icon stroke
    val IconStrokeWidth = 2.dp
}
