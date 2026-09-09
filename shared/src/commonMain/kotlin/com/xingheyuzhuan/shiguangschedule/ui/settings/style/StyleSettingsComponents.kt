package com.xingheyuzhuan.shiguangschedule.ui.settings.style

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.xingheyuzhuan.shiguangschedule.data.model.schedule_style.BorderTypeProto
import com.xingheyuzhuan.shiguangschedule.data.model.schedule_style.ScheduleModeProto
import com.xingheyuzhuan.shiguangschedule.ui.components.AdvancedColorPicker
import com.xingheyuzhuan.shiguangschedule.ui.components.ColorPickerConfig
import com.xingheyuzhuan.shiguangschedule.ui.schedule.MergedCourseBlock
import com.xingheyuzhuan.shiguangschedule.ui.schedule.WeeklyScheduleUiState
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.ScheduleGrid
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.ScheduleGridActions
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.ScheduleGridStyleComposed
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.ScheduleGridViewState
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.rememberScheduleGridState
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_confirm
import shiguangschedule.shared.generated.resources.action_reset
import shiguangschedule.shared.generated.resources.action_reset_style
import shiguangschedule.shared.generated.resources.border_type_dashed
import shiguangschedule.shared.generated.resources.border_type_solid
import shiguangschedule.shared.generated.resources.check_24px
import shiguangschedule.shared.generated.resources.desc_wallpaper_set
import shiguangschedule.shared.generated.resources.desc_wallpaper_unset
import shiguangschedule.shared.generated.resources.dialog_reset_message
import shiguangschedule.shared.generated.resources.dialog_reset_title
import shiguangschedule.shared.generated.resources.format_week_display
import shiguangschedule.shared.generated.resources.image_24px
import shiguangschedule.shared.generated.resources.label_border_type
import shiguangschedule.shared.generated.resources.label_corner_radius
import shiguangschedule.shared.generated.resources.label_course_text_color
import shiguangschedule.shared.generated.resources.label_day_header_height
import shiguangschedule.shared.generated.resources.label_font_scale
import shiguangschedule.shared.generated.resources.label_hide_date_under_day
import shiguangschedule.shared.generated.resources.label_hide_grid_lines
import shiguangschedule.shared.generated.resources.label_hide_location
import shiguangschedule.shared.generated.resources.label_hide_section_time
import shiguangschedule.shared.generated.resources.label_hide_teacher
import shiguangschedule.shared.generated.resources.label_inner_padding
import shiguangschedule.shared.generated.resources.label_none
import shiguangschedule.shared.generated.resources.label_opacity
import shiguangschedule.shared.generated.resources.label_outer_padding
import shiguangschedule.shared.generated.resources.label_page_text_color
import shiguangschedule.shared.generated.resources.label_range
import shiguangschedule.shared.generated.resources.label_remove_location_at
import shiguangschedule.shared.generated.resources.label_schedule_mode_24h
import shiguangschedule.shared.generated.resources.label_section_height
import shiguangschedule.shared.generated.resources.label_show_start_time
import shiguangschedule.shared.generated.resources.label_text_align_center_h
import shiguangschedule.shared.generated.resources.label_text_align_center_v
import shiguangschedule.shared.generated.resources.label_time_column_width
import shiguangschedule.shared.generated.resources.label_wallpaper
import shiguangschedule.shared.generated.resources.placeholder_input_value
import shiguangschedule.shared.generated.resources.preview_dark_mode
import shiguangschedule.shared.generated.resources.preview_light_mode
import shiguangschedule.shared.generated.resources.refresh_24px
import shiguangschedule.shared.generated.resources.status_not_set
import shiguangschedule.shared.generated.resources.style_category_color_scheme
import shiguangschedule.shared.generated.resources.style_category_course_block
import shiguangschedule.shared.generated.resources.style_category_grid_size
import shiguangschedule.shared.generated.resources.style_category_interface
import shiguangschedule.shared.generated.resources.title_dark_color_pool
import shiguangschedule.shared.generated.resources.title_light_color_pool
import kotlin.math.roundToInt
import kotlin.time.Clock

@Composable
fun SettingsListContent(
    currentStyle: ScheduleGridStyleComposed,
    viewModel: StyleSettingsViewModel,
    onWallpaperClick: () -> Unit,
    onPick: (isDark: Boolean, index: Int) -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(Res.string.dialog_reset_title)) },
            text = { Text(stringResource(Res.string.dialog_reset_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetStyleSettings()
                    showResetDialog = false
                }) { Text(stringResource(Res.string.action_confirm), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text(stringResource(Res.string.action_cancel)) }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        OutlinedButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
        ) {
            Text(stringResource(Res.string.action_reset_style))
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Text(stringResource(Res.string.style_category_interface), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        WallpaperItem(
            path = currentStyle.backgroundImagePath,
            onClick = onWallpaperClick,
            onLongClick = { viewModel.removeWallpaper() }
        )
        StyleSwitchItem(
            label = stringResource(Res.string.label_schedule_mode_24h),
            checked = currentStyle.scheduleMode == ScheduleModeProto.TIME_24H_MODE
        ) { isChecked ->
            val targetMode = if (isChecked) {
                ScheduleModeProto.TIME_24H_MODE
            } else {
                ScheduleModeProto.SECTION_MODE
            }
            viewModel.updateScheduleMode(targetMode)
        }
        StyleSwitchItem(stringResource(Res.string.label_hide_section_time), currentStyle.hideSectionTime) { viewModel.updateHideSectionTime(it) }
        StyleSwitchItem(stringResource(Res.string.label_hide_date_under_day), currentStyle.hideDateUnderDay) { viewModel.updateHideDateUnderDay(it) }
        StyleSwitchItem(label = stringResource(Res.string.label_hide_grid_lines), checked = currentStyle.hideGridLines) { viewModel.updateHideGridLines(it) }
        ColorPickerItem(
            label = stringResource(Res.string.label_page_text_color),
            currentColor = currentStyle.pageTextColor,
            onColorChanged = { viewModel.updatePageTextColor(it) },
            onReset = { viewModel.updatePageTextColor(null) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Text(stringResource(Res.string.style_category_grid_size), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        StyleSliderItem(stringResource(Res.string.label_section_height), currentStyle.sectionHeight.value, 40f..120f) { viewModel.updateSectionHeight(it) }
        StyleSliderItem(stringResource(Res.string.label_time_column_width), currentStyle.timeColumnWidth.value, 20f..80f) { viewModel.updateTimeColumnWidth(it) }
        StyleSliderItem(stringResource(Res.string.label_day_header_height), currentStyle.dayHeaderHeight.value, 30f..80f) { viewModel.updateDayHeaderHeight(it) }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Text(stringResource(Res.string.style_category_course_block), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        ColorPickerItem(
            label = stringResource(Res.string.label_course_text_color),
            currentColor = currentStyle.courseTextColor,
            onColorChanged = { viewModel.updateCourseTextColor(it) },
            onReset = { viewModel.updateCourseTextColor(null) }
        )
        StyleSwitchItem(stringResource(Res.string.label_show_start_time), currentStyle.showStartTime) { viewModel.updateShowStartTime(it) }
        StyleSwitchItem(stringResource(Res.string.label_hide_location), currentStyle.hideLocation) { viewModel.updateHideLocation(it) }
        StyleSwitchItem(stringResource(Res.string.label_hide_teacher), currentStyle.hideTeacher) { viewModel.updateHideTeacher(it) }
        StyleSwitchItem(stringResource(Res.string.label_remove_location_at), currentStyle.removeLocationAt) { viewModel.updateRemoveLocationAt(it) }
        StyleSwitchItem(stringResource(Res.string.label_text_align_center_h), currentStyle.textAlignCenterHorizontal) { viewModel.updateTextAlignCenterHorizontal(it) }
        StyleSwitchItem(stringResource(Res.string.label_text_align_center_v), currentStyle.textAlignCenterVertical) { viewModel.updateTextAlignCenterVertical(it) }
        BorderTypeSelector(currentStyle.borderType) { viewModel.updateBorderType(it) }

        StyleSliderItem(stringResource(Res.string.label_font_scale), currentStyle.fontScale, 0.5f..2.0f, 0.1f) { viewModel.updateCourseBlockFontScale(it) }
        StyleSliderItem(stringResource(Res.string.label_corner_radius), currentStyle.courseBlockCornerRadius.value, 0f..24f, 1f) { viewModel.updateCornerRadius(it) }
        StyleSliderItem(stringResource(Res.string.label_inner_padding), currentStyle.courseBlockInnerPadding.value, 0f..12f, 1f) { viewModel.updateInnerPadding(it) }
        StyleSliderItem(stringResource(Res.string.label_outer_padding), currentStyle.courseBlockOuterPadding.value, 0f..8f, 1f) { viewModel.updateOuterPadding(it) }
        StyleSliderItem(stringResource(Res.string.label_opacity), currentStyle.courseBlockAlpha, 0.1f..1f, 0.05f) { viewModel.updateAlpha(it) }


        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Text(stringResource(Res.string.style_category_color_scheme), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)

        ColorSchemeSection(
            title = stringResource(Res.string.title_light_color_pool),
            bgColor = lightColorScheme().surfaceContainerLow,
            isDarkSection = false,
            colors = currentStyle.courseColorMaps.map { it.light },
            onEditColor = { onPick(false, it) }
        )

        ColorSchemeSection(
            title = stringResource(Res.string.title_dark_color_pool),
            bgColor = darkColorScheme().surfaceContainerLow,
            isDarkSection = true,
            colors = currentStyle.courseColorMaps.map { it.dark },
            onEditColor = { onPick( true, it) }
        )
    }
}

@Composable
fun BorderTypeSelector(
    currentType: BorderTypeProto,
    onTypeChange: (BorderTypeProto) -> Unit
) {
    val types = listOf(
        BorderTypeProto.BORDER_TYPE_NONE to stringResource(Res.string.label_none),
        BorderTypeProto.BORDER_TYPE_SOLID to stringResource(Res.string.border_type_solid),
        BorderTypeProto.BORDER_TYPE_DASHED to stringResource(Res.string.border_type_dashed)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(Res.string.label_border_type), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().height(36.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            types.forEach { (type, label) ->
                val isSelected = currentType == type
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onTypeChange(type) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ColorSchemeSection(
    title: String,
    bgColor: Color,
    isDarkSection: Boolean,
    colors: List<Color>,
    onEditColor: (Int) -> Unit
) {
    val contentColor = if (isDarkSection) Color.White else Color.Black

    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(bgColor).padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = contentColor)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            colors.forEachIndexed { index, color ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(color).clickable { onEditColor(index) })
                    Text("${index + 1}", style = MaterialTheme.typography.labelSmall, color = contentColor.copy(0.6f), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun ColorPreviewBox(color: Color, isLightModeUI: Boolean) {
    Box(modifier = Modifier.fillMaxWidth().height(100.dp).padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp)).background(color), contentAlignment = Alignment.Center) {
        Text(
            text = if (isLightModeUI) stringResource(Res.string.preview_light_mode) else stringResource(Res.string.preview_dark_mode),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isLightModeUI) lightColorScheme().onSurface else Color.White
        )
    }
}

@Composable
fun ScheduleGridContent(
    style: ScheduleGridStyleComposed,
    demoUiState: WeeklyScheduleUiState
) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val localDates = remember(demoUiState.firstDayOfWeek) {
        val targetDayOfWeek = DayOfWeek.entries.getOrNull(demoUiState.firstDayOfWeek - 1) ?: DayOfWeek.MONDAY
        var startOfWeek = today
        while (startOfWeek.dayOfWeek != targetDayOfWeek) {
            startOfWeek = startOfWeek.minus(1, DateTimeUnit.DAY)
        }
        (0..6).map { startOfWeek.plus(it, DateTimeUnit.DAY) }
    }
    val currentYearString = remember(today) { today.year.toString() }
    val dummyDates = remember(localDates) {
        localDates.map {
            val month = it.month.number.toString().padStart(2, '0')
            val day = it.day.toString().padStart(2, '0')
            "$month/$day"
        }
    }
    val dynamicTodayIndex = remember(localDates) { localDates.indexOf(today) }
    val previewWeekStr = stringResource(Res.string.format_week_display, 1)
    val previewScrollState = rememberScrollState()
    val gridState = rememberScheduleGridState(gridScrollState = previewScrollState)
    val gridViewState = remember(dummyDates, currentYearString, demoUiState, dynamicTodayIndex, previewWeekStr) {
        ScheduleGridViewState(
            dates = dummyDates,
            currentYear = currentYearString,
            currentWeek = previewWeekStr,
            timeSlots = demoUiState.timeSlots,
            mergedCourses = demoUiState.currentMergedCourses,
            showWeekends = demoUiState.showWeekends,
            todayIndex = dynamicTodayIndex,
            firstDayOfWeek = demoUiState.firstDayOfWeek,
            currentSectionIndex = -1
        )
    }

    val gridActions = remember {
        object : ScheduleGridActions {
            override fun onCourseBlockClicked(block: MergedCourseBlock) {}
            override fun onGridCellClicked(day: Int, section: Int) {}
            override fun onTimeSlotClicked() {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (style.backgroundImagePath.isNotEmpty()) {
            AsyncImage(
                model = style.backgroundImagePath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )
        }

        ScheduleGrid(
            state = gridState,
            viewState = gridViewState,
            actions = gridActions,
            style = style,
            modifier = Modifier
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(
                    orientation = Orientation.Vertical,
                    state = ScrollableState { delta ->
                        previewScrollState.dispatchRawDelta(-delta)
                        delta
                    },
                    flingBehavior = ScrollableDefaults.flingBehavior()
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {},
                        onTap = {}
                    )
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleSliderItem(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    stepValue: Float = 1f,
    onValueChange: (Float) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val isIntegerStep = stepValue >= 1f

    fun formatValue(v: Float): String {
        return if (isIntegerStep) {
            "${v.toInt()}"
        } else {
            val rounded = (v * 10).roundToInt() / 10.0
            if (rounded % 1.0 == 0.0) "${rounded.toInt()}.0" else "$rounded"
        }
    }

    val steps = remember(range, stepValue) {
        if (stepValue > 0f) {
            ((range.endInclusive - range.start) / stepValue).toInt() - 1
        } else 0
    }

    if (showDialog) {
        var textFieldValue by remember { mutableStateOf(formatValue(value)) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(label) },
            text = {
                Column {
                    Text(
                        text = "${stringResource(Res.string.label_range)}: ${formatValue(range.start)} - ${formatValue(range.endInclusive)}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = { input ->
                            if (isIntegerStep) {
                                if (input.all { it.isDigit() }) textFieldValue = input
                            } else {
                                if (input.all { it.isDigit() || it == '.' }) textFieldValue = input
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (isIntegerStep) KeyboardType.Number
                            else KeyboardType.Decimal
                        ),
                        placeholder = { Text(stringResource(Res.string.placeholder_input_value)) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val newValue = textFieldValue.toFloatOrNull()
                    if (newValue != null) {
                        val clampedValue = newValue.coerceIn(range.start, range.endInclusive)
                        val steppedValue = if (stepValue > 0f) {
                            val count = ((clampedValue - range.start) / stepValue).roundToInt()
                            range.start + count * stepValue
                        } else clampedValue

                        onValueChange(steppedValue)
                        showDialog = false
                    }
                }) {
                    Text(stringResource(Res.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { showDialog = true }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = formatValue(value),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = if (steps > 0) steps else 0,
            modifier = Modifier.height(32.dp),
            thumb = {
                Surface(
                    modifier = Modifier.size(16.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 1.dp,
                    border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {}
            },
            track = { sliderState ->
                Box(
                    modifier = Modifier.fillMaxWidth().height(22.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.fillMaxWidth().height(22.dp),
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            inactiveTrackColor = Color.Transparent
                        ),
                        thumbTrackGapSize = 0.dp,
                        trackInsideCornerSize = 0.dp
                    )
                }
            }
        )
    }
}

@Composable
fun StyleSwitchItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent = if (checked) {
                { Icon(modifier = Modifier.size(SwitchDefaults.IconSize), imageVector = vectorResource(Res.drawable.check_24px), contentDescription = null) }
            } else null
        )
    }
}

@Composable
fun WallpaperItem(
    path: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val hasWallpaper = path.isNotEmpty()

    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(Res.string.label_wallpaper), style = MaterialTheme.typography.bodyMedium)
            Text(
                text = if (hasWallpaper) stringResource(Res.string.desc_wallpaper_set)
                else stringResource(Res.string.desc_wallpaper_unset),
                style = MaterialTheme.typography.labelSmall,
                color = if (hasWallpaper) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = vectorResource(Res.drawable.image_24px),
            contentDescription = null,
            tint = if (hasWallpaper) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerItem(
    label: String,
    currentColor: Color?,
    onColorChanged: (Color) -> Unit,
    onReset: () -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { showSheet = true }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)

        if (currentColor != null) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(currentColor)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
            )
        } else {
            Text(
                text = stringResource(Res.string.status_not_set),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 40.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    TextButton(onClick = {
                        onReset()
                        showSheet = false
                    }) {
                        Icon(vectorResource(Res.drawable.refresh_24px), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(Res.string.action_reset))
                    }
                }
                val pickerInitialColor = currentColor ?: MaterialTheme.colorScheme.primary

                AdvancedColorPicker(
                    initialColor = pickerInitialColor,
                    onColorChanged = onColorChanged,
                    config = ColorPickerConfig(
                        showAlpha = false,
                        showInputMode = true
                    )
                )
            }
        }
    }
}

@Composable
fun MiuixSettingsListContent(
    currentStyle: ScheduleGridStyleComposed,
    viewModel: StyleSettingsViewModel,
    onWallpaperClick: () -> Unit,
    onPick: (isDark: Boolean, index: Int) -> Unit,
) {
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        top.yukonga.miuix.kmp.basic.TextButton(
            text = stringResource(Res.string.action_reset_style),
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.error),
        )

        MiuixStyleGroup(stringResource(Res.string.style_category_interface)) {
            MiuixWallpaperItem(currentStyle.backgroundImagePath, onWallpaperClick) { viewModel.removeWallpaper() }
            MiuixStyleSwitchItem(stringResource(Res.string.label_schedule_mode_24h), currentStyle.scheduleMode == ScheduleModeProto.TIME_24H_MODE) {
                viewModel.updateScheduleMode(if (it) ScheduleModeProto.TIME_24H_MODE else ScheduleModeProto.SECTION_MODE)
            }
            MiuixStyleSwitchItem(stringResource(Res.string.label_hide_section_time), currentStyle.hideSectionTime, viewModel::updateHideSectionTime)
            MiuixStyleSwitchItem(stringResource(Res.string.label_hide_date_under_day), currentStyle.hideDateUnderDay, viewModel::updateHideDateUnderDay)
            MiuixStyleSwitchItem(stringResource(Res.string.label_hide_grid_lines), currentStyle.hideGridLines, viewModel::updateHideGridLines)
            MiuixColorPickerItem(stringResource(Res.string.label_page_text_color), currentStyle.pageTextColor, viewModel::updatePageTextColor) { viewModel.updatePageTextColor(null) }
        }

        MiuixStyleGroup(stringResource(Res.string.style_category_grid_size)) {
            MiuixStyleSliderItem(stringResource(Res.string.label_section_height), currentStyle.sectionHeight.value, 40f..120f, 1f, viewModel::updateSectionHeight)
            MiuixStyleSliderItem(stringResource(Res.string.label_time_column_width), currentStyle.timeColumnWidth.value, 20f..80f, 1f, viewModel::updateTimeColumnWidth)
            MiuixStyleSliderItem(stringResource(Res.string.label_day_header_height), currentStyle.dayHeaderHeight.value, 30f..80f, 1f, viewModel::updateDayHeaderHeight)
        }

        MiuixStyleGroup(stringResource(Res.string.style_category_course_block)) {
            MiuixColorPickerItem(stringResource(Res.string.label_course_text_color), currentStyle.courseTextColor, viewModel::updateCourseTextColor) { viewModel.updateCourseTextColor(null) }
            MiuixStyleSwitchItem(stringResource(Res.string.label_show_start_time), currentStyle.showStartTime, viewModel::updateShowStartTime)
            MiuixStyleSwitchItem(stringResource(Res.string.label_hide_location), currentStyle.hideLocation, viewModel::updateHideLocation)
            MiuixStyleSwitchItem(stringResource(Res.string.label_hide_teacher), currentStyle.hideTeacher, viewModel::updateHideTeacher)
            MiuixStyleSwitchItem(stringResource(Res.string.label_remove_location_at), currentStyle.removeLocationAt, viewModel::updateRemoveLocationAt)
            MiuixStyleSwitchItem(stringResource(Res.string.label_text_align_center_h), currentStyle.textAlignCenterHorizontal, viewModel::updateTextAlignCenterHorizontal)
            MiuixStyleSwitchItem(stringResource(Res.string.label_text_align_center_v), currentStyle.textAlignCenterVertical, viewModel::updateTextAlignCenterVertical)
            MiuixBorderTypeSelector(currentStyle.borderType, viewModel::updateBorderType)
            MiuixStyleSliderItem(stringResource(Res.string.label_font_scale), currentStyle.fontScale, 0.5f..2f, 0.1f, viewModel::updateCourseBlockFontScale)
            MiuixStyleSliderItem(stringResource(Res.string.label_corner_radius), currentStyle.courseBlockCornerRadius.value, 0f..24f, 1f, viewModel::updateCornerRadius)
            MiuixStyleSliderItem(stringResource(Res.string.label_inner_padding), currentStyle.courseBlockInnerPadding.value, 0f..12f, 1f, viewModel::updateInnerPadding)
            MiuixStyleSliderItem(stringResource(Res.string.label_outer_padding), currentStyle.courseBlockOuterPadding.value, 0f..8f, 1f, viewModel::updateOuterPadding)
            MiuixStyleSliderItem(stringResource(Res.string.label_opacity), currentStyle.courseBlockAlpha, 0.1f..1f, 0.05f, viewModel::updateAlpha)
        }

        top.yukonga.miuix.kmp.basic.SmallTitle(stringResource(Res.string.style_category_color_scheme))
        MiuixColorSchemeSection(stringResource(Res.string.title_light_color_pool), false, currentStyle.courseColorMaps.map { it.light }) { onPick(false, it) }
        MiuixColorSchemeSection(stringResource(Res.string.title_dark_color_pool), true, currentStyle.courseColorMaps.map { it.dark }) { onPick(true, it) }
        Spacer(Modifier.height(16.dp))
    }

    top.yukonga.miuix.kmp.overlay.OverlayDialog(
        title = stringResource(Res.string.dialog_reset_title),
        summary = stringResource(Res.string.dialog_reset_message),
        show = showResetDialog,
        onDismissRequest = { showResetDialog = false },
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), { showResetDialog = false }, Modifier.weight(1f))
            top.yukonga.miuix.kmp.basic.TextButton(
                text = stringResource(Res.string.action_confirm),
                onClick = { viewModel.resetStyleSettings(); showResetDialog = false },
                modifier = Modifier.weight(1f),
                colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.error),
            )
        }
    }
}

@Composable
private fun MiuixStyleGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    top.yukonga.miuix.kmp.basic.SmallTitle(title)
    top.yukonga.miuix.kmp.basic.Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(vertical = 4.dp),
        content = content,
    )
}

@Composable
private fun MiuixStyleSwitchItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    top.yukonga.miuix.kmp.basic.BasicComponent(
        modifier = Modifier.fillMaxWidth(),
        title = label,
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        endActions = { top.yukonga.miuix.kmp.basic.Switch(checked, onCheckedChange) },
        onClick = { onCheckedChange(!checked) },
    )
}

@Composable
private fun MiuixWallpaperItem(path: String, onClick: () -> Unit, onLongClick: () -> Unit) {
    val hasWallpaper = path.isNotEmpty()
    Row(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = onClick, onLongClick = onLongClick).padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.label_wallpaper), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body1)
            top.yukonga.miuix.kmp.basic.Text(
                if (hasWallpaper) stringResource(Res.string.desc_wallpaper_set) else stringResource(Res.string.desc_wallpaper_unset),
                style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote1,
                color = if (hasWallpaper) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary else top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }
        top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.image_24px), null, tint = if (hasWallpaper) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary else top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
    }
}

@Composable
private fun MiuixStyleSliderItem(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    stepValue: Float,
    onValueChange: (Float) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var input by remember(value, showDialog) { mutableStateOf(if (stepValue >= 1f) value.toInt().toString() else "%.2f".format(value).trimEnd('0').trimEnd('.')) }
    val steps = (((range.endInclusive - range.start) / stepValue).roundToInt() - 1).coerceAtLeast(0)
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            top.yukonga.miuix.kmp.basic.Text(label, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body1)
            top.yukonga.miuix.kmp.basic.TextButton(input, { showDialog = true })
        }
        top.yukonga.miuix.kmp.basic.Slider(value, onValueChange, Modifier.fillMaxWidth(), valueRange = range, steps = steps)
    }
    top.yukonga.miuix.kmp.overlay.OverlayDialog(title = label, summary = "${stringResource(Res.string.label_range)}: ${range.start} - ${range.endInclusive}", show = showDialog, onDismissRequest = { showDialog = false }) {
        Column(Modifier.fillMaxWidth()) {
            top.yukonga.miuix.kmp.basic.TextField(input, { input = it.filter { char -> char.isDigit() || char == '.' } }, Modifier.fillMaxWidth(), label = stringResource(Res.string.placeholder_input_value), singleLine = true)
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), { showDialog = false }, Modifier.weight(1f))
                top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_confirm), {
                    input.toFloatOrNull()?.let { raw ->
                        val clamped = raw.coerceIn(range.start, range.endInclusive)
                        val snapped = range.start + ((clamped - range.start) / stepValue).roundToInt() * stepValue
                        onValueChange(snapped); showDialog = false
                    }
                }, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
            }
        }
    }
}

@Composable
private fun MiuixBorderTypeSelector(currentType: BorderTypeProto, onTypeChange: (BorderTypeProto) -> Unit) {
    val types = listOf(BorderTypeProto.BORDER_TYPE_NONE, BorderTypeProto.BORDER_TYPE_SOLID, BorderTypeProto.BORDER_TYPE_DASHED)
    val labels = listOf(stringResource(Res.string.label_none), stringResource(Res.string.border_type_solid), stringResource(Res.string.border_type_dashed))
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.label_border_type), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body1)
        top.yukonga.miuix.kmp.basic.TabRow(labels, types.indexOf(currentType).coerceAtLeast(0), { onTypeChange(types[it]) }, Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun MiuixColorPickerItem(label: String, currentColor: Color?, onColorChanged: (Color) -> Unit, onReset: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    top.yukonga.miuix.kmp.basic.BasicComponent(
        modifier = Modifier.fillMaxWidth(),
        title = label,
        summary = if (currentColor == null) stringResource(Res.string.status_not_set) else null,
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        endActions = { Box(Modifier.size(28.dp).clip(CircleShape).background(currentColor ?: top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainerHigh)) },
        onClick = { showDialog = true },
    )
    top.yukonga.miuix.kmp.overlay.OverlayDialog(title = label, show = showDialog, onDismissRequest = { showDialog = false }) {
        Column(Modifier.fillMaxWidth()) {
            AdvancedColorPicker(currentColor ?: top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary, onColorChanged, ColorPickerConfig(showAlpha = false, showInputMode = true))
            top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_reset), { onReset(); showDialog = false }, Modifier.fillMaxWidth(), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
        }
    }
}

@Composable
private fun MiuixColorSchemeSection(title: String, dark: Boolean, colors: List<Color>, onEditColor: (Int) -> Unit) {
    top.yukonga.miuix.kmp.basic.Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(16.dp),
        colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = if (dark) Color(0xFF242424) else Color.White),
    ) {
        top.yukonga.miuix.kmp.basic.Text(title, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body1, color = if (dark) Color.White else Color.Black)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            colors.forEachIndexed { index, color ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(44.dp).clip(CircleShape).background(color).clickable { onEditColor(index) })
                    top.yukonga.miuix.kmp.basic.Text("${index + 1}", style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote1, color = if (dark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f))
                }
            }
        }
    }
}
