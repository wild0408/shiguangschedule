package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.background
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
actual fun AndroidLiquidGlassTopBar(
    fraction: Float,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val f = fraction.coerceIn(0f, 1f)
    val backgroundColor = MiuixTheme.colorScheme.surface
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = f }
                .background(backgroundColor.copy(alpha = 0.18f + 0.52f * f))
        )
        content()
    }
}
