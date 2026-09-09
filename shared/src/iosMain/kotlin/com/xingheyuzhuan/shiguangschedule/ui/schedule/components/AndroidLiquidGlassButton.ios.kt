package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

@Composable actual fun AndroidLiquidGlassButton(fraction: Float, onClick: () -> Unit, modifier: Modifier, content: @Composable () -> Unit) = androidx.compose.foundation.layout.Box(modifier.clickable(onClick = onClick), contentAlignment = androidx.compose.ui.Alignment.Center) { content() }
