package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.graphicsLayer
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
actual fun AndroidLiquidGlassButton(
    fraction: Float,
    onClick: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val f = fraction.coerceIn(0f, 1f)
    val surfaceColor = MiuixTheme.colorScheme.surfaceContainerHigh
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .graphicsLayer { alpha = 0.96f + 0.04f * f }
            .background(surfaceColor.copy(alpha = 0.12f + 0.78f * f), CircleShape),
        contentAlignment = Alignment.Center
    ) { content() }
}
