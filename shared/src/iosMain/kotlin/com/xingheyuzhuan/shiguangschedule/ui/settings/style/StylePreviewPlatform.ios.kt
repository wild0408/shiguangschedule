package com.xingheyuzhuan.shiguangschedule.ui.settings.style

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xingheyuzhuan.shiguangschedule.ui.schedule.WeeklyScheduleUiState
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.ScheduleGridStyleComposed

@Composable
actual fun MiuixStylePreview(
    style: ScheduleGridStyleComposed,
    demoUiState: WeeklyScheduleUiState,
    modifier: Modifier,
) {
    ScheduleGridContent(style, demoUiState)
}
