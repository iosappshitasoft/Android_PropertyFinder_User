package com.app.propertyfinder.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Animated shimmer brush — reuse this in any shimmer composable. */
@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0E0E0),
            Color(0xFFF5F5F5),
            Color(0xFFE0E0E0)
        ),
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )
}

/** A single shimmer placeholder block. */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    radius: Dp = 8.dp,
    brush: Brush = shimmerBrush()
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(radius))
            .background(brush)
    )
}

/**
 * Shimmer skeleton that matches TransactionItemCard layout:
 *   - Title row (name + status badge)
 *   - Location row
 *   - Price row
 *   - Bottom spec row
 */
@Composable
fun TransactionItemShimmer(brush: Brush = shimmerBrush()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        // Title + badge row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerBox(modifier = Modifier.weight(0.6f), height = 16.dp, brush = brush)
            Spacer(Modifier.width(16.dp))
            ShimmerBox(modifier = Modifier.width(60.dp), height = 22.dp, radius = 4.dp, brush = brush)
        }

        Spacer(Modifier.height(10.dp))

        // Location row
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.5f), height = 12.dp, brush = brush)

        Spacer(Modifier.height(12.dp))

        // Price row
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.45f), height = 14.dp, brush = brush)

        Spacer(Modifier.height(16.dp))

        // Bottom spec row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(Color(0xFFF5F5F5))
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerBox(modifier = Modifier.weight(0.6f), height = 11.dp, brush = brush)
            ShimmerBox(modifier = Modifier.width(70.dp), height = 11.dp, brush = brush)
        }
    }
}

/**
 * Shimmer skeleton for the stats cards grid:
 *   - 2 cards in first row
 *   - 2 cards in second row (or 1 full-width for rent view)
 */
@Composable
fun TransactionStatsShimmer(brush: Brush = shimmerBrush(), isRentView: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Time period chips row placeholder
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(5) {
                ShimmerBox(
                    modifier = Modifier.width(60.dp),
                    height = 32.dp,
                    radius = 5.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF3A3A3A), Color(0xFF4A4A4A), Color(0xFF3A3A3A))
                    )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        if (!isRentView) {
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCardShimmer(modifier = Modifier.weight(1f))
                StatsCardShimmer(modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCardShimmer(modifier = Modifier.weight(1f))
                StatsCardShimmer(modifier = Modifier.weight(1f))
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCardShimmer(modifier = Modifier.weight(1f))
                StatsCardShimmer(modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            StatsCardShimmer(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun StatsCardShimmer(modifier: Modifier = Modifier) {
    val darkBrush = Brush.linearGradient(
        listOf(Color(0xFF3A3A3A), Color(0xFF4A4A4A), Color(0xFF3A3A3A))
    )
    Box(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF383838), Color(0xFF050505)))
            )
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.7f), height = 20.dp, brush = darkBrush)
            Spacer(Modifier.height(8.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.5f), height = 12.dp, brush = darkBrush)
        }
    }
}
