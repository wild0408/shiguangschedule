package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AndroidLiquidGlassButton(
    fraction: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
