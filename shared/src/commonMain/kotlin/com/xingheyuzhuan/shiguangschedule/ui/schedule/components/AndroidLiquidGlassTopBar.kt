package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AndroidLiquidGlassTopBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
