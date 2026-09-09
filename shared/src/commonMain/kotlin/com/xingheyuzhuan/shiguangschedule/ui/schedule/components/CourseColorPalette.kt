package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.xingheyuzhuan.shiguangschedule.data.model.DualColor
import com.xingheyuzhuan.shiguangschedule.data.model.ScheduleGridStyle

/**
 * NexioSchedule 的 Miuix 课程色板。旧版默认 pastel 色只作为存储兼容值，
 * Miuix 页面显示时解析为这组更高饱和度的颜色。
 */
internal val NEXIO_COURSE_COLORS = listOf(
    Color(0xFF4CAF50),
    Color(0xFF2196F3),
    Color(0xFFFF9800),
    Color(0xFFF44336),
    Color(0xFFE6B422),
    Color(0xFFE91E63),
    Color(0xFF00BCD4),
    Color(0xFF3F51B5),
    Color(0xFFAB47BC),
    Color(0xFF009688),
    Color(0xFF673AB7),
)

internal fun isLegacyDefaultCourseColor(index: Int, color: Color, isDark: Boolean): Boolean {
    val defaultPair = ScheduleGridStyle.DEFAULT_COLOR_MAPS.getOrNull(index) ?: return false
    return color == if (isDark) defaultPair.dark else defaultPair.light
}

/** Resolves the color actually shown by a course component for the selected UI style. */
internal fun resolveCourseColor(
    colorIndex: Int,
    colorMaps: List<DualColor>,
    isDark: Boolean,
    useMiuix: Boolean,
): Color {
    val index = colorIndex.coerceAtLeast(0)
    val configuredColor = colorMaps.getOrNull(index)?.let {
        if (isDark) it.dark else it.light
    }
    if (!useMiuix) {
        return configuredColor
            ?: colorMaps.firstOrNull()?.let { if (isDark) it.dark else it.light }
            ?: if (isDark) ScheduleGridStyle.DEFAULT_COLOR_MAPS.first().dark else ScheduleGridStyle.DEFAULT_COLOR_MAPS.first().light
    }

    val paletteIndex = index.mod(NEXIO_COURSE_COLORS.size)
    val legacyIndex = index.takeIf { it < ScheduleGridStyle.DEFAULT_COLOR_MAPS.size } ?: paletteIndex
    return when {
        configuredColor == null -> NEXIO_COURSE_COLORS[paletteIndex]
        isLegacyDefaultCourseColor(legacyIndex, configuredColor, isDark) -> NEXIO_COURSE_COLORS[paletteIndex]
        else -> configuredColor
    }
}

internal fun miuixCourseTextColor(color: Color, isDark: Boolean): Color {
    // Keep the course hue while moving the label away from the translucent card surface.
    return if (isDark) {
        lerp(color, Color.White, 0.48f).copy(alpha = 0.98f)
    } else {
        lerp(color, Color.Black, 0.38f).copy(alpha = 0.98f)
    }
}
