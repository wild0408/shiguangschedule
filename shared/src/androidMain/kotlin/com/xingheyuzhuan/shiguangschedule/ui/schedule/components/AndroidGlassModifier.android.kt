package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

actual fun Modifier.androidLiquidGlass(fraction: Float): Modifier {
    val f = fraction.coerceIn(0f, 1f)
    return this
        .graphicsLayer {
            // Android-specific approximation of NexioSchedule's progressive backdrop.
            alpha = 0.92f + 0.08f * f
        }
        .then(if (f > 0.02f) Modifier.blur((2f + 8f * f).dp) else Modifier)
}
