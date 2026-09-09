package com.xingheyuzhuan.shiguangschedule.ui.settings.quickactions.delete

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xingheyuzhuan.shiguangschedule.data.db.main.CourseWithWeeks
import com.xingheyuzhuan.shiguangschedule.ui.components.NativeNumberPicker
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.todayIn
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_confirm
import shiguangschedule.shared.generated.resources.action_deselect_all
import shiguangschedule.shared.generated.resources.action_select_all
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.calendar_today_24px
import shiguangschedule.shared.generated.resources.close_24px
import shiguangschedule.shared.generated.resources.confirm_delete
import shiguangschedule.shared.generated.resources.course_time_day_section_details_tweak
import shiguangschedule.shared.generated.resources.course_time_day_time_details_tweak
import shiguangschedule.shared.generated.resources.delete_24px
import shiguangschedule.shared.generated.resources.dialog_delete_confirm_msg
import shiguangschedule.shared.generated.resources.filter_list_24px
import shiguangschedule.shared.generated.resources.hint_affected_count
import shiguangschedule.shared.generated.resources.hint_no_selection
import shiguangschedule.shared.generated.resources.item_quick_delete
import shiguangschedule.shared.generated.resources.label_day_of_week
import shiguangschedule.shared.generated.resources.label_dimension_dates
import shiguangschedule.shared.generated.resources.label_dimension_weeks_days
import shiguangschedule.shared.generated.resources.label_none
import shiguangschedule.shared.generated.resources.label_weeks_format
import shiguangschedule.shared.generated.resources.quick_delete_dialog_select_date_title
import shiguangschedule.shared.generated.resources.quick_delete_filter_date_range_hint
import shiguangschedule.shared.generated.resources.quick_delete_filter_weeks_days_hint
import shiguangschedule.shared.generated.resources.quick_delete_label_days_prefix
import shiguangschedule.shared.generated.resources.title_current_week
import shiguangschedule.shared.generated.resources.title_select_weeks
import shiguangschedule.shared.generated.resources.week_days_full_names
import kotlin.time.Instant
import kotlin.time.Clock

/**
 * 将 UiTextRes 转化为 Composable 的字符串
 */
@Composable
fun UiTextRes.asString(): String {
    return stringResource(resource, *args.toTypedArray())
}

/**
 * 快速删除界面：支持按“周次+星期”或“日期范围”筛选并批量清理课程。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickDeleteScreen(
    onBack: () -> Unit,
    viewModel: QuickDeleteViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX
    val miuixScrollBehavior = if (useMiuix) top.yukonga.miuix.kmp.basic.MiuixScrollBehavior() else null
    val weekDays = stringArrayResource(Res.array.week_days_full_names)
    val snackbarHostState = remember { SnackbarHostState() }

    // 控制侧边栏和日期选择器的显示状态
    val sheetState = rememberModalBottomSheetState()
    var showFilterSheet by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    // 控制二次确认弹窗的显示
    var showConfirmDialog by remember { mutableStateOf(false) }

    // 监听 ViewModel 发送的成功或错误消息，并使用 Snackbar 展示
    val successText = uiState.successMessage?.asString()
    val errorText = uiState.errorMessage?.asString()

    LaunchedEffect(successText, errorText) {
        successText?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetMessages()
        }
        errorText?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetMessages()
        }
    }

    Scaffold(
        modifier = if (useMiuix) Modifier.nestedScroll(miuixScrollBehavior!!.nestedScrollConnection) else Modifier,
        containerColor = if (useMiuix) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (useMiuix) top.yukonga.miuix.kmp.basic.TopAppBar(
                title = stringResource(Res.string.item_quick_delete),
                largeTitle = stringResource(Res.string.item_quick_delete),
                color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surface,
                scrollBehavior = miuixScrollBehavior,
                defaultWindowInsetsPadding = true,
                navigationIcon = {
                    top.yukonga.miuix.kmp.basic.IconButton(onBack) {
                        top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.arrow_back_24px), stringResource(Res.string.a11y_back), tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurface)
                    }
                }
            ) else TopAppBar(
                title = { Text(stringResource(Res.string.item_quick_delete)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(vectorResource(Res.drawable.arrow_back_24px), stringResource(Res.string.a11y_back))
                    }
                }
            )
        },
        bottomBar = {
            // 仅当有选中的课程受到影响时显示删除按钮
            if (uiState.affectedCourses.isNotEmpty()) {
                if (useMiuix) top.yukonga.miuix.kmp.basic.Card(Modifier.fillMaxWidth()) {
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.confirm_delete), { showConfirmDialog = true }, Modifier.fillMaxWidth(), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.error))
                } else Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
                    Button(
                        onClick = { showConfirmDialog = true }, // 点击后弹出确认弹窗
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(vectorResource(Res.drawable.delete_24px), null)
                        Spacer(Modifier.size(8.dp))
                        Text(stringResource(Res.string.confirm_delete))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 维度一：周次和星期筛选卡片
            item {
                Spacer(Modifier.height(8.dp))
                if (useMiuix) {
                    top.yukonga.miuix.kmp.basic.SmallTitle(stringResource(Res.string.label_dimension_weeks_days))
                    top.yukonga.miuix.kmp.basic.Card(
                        modifier = Modifier.fillMaxWidth().clickable { showFilterSheet = true },
                        colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer)
                    ) {
                        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.filter_list_24px), null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                if (uiState.selectedWeeks.isEmpty() || uiState.selectedDays.isEmpty()) {
                                    top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.quick_delete_filter_weeks_days_hint), color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
                                } else {
                                    val weeksContent = uiState.selectedWeeks.sorted().joinToString(", ")
                                    top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.label_weeks_format, weeksContent), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body1)
                                    val daysContent = uiState.selectedDays.sorted().joinToString("、") { weekDays[it - 1] }
                                    top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.quick_delete_label_days_prefix, daysContent), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote1, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
                                }
                            }
                            if (uiState.selectedWeeks.isNotEmpty() || uiState.selectedDays.isNotEmpty()) {
                                top.yukonga.miuix.kmp.basic.IconButton({ viewModel.clearWeeksAndDays() }) { top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.close_24px), null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantActions) }
                            }
                        }
                    }
                } else {
                    Text(text = stringResource(Res.string.label_dimension_weeks_days), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    OutlinedCard(onClick = { showFilterSheet = true }, modifier = Modifier.padding(vertical = 8.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(vectorResource(Res.drawable.filter_list_24px), null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                if (uiState.selectedWeeks.isEmpty() || uiState.selectedDays.isEmpty()) Text(stringResource(Res.string.quick_delete_filter_weeks_days_hint), color = MaterialTheme.colorScheme.outline)
                                else {
                                    val weeksContent = uiState.selectedWeeks.sorted().joinToString(", ")
                                    Text(stringResource(Res.string.label_weeks_format, weeksContent), style = MaterialTheme.typography.bodyMedium)
                                    val daysContent = uiState.selectedDays.sorted().joinToString("、") { weekDays[it - 1] }
                                    Text(stringResource(Res.string.quick_delete_label_days_prefix, daysContent), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                            if (uiState.selectedWeeks.isNotEmpty() || uiState.selectedDays.isNotEmpty()) IconButton(onClick = { viewModel.clearWeeksAndDays() }, modifier = Modifier.size(24.dp)) { Icon(vectorResource(Res.drawable.close_24px), null, modifier = Modifier.size(16.dp)) }
                        }
                    }
                }
            }

            // 维度二：具体日期范围筛选卡片
            item {
                if (useMiuix) top.yukonga.miuix.kmp.basic.SmallTitle(stringResource(Res.string.label_dimension_dates)) else Text(text = stringResource(Res.string.label_dimension_dates), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                val dateCardContent: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit = {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (useMiuix) top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.calendar_today_24px), null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary) else Icon(vectorResource(Res.drawable.calendar_today_24px), null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        val dateText = if (uiState.startDate != null && uiState.endDate != null) {
                            "${uiState.startDate} ~ ${uiState.endDate}"
                        } else {
                            stringResource(Res.string.quick_delete_filter_date_range_hint)
                        }
                        if (useMiuix) top.yukonga.miuix.kmp.basic.Text(dateText, Modifier.weight(1f), color = if (uiState.startDate != null) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurface else top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary) else Text(dateText, Modifier.weight(1f), color = if (uiState.startDate != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline)
                        if (uiState.startDate != null) {
                            if (useMiuix) top.yukonga.miuix.kmp.basic.IconButton({ viewModel.clearDateRange() }) { top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.close_24px), null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantActions) }
                            else IconButton(onClick = { viewModel.clearDateRange() }, modifier = Modifier.size(24.dp)) { Icon(vectorResource(Res.drawable.close_24px), null, modifier = Modifier.size(16.dp)) }
                        }
                    }
                }
                if (useMiuix) top.yukonga.miuix.kmp.basic.Card(Modifier.fillMaxWidth().clickable { showDateRangePicker = true }, colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer), content = dateCardContent)
                else OutlinedCard(onClick = { showDateRangePicker = true }, modifier = Modifier.padding(vertical = 8.dp), content = dateCardContent)
            }

            // 预览状态提示：显示受影响的记录条数
            item {
                val count = uiState.affectedCourses.size
                if (useMiuix) top.yukonga.miuix.kmp.basic.Text(
                    text = if (count > 0) stringResource(Res.string.hint_affected_count, count) else stringResource(Res.string.hint_no_selection),
                    color = if (count > 0) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.error else top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                ) else Text(
                    text = if (count > 0) stringResource(Res.string.hint_affected_count, count) else stringResource(Res.string.hint_no_selection),
                    color = if (count > 0) MaterialTheme.colorScheme.error else Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 平铺显示所有待删除的课程实例预览
            items(uiState.affectedCourses) { previewItem ->
                DeletePreviewCard(previewItem.courseWithWeeks, previewItem.targetWeek)
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }

    // 二次确认对话框
    if (showConfirmDialog) {
        if (useMiuix) top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = stringResource(Res.string.confirm_delete), summary = stringResource(Res.string.dialog_delete_confirm_msg), onDismissRequest = { showConfirmDialog = false }) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), { showConfirmDialog = false }, Modifier.weight(1f))
                top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_confirm), { showConfirmDialog = false; viewModel.executeDelete() }, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.error))
            }
        } else AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(stringResource(Res.string.confirm_delete)) },
            text = { Text(stringResource(Res.string.dialog_delete_confirm_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.executeDelete() // 真正执行删除
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(Res.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }

    // 底部筛选面板：选择周次（1-20）和星期（1-7）
    if (showFilterSheet) {
        FilterBottomSheet(
            sheetState = sheetState,
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { showFilterSheet = false }
        )
    }

    // 原生风格的日期范围选择对话框
    if (showDateRangePicker) {
        DateRangePickerModal(
            onDismiss = { showDateRangePicker = false },
            onConfirm = { start, end ->
                viewModel.setDateRange(start, end)
                showDateRangePicker = false
            }
        )
    }
}

/**
 * 底部筛选抽屉，包含周次多选和星期多选。
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    uiState: QuickDeleteUiState,
    viewModel: QuickDeleteViewModel,
    onDismiss: () -> Unit
) {
    val weekDays = stringArrayResource(Res.array.week_days_full_names)
    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = stringResource(Res.string.title_select_weeks), onDismissRequest = onDismiss, insideMargin = androidx.compose.ui.unit.DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.title_select_weeks), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title3)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..20).forEach { week ->
                        top.yukonga.miuix.kmp.basic.Card(Modifier.size(42.dp).clickable { viewModel.toggleWeek(week) }, colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = if (week in uiState.selectedWeeks) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary else top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer)) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { top.yukonga.miuix.kmp.basic.Text(week.toString(), color = if (week in uiState.selectedWeeks) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onPrimary else top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurface) }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.label_day_of_week), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title3)
                    top.yukonga.miuix.kmp.basic.TextButton(if (uiState.selectedDays.size == 7) stringResource(Res.string.action_deselect_all) else stringResource(Res.string.action_select_all), { if (uiState.selectedDays.size == 7) viewModel.clearAllDays() else viewModel.selectAllDays() })
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..7).forEach { day ->
                        top.yukonga.miuix.kmp.basic.Card(Modifier.clickable { viewModel.toggleDay(day) }, colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = if (day in uiState.selectedDays) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary else top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer)) {
                            top.yukonga.miuix.kmp.basic.Text(weekDays[day - 1], Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = if (day in uiState.selectedDays) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onPrimary else top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurface)
                        }
                    }
                }
                top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_confirm), onDismiss, Modifier.fillMaxWidth(), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
            }
        }
        return
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(stringResource(Res.string.title_select_weeks), style = MaterialTheme.typography.titleMedium)

            // 周次流式布局列表
            FlowRow(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..20).forEach { week ->
                    FilterChip(
                        selected = uiState.selectedWeeks.contains(week),
                        onClick = { viewModel.toggleWeek(week) },
                        label = {
                            Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
                                Text(week.toString())
                            }
                        }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            // 星期选择栏，带全选/取消全选
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(Res.string.label_day_of_week), style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = {
                    if (uiState.selectedDays.size == 7) viewModel.clearAllDays() else viewModel.selectAllDays()
                }) {
                    Text(if (uiState.selectedDays.size == 7) stringResource(Res.string.action_deselect_all) else stringResource(Res.string.action_select_all))
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..7).forEach { day ->
                    FilterChip(
                        selected = uiState.selectedDays.contains(day),
                        onClick = { viewModel.toggleDay(day) },
                        label = {
                            Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.Center) {
                                Text(weekDays[day - 1])
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(Res.string.action_confirm))
            }
        }
    }
}

/**
 * Material 3 风格的日期范围选择器。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, LocalDate) -> Unit
) {
    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val years = remember { (today.year - 5..today.year + 10).toList() }
        val months = remember { (1..12).toList() }
        val days = remember { (1..31).toList() }
        var selectingEnd by remember { mutableStateOf(false) }
        var startDate by remember { mutableStateOf<LocalDate?>(null) }
        var startYear by remember { mutableStateOf(today.year) }
        var startMonth by remember { mutableStateOf(today.month.number) }
        var startDay by remember { mutableStateOf(today.day) }
        var endYear by remember { mutableStateOf(today.year) }
        var endMonth by remember { mutableStateOf(today.month.number) }
        var endDay by remember { mutableStateOf(today.day) }
        val year = if (selectingEnd) endYear else startYear
        val month = if (selectingEnd) endMonth else startMonth
        val day = if (selectingEnd) endDay else startDay
        val chosenDate = runCatching { LocalDate(year, month, day) }.getOrNull()

        top.yukonga.miuix.kmp.window.WindowDialog(
            show = true,
            title = stringResource(Res.string.quick_delete_dialog_select_date_title),
            onDismissRequest = onDismiss,
            insideMargin = androidx.compose.ui.unit.DpSize(16.dp, 16.dp)
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (startDate != null) top.yukonga.miuix.kmp.basic.Text(startDate.toString(), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote1, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NativeNumberPicker(years, year, { value -> if (selectingEnd) endYear = value else startYear = value }, Modifier.weight(1f).height(150.dp))
                    NativeNumberPicker(months, month, { value -> if (selectingEnd) endMonth = value else startMonth = value }, Modifier.weight(1f).height(150.dp))
                    NativeNumberPicker(days, day, { value -> if (selectingEnd) endDay = value else startDay = value }, Modifier.weight(1f).height(150.dp))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), onDismiss, Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(
                        stringResource(Res.string.action_confirm),
                        {
                            if (!selectingEnd) {
                                startDate = chosenDate
                                endYear = year
                                endMonth = month
                                endDay = day
                                selectingEnd = true
                            } else if (chosenDate != null && startDate != null) {
                                onConfirm(startDate!!, chosenDate)
                            }
                        },
                        Modifier.weight(1f),
                        enabled = chosenDate != null && (!selectingEnd || startDate == null || chosenDate >= startDate!!),
                        colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }
        }
        return
    }
    val state = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val start = state.selectedStartDateMillis?.toLocalDate()
                    val end = state.selectedEndDateMillis?.toLocalDate()
                    if (start != null && end != null) {
                        onConfirm(start, end)
                    }
                },
                enabled = state.selectedEndDateMillis != null
            ) { Text(stringResource(Res.string.action_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_cancel)) }
        }
    ) {
        DateRangePicker(
            state = state,
            modifier = Modifier.weight(1f),
            title = {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(Res.string.quick_delete_dialog_select_date_title)
                )
            }
        )
    }
}

/**
 * 待删除课程的预览卡片，显示课程名称、具体周次和节次/时间信息。
 */
@Composable
fun DeletePreviewCard(
    courseWithWeeks: CourseWithWeeks,
    targetWeek: Int
) {
    val weekDays = stringArrayResource(Res.array.week_days_full_names)
    val course = courseWithWeeks.course
    val weekText = stringResource(Res.string.title_current_week, targetWeek.toString())
    val dayString = weekDays[course.day - 1]
    val detailsText = if (course.isCustomTime) {
        stringResource(Res.string.course_time_day_time_details_tweak, dayString, course.customStartTime ?: stringResource(Res.string.label_none), course.customEndTime ?: stringResource(Res.string.label_none))
    } else {
        stringResource(Res.string.course_time_day_section_details_tweak, dayString, (course.startSection ?: 0).toString(), (course.endSection ?: 0).toString())
    }

    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.basic.Card(Modifier.fillMaxWidth(), colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.errorContainer.copy(alpha = 0.18f))) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    top.yukonga.miuix.kmp.basic.Text(course.name, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body1, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    top.yukonga.miuix.kmp.basic.Text("$weekText · $detailsText", style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote1, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
                }
                top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.delete_24px), null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.error)
            }
        }
        return
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$weekText · $detailsText",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(vectorResource(Res.drawable.delete_24px), null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
        }
    }
}

/**
 * 将 DatePicker 的毫秒值转换为 kotlinx.datetime.LocalDate。
 */
private fun Long.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
