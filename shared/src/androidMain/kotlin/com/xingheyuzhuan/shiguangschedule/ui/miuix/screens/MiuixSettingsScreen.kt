package com.xingheyuzhuan.shiguangschedule.ui.miuix.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.ui.components.LocalNavigationHostPadding
import com.xingheyuzhuan.shiguangschedule.ui.settings.SettingsViewModel
import com.xingheyuzhuan.shiguangschedule.ui.miuix.components.MiuixDatePickerDialog
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.rememberSharedScrollBehavior
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.chrome.HyperGlassTopBar
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.utils.overScrollVertical
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_confirm
import shiguangschedule.shared.generated.resources.chevron_right_24px
import shiguangschedule.shared.generated.resources.date_format_year_month_day
import shiguangschedule.shared.generated.resources.day_of_week_monday
import shiguangschedule.shared.generated.resources.day_of_week_sunday
import shiguangschedule.shared.generated.resources.desc_course_conversion
import shiguangschedule.shared.generated.resources.desc_course_management
import shiguangschedule.shared.generated.resources.desc_current_week_manual
import shiguangschedule.shared.generated.resources.desc_first_day_of_week
import shiguangschedule.shared.generated.resources.desc_manage_course_tables
import shiguangschedule.shared.generated.resources.desc_more_options
import shiguangschedule.shared.generated.resources.desc_notification_settings
import shiguangschedule.shared.generated.resources.desc_personalization
import shiguangschedule.shared.generated.resources.desc_quick_actions
import shiguangschedule.shared.generated.resources.desc_set_start_date
import shiguangschedule.shared.generated.resources.desc_show_non_current_week
import shiguangschedule.shared.generated.resources.desc_show_weekends
import shiguangschedule.shared.generated.resources.desc_time_slot_customization
import shiguangschedule.shared.generated.resources.desc_total_weeks
import shiguangschedule.shared.generated.resources.dialog_title_manual_set_week
import shiguangschedule.shared.generated.resources.dialog_title_select_total_weeks
import shiguangschedule.shared.generated.resources.dialog_title_set_first_day_of_week
import shiguangschedule.shared.generated.resources.item_course_conversion
import shiguangschedule.shared.generated.resources.item_course_management
import shiguangschedule.shared.generated.resources.item_current_week
import shiguangschedule.shared.generated.resources.item_first_day_of_week
import shiguangschedule.shared.generated.resources.item_more_options
import shiguangschedule.shared.generated.resources.item_personalization
import shiguangschedule.shared.generated.resources.item_quick_actions
import shiguangschedule.shared.generated.resources.item_set_start_date
import shiguangschedule.shared.generated.resources.item_show_non_current_week
import shiguangschedule.shared.generated.resources.item_show_weekends
import shiguangschedule.shared.generated.resources.item_time_slot_customization
import shiguangschedule.shared.generated.resources.item_total_weeks
import shiguangschedule.shared.generated.resources.more_horiz_24px
import shiguangschedule.shared.generated.resources.section_title_advanced_features
import shiguangschedule.shared.generated.resources.section_title_general_settings
import shiguangschedule.shared.generated.resources.status_current_week_format
import shiguangschedule.shared.generated.resources.status_not_set
import shiguangschedule.shared.generated.resources.status_set_start_date_first
import shiguangschedule.shared.generated.resources.status_total_weeks_format
import shiguangschedule.shared.generated.resources.title_course_notification_settings
import shiguangschedule.shared.generated.resources.title_manage_course_tables
import shiguangschedule.shared.generated.resources.title_schedule_settings
import shiguangschedule.shared.generated.resources.title_vacation
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.NumberPicker
import top.yukonga.miuix.kmp.window.WindowDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val SETTING_PADDING = 16.dp
private val SECTION_SPACING = 16.dp
private val ITEM_SPACING = 16.dp

@Composable
fun MiuixSettingsScreen(
    onNavigate: (Destination) -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val background = MiuixTheme.colorScheme.surface
    val scrollBehavior = rememberSharedScrollBehavior()
    val backdrop = rememberLayerBackdrop { drawRect(background); drawContent() }
    val direction = LocalLayoutDirection.current
    val hostPadding = LocalNavigationHostPadding.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = background,
        topBar = {
            HyperGlassTopBar(
                title = stringResource(Res.string.title_schedule_settings),
                backdrop = backdrop,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { scaffoldPadding ->
        Box(Modifier.fillMaxSize().background(background).layerBackdrop(backdrop)) {
            if (!uiState.isReady) {
                Box(modifier = Modifier.fillMaxSize().padding(scaffoldPadding))
            } else {
                val appSettings = uiState.appSettings
                val courseTableConfig = uiState.courseConfig
                val displayCurrentWeek = uiState.currentWeek

                val showWeekends = courseTableConfig?.showWeekends ?: false
                val semesterStartDateString = courseTableConfig?.semesterStartDate
                val semesterTotalWeeks = courseTableConfig?.semesterTotalWeeks ?: 20
                val firstDayOfWeekInt = courseTableConfig?.firstDayOfWeek ?: DayOfWeek.MONDAY.isoDayNumber

                val semesterStartDate: LocalDate? = remember(semesterStartDateString) {
                    semesterStartDateString?.let {
                        try {
                            LocalDate.parse(it)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }

                var showTotalWeeksDialog by remember { mutableStateOf(false) }
                var showManualWeekDialog by remember { mutableStateOf(false) }
                var showDatePickerModal by remember { mutableStateOf(false) }
                var showFirstDayOfWeekDialog by remember { mutableStateOf(false) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .overScrollVertical()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(SECTION_SPACING),
                    contentPadding = PaddingValues(
                        start = scaffoldPadding.calculateLeftPadding(direction) + 20.dp,
                        top = scaffoldPadding.calculateTopPadding() + 12.dp,
                        end = scaffoldPadding.calculateRightPadding(direction) + 20.dp,
                        bottom = scaffoldPadding.calculateBottomPadding() + hostPadding.calculateBottomPadding() + 20.dp,
                    )
                ) {
                    item {
                        GeneralSettingsSection(
                            showNonCurrentWeek = appSettings.showNonCurrentWeekCourses,
                            onShowNonCurrentWeekChanged = { isChecked -> viewModel.onShowNonCurrentWeekChanged(isChecked) },
                            showWeekends = showWeekends,
                            onShowWeekendsChanged = { isChecked -> viewModel.onShowWeekendsChanged(isChecked) },
                            semesterStartDate = semesterStartDate,
                            semesterTotalWeeks = semesterTotalWeeks,
                            firstDayOfWeekInt = firstDayOfWeekInt,
                            displayCurrentWeek = displayCurrentWeek,
                            onSemesterStartDateClick = { showDatePickerModal = true },
                            onSemesterTotalWeeksClick = { showTotalWeeksDialog = true },
                            onManualWeekClick = { showManualWeekDialog = true },
                            onFirstDayOfWeekClick = { showFirstDayOfWeekDialog = true },
                            onQuickActionsClick = { onNavigate(Destination.QuickActions) }
                        )
                    }
                    item {
                        AdvancedSettingsSection(onNavigate = onNavigate)
                    }
                }

                if (showDatePickerModal) {
            MiuixDatePickerDialog(
                title = stringResource(Res.string.item_set_start_date),
                        onDateSelected = { selectedDateMillis ->
                            viewModel.onSemesterStartDateSelected(selectedDateMillis)
                        },
                        onDismiss = { showDatePickerModal = false }
                    )
                }

                if (showTotalWeeksDialog) {
                    NumberPickerDialog(
                        title = stringResource(Res.string.dialog_title_select_total_weeks),
                        range = 1..30,
                        initialValue = semesterTotalWeeks,
                        onDismiss = { showTotalWeeksDialog = false },
                        onConfirm = { selectedWeeks ->
                            viewModel.onSemesterTotalWeeksSelected(selectedWeeks)
                            showTotalWeeksDialog = false
                        }
                    )
                }

                if (showManualWeekDialog) {
                    ManualWeekPickerDialog(
                        totalWeeks = semesterTotalWeeks,
                        currentWeek = displayCurrentWeek,
                        onDismiss = { showManualWeekDialog = false },
                        onConfirm = { weekNumber ->
                            viewModel.onCurrentWeekManuallySet(weekNumber)
                            showManualWeekDialog = false
                        }
                    )
                }

                if (showFirstDayOfWeekDialog) {
                    DayOfWeekPickerDialog(
                        initialDayOfWeekInt = firstDayOfWeekInt,
                        onDismiss = { showFirstDayOfWeekDialog = false },
                        onConfirm = { selectedDayInt ->
                            viewModel.onFirstDayOfWeekSelected(selectedDayInt)
                            showFirstDayOfWeekDialog = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * 通用设置卡片
 */
@Composable
private fun GeneralSettingsSection(
    showNonCurrentWeek: Boolean,
    onShowNonCurrentWeekChanged: (Boolean) -> Unit,
    showWeekends: Boolean,
    onShowWeekendsChanged: (Boolean) -> Unit,
    semesterStartDate: LocalDate?,
    semesterTotalWeeks: Int,
    firstDayOfWeekInt: Int,
    displayCurrentWeek: Int?,
    onSemesterStartDateClick: () -> Unit,
    onSemesterTotalWeeksClick: () -> Unit,
    onManualWeekClick: () -> Unit,
    onFirstDayOfWeekClick: () -> Unit,
    onQuickActionsClick: () -> Unit
) {
    SettingsSectionCard {
        Column(
            modifier = Modifier.padding(SETTING_PADDING),
            verticalArrangement = Arrangement.spacedBy(ITEM_SPACING)
        ) {
            Text(
                stringResource(Res.string.section_title_general_settings),
                style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title3,
                fontWeight = FontWeight.SemiBold
            )

            SettingItem(
                title = stringResource(Res.string.item_show_non_current_week),
                subtitle = stringResource(Res.string.desc_show_non_current_week)
            ) {
                top.yukonga.miuix.kmp.basic.Switch(checked = showNonCurrentWeek, onCheckedChange = onShowNonCurrentWeekChanged)
            }

            SettingItem(
                title = stringResource(Res.string.item_show_weekends),
                subtitle = stringResource(Res.string.desc_show_weekends)
            ) {
                top.yukonga.miuix.kmp.basic.Switch(checked = showWeekends, onCheckedChange = onShowWeekendsChanged)
            }

            SettingItem(
                title = stringResource(Res.string.item_set_start_date),
                subtitle = stringResource(Res.string.desc_set_start_date),
                onClick = onSemesterStartDateClick
            ) {
                val formattedDate = semesterStartDate?.let {
                    stringResource(
                        Res.string.date_format_year_month_day,
                        it.year.toString(),
                        it.month.number.toString(),
                        it.day.toString()
                    )
                } ?: stringResource(Res.string.status_not_set)

                Text(
                    text = formattedDate,
                    style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2
                )
            }

            SettingItem(
                title = stringResource(Res.string.item_total_weeks),
                subtitle = stringResource(Res.string.desc_total_weeks),
                onClick = onSemesterTotalWeeksClick
            ) {
                Text(
                    text = stringResource(Res.string.status_total_weeks_format, semesterTotalWeeks),
                    style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2
                )
            }

            SettingItem(
                title = stringResource(Res.string.item_current_week),
                subtitle = stringResource(Res.string.desc_current_week_manual),
                onClick = onManualWeekClick
            ) {
                val weekStatusText = when {
                    semesterStartDate == null -> stringResource(Res.string.status_set_start_date_first)
                    displayCurrentWeek == null -> stringResource(Res.string.title_vacation)
                    else -> stringResource(Res.string.status_current_week_format, displayCurrentWeek)
                }
                Text(
                    text = weekStatusText,
                    style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2
                )
            }

            SettingItem(
                title = stringResource(Res.string.item_first_day_of_week),
                subtitle = stringResource(Res.string.desc_first_day_of_week),
                onClick = onFirstDayOfWeekClick
            ) {
                val dayText = when (firstDayOfWeekInt) {
                    DayOfWeek.MONDAY.isoDayNumber -> stringResource(Res.string.day_of_week_monday)
                    DayOfWeek.SUNDAY.isoDayNumber -> stringResource(Res.string.day_of_week_sunday)
                    else -> stringResource(Res.string.day_of_week_monday)
                }
                Text(
                    text = dayText,
                    style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2
                )
            }

            SettingItem(
                title = stringResource(Res.string.item_quick_actions),
                subtitle = stringResource(Res.string.desc_quick_actions),
                onClick = onQuickActionsClick
            )
        }
    }
}

/**
 * 高级功能卡片
 */
@Composable
private fun AdvancedSettingsSection(onNavigate: (Destination) -> Unit) {
    SettingsSectionCard {
        Column(
            modifier = Modifier.padding(SETTING_PADDING),
            verticalArrangement = Arrangement.spacedBy(ITEM_SPACING)
        ) {
            Text(
                stringResource(Res.string.section_title_advanced_features),
                style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title3,
                fontWeight = FontWeight.SemiBold
            )
            SettingItem(
                title = stringResource(Res.string.item_course_conversion),
                subtitle = stringResource(Res.string.desc_course_conversion),
                onClick = { onNavigate(Destination.CourseTableConversion) }
            )
            SettingItem(
                title = stringResource(Res.string.title_course_notification_settings),
                subtitle = stringResource(Res.string.desc_notification_settings),
                onClick = { onNavigate(Destination.NotificationSettings) }
            )
            SettingItem(
                title = stringResource(Res.string.title_manage_course_tables),
                subtitle = stringResource(Res.string.desc_manage_course_tables),
                onClick = { onNavigate(Destination.ManageCourseTables) }
            )
            SettingItem(
                title = stringResource(Res.string.item_course_management),
                subtitle = stringResource(Res.string.desc_course_management),
                onClick = { onNavigate(Destination.CourseManagementList) }
            )
            SettingItem(
                title = stringResource(Res.string.item_time_slot_customization),
                subtitle = stringResource(Res.string.desc_time_slot_customization),
                onClick = { onNavigate(Destination.TimeSlotSettings) }
            )
            SettingItem(
                title = stringResource(Res.string.item_personalization),
                subtitle = stringResource(Res.string.desc_personalization),
                onClick = { onNavigate(Destination.StyleSettings) }
            )
            SettingItem(
                title = stringResource(Res.string.item_more_options),
                subtitle = stringResource(Res.string.desc_more_options),
                onClick = { onNavigate(Destination.MoreOptions) },
                icon = vectorResource(Res.drawable.more_horiz_24px)
            )
        }
    }
}

/**
 * 封装单个设置项的可组合函数，提高代码复用性
 */
@Composable
private fun SettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector = vectorResource(Res.drawable.chevron_right_24px),
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit = {
        top.yukonga.miuix.kmp.basic.Icon(
            vectorResource(Res.drawable.chevron_right_24px),
            contentDescription = null,
            tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantActions
        )
    }
) {
    top.yukonga.miuix.kmp.basic.BasicComponent(
        modifier = Modifier.fillMaxWidth(),
        title = title,
        summary = subtitle,
        onClick = onClick,
        insideMargin = PaddingValues(vertical = 10.dp, horizontal = 4.dp),
        endActions = { trailingContent() },
    )
}

@Composable
private fun SettingsSectionCard(content: @Composable () -> Unit) {
    top.yukonga.miuix.kmp.basic.Card(Modifier.fillMaxWidth()) { content() }
}

/**
 * 手动周数选择器对话框
 */
@Composable
fun ManualWeekPickerDialog(
    totalWeeks: Int,
    currentWeek: Int?,
    onDismiss: () -> Unit,
    onConfirm: (Int?) -> Unit
) {
    val optionOnVacationText = stringResource(Res.string.title_vacation)
    val safeTotalWeeks = totalWeeks.coerceAtLeast(1)
    val weekLabels = (0..safeTotalWeeks).map { value ->
        if (value == 0) optionOnVacationText
        else stringResource(Res.string.status_current_week_format, value)
    }
    var selectedValue by remember(safeTotalWeeks, currentWeek) {
        mutableIntStateOf((currentWeek ?: 0).coerceIn(0, safeTotalWeeks))
    }

    WindowDialog(
        show = true,
        title = stringResource(Res.string.dialog_title_manual_set_week),
        onDismissRequest = onDismiss,
        insideMargin = androidx.compose.ui.unit.DpSize(16.dp, 16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NumberPicker(
                value = selectedValue,
                onValueChange = { selectedValue = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                range = 0..safeTotalWeeks,
                label = { value -> weekLabels[value.coerceIn(0, weekLabels.lastIndex)] },
                visibleItemCount = 3,
            )
            MiuixDialogActions(
                onDismiss = onDismiss,
                onConfirm = { onConfirm(selectedValue.takeUnless { it == 0 }) },
            )
        }
    }
}

/**
 * 每周起始日选择器对话框
 */
@Composable
fun DayOfWeekPickerDialog(
    initialDayOfWeekInt: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val dayOfWeekMondayText = stringResource(Res.string.day_of_week_monday)
    val dayOfWeekSundayText = stringResource(Res.string.day_of_week_sunday)
    val dayLabels = listOf(dayOfWeekMondayText, dayOfWeekSundayText)
    val initialIndex = if (initialDayOfWeekInt == DayOfWeek.SUNDAY.isoDayNumber) 1 else 0
    var selectedIndex by remember(initialDayOfWeekInt) { mutableIntStateOf(initialIndex) }

    WindowDialog(
        show = true,
        title = stringResource(Res.string.dialog_title_set_first_day_of_week),
        onDismissRequest = onDismiss,
        insideMargin = androidx.compose.ui.unit.DpSize(16.dp, 16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NumberPicker(
                value = selectedIndex,
                onValueChange = { selectedIndex = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                range = 0..1,
                label = { value -> dayLabels[value.coerceIn(0, dayLabels.lastIndex)] },
                visibleItemCount = 3,
            )
            MiuixDialogActions(
                onDismiss = onDismiss,
                onConfirm = {
                    onConfirm(if (selectedIndex == 1) DayOfWeek.SUNDAY.isoDayNumber else DayOfWeek.MONDAY.isoDayNumber)
                },
            )
        }
    }
}

/**
 * 数字选择器对话框
 */
@Composable
private fun NumberPickerDialog(
    title: String,
    range: IntRange,
    initialValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var dialogSelectedValue by remember { mutableIntStateOf(initialValue.coerceIn(range)) }

    WindowDialog(
        show = true,
        title = title,
        onDismissRequest = onDismiss,
        insideMargin = androidx.compose.ui.unit.DpSize(16.dp, 16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NumberPicker(
                value = dialogSelectedValue,
                onValueChange = { dialogSelectedValue = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                range = range,
                label = Int::toString,
                visibleItemCount = 3,
            )
            MiuixDialogActions(
                onDismiss = onDismiss,
                onConfirm = { onConfirm(dialogSelectedValue) },
            )
        }
    }
}

@Composable
private fun MiuixDialogActions(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), onDismiss, Modifier.weight(1f))
        top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_confirm), onConfirm, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
    }
}
