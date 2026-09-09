package com.xingheyuzhuan.shiguangschedule.ui.settings.quickactions.tweaks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.data.db.main.CourseWithWeeks
import com.xingheyuzhuan.shiguangschedule.data.repository.CourseTableRepository.TweakMode
import com.xingheyuzhuan.shiguangschedule.ui.components.CourseTablePickerDialog
import com.xingheyuzhuan.shiguangschedule.ui.components.DatePickerModal
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_arrow
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.a11y_save_tweak
import shiguangschedule.shared.generated.resources.action_select_table
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.arrow_downward_24px
import shiguangschedule.shared.generated.resources.arrow_drop_down_24px
import shiguangschedule.shared.generated.resources.arrow_forward_24px
import shiguangschedule.shared.generated.resources.course_time_day_section_details_tweak
import shiguangschedule.shared.generated.resources.course_time_day_time_details_tweak
import shiguangschedule.shared.generated.resources.date_format_month_day
import shiguangschedule.shared.generated.resources.dialog_title_select_tweak_from_date
import shiguangschedule.shared.generated.resources.dialog_title_select_tweak_to_date
import shiguangschedule.shared.generated.resources.check_24px
import shiguangschedule.shared.generated.resources.double_arrow_24px
import shiguangschedule.shared.generated.resources.label_select_tweak_table
import shiguangschedule.shared.generated.resources.label_tweak_from_date
import shiguangschedule.shared.generated.resources.label_tweak_to_date
import shiguangschedule.shared.generated.resources.sync_alt_24px
import shiguangschedule.shared.generated.resources.text_error
import shiguangschedule.shared.generated.resources.text_no_course
import shiguangschedule.shared.generated.resources.text_tweak_hint
import shiguangschedule.shared.generated.resources.title_tweak_from_course
import shiguangschedule.shared.generated.resources.title_tweak_schedule
import shiguangschedule.shared.generated.resources.title_tweak_to_course
import shiguangschedule.shared.generated.resources.title_select_tweak_mode
import shiguangschedule.shared.generated.resources.tweak_mode_exchange
import shiguangschedule.shared.generated.resources.tweak_mode_merge
import shiguangschedule.shared.generated.resources.tweak_mode_overwrite
import shiguangschedule.shared.generated.resources.week_days_full_names
import kotlin.time.Instant

@Composable
fun UiTextRes.asString(): String {
    return if (args.isEmpty()) {
        stringResource(resource)
    } else {
        stringResource(resource, *args.toTypedArray())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TweakScheduleScreen(
    onBack: () -> Unit,
    viewModel: TweakScheduleViewModel = koinViewModel()
) {
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showCourseTablePicker by remember { mutableStateOf(false) }
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }

    val titleTweakSchedule = stringResource(Res.string.title_tweak_schedule)
    val a11yBack = stringResource(Res.string.a11y_back)
    val a11ySaveTweak = stringResource(Res.string.a11y_save_tweak)
    val labelSelectTweakTable = stringResource(Res.string.label_select_tweak_table)
    val actionSelectTable = stringResource(Res.string.action_select_table)
    val labelTweakFromDate = stringResource(Res.string.label_tweak_from_date)
    val labelTweakToDate = stringResource(Res.string.label_tweak_to_date)
    val textTweakHint = stringResource(Res.string.text_tweak_hint)
    val titleTweakFromCourse = stringResource(Res.string.title_tweak_from_course)
    val titleTweakToCourse = stringResource(Res.string.title_tweak_to_course)
    val dialogTitleSelectTweakFromDate = stringResource(Res.string.dialog_title_select_tweak_from_date)
    val dialogTitleSelectTweakToDate = stringResource(Res.string.dialog_title_select_tweak_to_date)
    val a11yArrow = stringResource(Res.string.a11y_arrow)

    val errorMessageText = uiState.errorMessage?.asString()
    val successMessageText = uiState.successMessage?.asString()

    LaunchedEffect(errorMessageText) {
        errorMessageText?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetMessages()
        }
    }

    LaunchedEffect(successMessageText) {
        successMessageText?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetMessages()
        }
    }

    Scaffold(
        containerColor = if (useMiuix) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (useMiuix) {
                top.yukonga.miuix.kmp.basic.TopAppBar(
                    title = titleTweakSchedule,
                    largeTitle = titleTweakSchedule,
                    color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surface,
                    defaultWindowInsetsPadding = true,
                    navigationIcon = {
                        top.yukonga.miuix.kmp.basic.IconButton(onClick = onBack) {
                            top.yukonga.miuix.kmp.basic.Icon(
                                imageVector = vectorResource(Res.drawable.arrow_back_24px),
                                contentDescription = a11yBack,
                                tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        top.yukonga.miuix.kmp.basic.IconButton(onClick = { viewModel.moveCourses() }) {
                            top.yukonga.miuix.kmp.basic.Icon(
                                imageVector = vectorResource(Res.drawable.check_24px),
                                contentDescription = a11ySaveTweak,
                                tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(titleTweakSchedule) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(vectorResource(Res.drawable.arrow_back_24px), a11yBack)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.moveCourses() }) {
                            Icon(vectorResource(Res.drawable.check_24px), a11ySaveTweak)
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CourseTableSelectionRow(
                    label = labelSelectTweakTable,
                    value = uiState.selectedCourseTable?.name ?: actionSelectTable,
                    contentDescription = actionSelectTable,
                    useMiuix = useMiuix,
                    onClick = { showCourseTablePicker = true }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    DateButton(label = labelTweakFromDate, date = uiState.fromDate, useMiuix = useMiuix, onClick = { showFromDatePicker = true })
                    DateButton(label = labelTweakToDate, date = uiState.toDate, useMiuix = useMiuix, onClick = { showToDatePicker = true })
                }
            }

            item {
                if (useMiuix) top.yukonga.miuix.kmp.basic.Text(
                    text = textTweakHint,
                    style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote1,
                    color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary
                ) else Text(textTweakHint, style = MaterialTheme.typography.bodySmall)
            }

            item {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val isLandscape = maxWidth > maxHeight
                    val (modeIcon, _) = getTweakModeDisplayInfo(uiState.tweakMode)

                    if (isLandscape) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CourseDisplayCard(modifier = Modifier.weight(1f), title = titleTweakFromCourse, courses = uiState.fromCourses, useMiuix = useMiuix)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                TweakModeSelector(currentMode = uiState.tweakMode, useMiuix = useMiuix, onModeSelected = { viewModel.onTweakModeChanged(it) })
                                TweakIcon(modeIcon, a11yArrow, Modifier.size(32.dp), useMiuix)
                            }

                            CourseDisplayCard(modifier = Modifier.weight(1f), title = titleTweakToCourse, courses = uiState.toCourses, useMiuix = useMiuix)
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CourseDisplayCard(title = titleTweakFromCourse, courses = uiState.fromCourses, useMiuix = useMiuix)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                val verticalIcon = when (uiState.tweakMode) {
                                    TweakMode.EXCHANGE -> vectorResource(Res.drawable.sync_alt_24px)
                                    TweakMode.OVERWRITE -> vectorResource(Res.drawable.double_arrow_24px)
                                    TweakMode.MERGE -> vectorResource(Res.drawable.arrow_downward_24px)
                                }

                                val rotationAngle = if (uiState.tweakMode != TweakMode.MERGE) 90f else 0f

                                TweakIcon(verticalIcon, a11yArrow, Modifier.size(32.dp).rotate(rotationAngle), useMiuix)

                                TweakModeSelector(
                                    currentMode = uiState.tweakMode,
                                    useMiuix = useMiuix,
                                    onModeSelected = { viewModel.onTweakModeChanged(it) }
                                )
                            }

                            CourseDisplayCard(title = titleTweakToCourse, courses = uiState.toCourses, useMiuix = useMiuix)
                        }
                    }
                }
            }
        }
    }

    if (showCourseTablePicker) {
        CourseTablePickerDialog(
            title = labelSelectTweakTable,
            onDismissRequest = { showCourseTablePicker = false },
            onTableSelected = { viewModel.onCourseTableSelected(it); showCourseTablePicker = false }
        )
    }

    if (showFromDatePicker) {
        DatePickerModal(onDateSelected = { it?.let { viewModel.onFromDateSelected(it.toLocalDate()) } }, onDismiss = { showFromDatePicker = false }, title = dialogTitleSelectTweakFromDate)
    }

    if (showToDatePicker) {
        DatePickerModal(onDateSelected = { it?.let { viewModel.onToDateSelected(it.toLocalDate()) } }, onDismiss = { showToDatePicker = false }, title = dialogTitleSelectTweakToDate)
    }
}

@Composable
private fun TweakModeSelector(currentMode: TweakMode, useMiuix: Boolean, onModeSelected: (TweakMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val (_, label) = getTweakModeDisplayInfo(currentMode)
    val dialogTitle = stringResource(Res.string.title_select_tweak_mode)

    if (useMiuix) {
        top.yukonga.miuix.kmp.basic.TextButton(text = label, onClick = { expanded = true })
        if (expanded) top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = dialogTitle, onDismissRequest = { expanded = false }, insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TweakMode.entries.forEach { mode ->
                    val (mIcon, mLabel) = getTweakModeDisplayInfo(mode)
                    top.yukonga.miuix.kmp.basic.BasicComponent(title = mLabel, onClick = { onModeSelected(mode); expanded = false }, startAction = { top.yukonga.miuix.kmp.basic.Icon(mIcon, null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary) }, endActions = { if (mode == currentMode) top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.check_24px), null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary) })
                }
            }
        }
        return
    }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Icon(vectorResource(Res.drawable.arrow_drop_down_24px), contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TweakMode.entries.forEach { mode ->
                val (mIcon, mLabel) = getTweakModeDisplayInfo(mode)
                DropdownMenuItem(
                    leadingIcon = { Icon(mIcon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text(mLabel) },
                    onClick = { onModeSelected(mode); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun TweakIcon(imageVector: ImageVector, description: String, modifier: Modifier, useMiuix: Boolean) {
    if (useMiuix) top.yukonga.miuix.kmp.basic.Icon(imageVector, description, modifier, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary)
    else Icon(imageVector, description, modifier, tint = MaterialTheme.colorScheme.primary)
}

@Composable
private fun CourseTableSelectionRow(label: String, value: String, contentDescription: String, useMiuix: Boolean, onClick: () -> Unit) {
    if (useMiuix) {
        top.yukonga.miuix.kmp.basic.BasicComponent(
            modifier = Modifier.fillMaxWidth(), title = label, summary = value, onClick = onClick,
            endActions = { top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.arrow_forward_24px), contentDescription, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantActions) }
        )
    } else {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            TextButton(onClick) { Row(verticalAlignment = Alignment.CenterVertically) { Text(value); Icon(vectorResource(Res.drawable.arrow_forward_24px), contentDescription, Modifier.size(16.dp)) } }
        }
    }
}

@Composable
private fun getTweakModeDisplayInfo(mode: TweakMode): Pair<ImageVector, String> {
    return when (mode) {
        TweakMode.MERGE -> vectorResource(Res.drawable.arrow_forward_24px) to stringResource(Res.string.tweak_mode_merge)
        TweakMode.OVERWRITE -> vectorResource(Res.drawable.double_arrow_24px) to stringResource(Res.string.tweak_mode_overwrite)
        TweakMode.EXCHANGE -> vectorResource(Res.drawable.sync_alt_24px) to stringResource(Res.string.tweak_mode_exchange)
    }
}

@Composable
fun CourseDisplayCard(title: String, courses: List<CourseWithWeeks>, modifier: Modifier = Modifier, useMiuix: Boolean = false) {
    val textNoCourse = stringResource(Res.string.text_no_course)
    val sectionFormatRes = Res.string.course_time_day_section_details_tweak
    val customTimeFormatRes = Res.string.course_time_day_time_details_tweak

    val content: @Composable ColumnScope.() -> Unit = {
        Column(modifier = Modifier.padding(16.dp)) {
            if (useMiuix) top.yukonga.miuix.kmp.basic.Text(title, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title3) else Text(text = title, style = MaterialTheme.typography.titleLarge)
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 250.dp)) {
                if (courses.isEmpty()) {
                    item { if (useMiuix) top.yukonga.miuix.kmp.basic.Text(textNoCourse, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary, modifier = Modifier.padding(8.dp)) else Text(text = textNoCourse, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp)) }
                } else {
                    items(courses) { courseWithWeeks: CourseWithWeeks ->
                        val course = courseWithWeeks.course
                        val dayString = getLocalizedDayString(course.day)
                        val detailsText = if (course.isCustomTime) {
                            stringResource(customTimeFormatRes, dayString, course.customStartTime ?: "??:??", course.customEndTime ?: "??:??")
                        } else {
                            stringResource(sectionFormatRes, dayString, (course.startSection ?: 0).toString(), (course.endSection ?: 0).toString())
                        }
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            if (useMiuix) {
                                top.yukonga.miuix.kmp.basic.Text(course.name, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body1)
                                top.yukonga.miuix.kmp.basic.Text(detailsText, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote1, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
                            } else {
                                Text(text = course.name, style = MaterialTheme.typography.bodyLarge)
                                Text(text = detailsText, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
    if (useMiuix) top.yukonga.miuix.kmp.basic.Card(modifier = modifier, colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer), content = content) else Card(modifier = modifier, content = content)
}

@Composable
private fun DateButton(label: String, date: LocalDate, useMiuix: Boolean, onClick: () -> Unit) {
    val dateDisplay = stringResource(
        Res.string.date_format_month_day,
        date.month.number,
        date.day
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (useMiuix) {
            top.yukonga.miuix.kmp.basic.Text(label, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote1, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
            top.yukonga.miuix.kmp.basic.TextButton(dateDisplay, onClick)
        } else {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = onClick) { Text(text = dateDisplay, style = MaterialTheme.typography.titleMedium) }
        }
    }
}

private fun Long.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

@Composable
private fun getLocalizedDayString(day: Int): String {
    val weekDays = stringArrayResource(Res.array.week_days_full_names)
    return if (day in 1..7) weekDays[day - 1] else stringResource(Res.string.text_error)
}
