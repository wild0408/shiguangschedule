package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable actual fun AndroidLiquidGlassTopBar(fraction: Float, modifier: Modifier, content: @Composable () -> Unit) = androidx.compose.foundation.layout.Box(modifier) { content() }
