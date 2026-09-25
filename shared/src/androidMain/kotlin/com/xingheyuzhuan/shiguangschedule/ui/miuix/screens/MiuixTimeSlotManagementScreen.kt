package com.xingheyuzhuan.shiguangschedule.ui.miuix.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xingheyuzhuan.shiguangschedule.data.db.main.TimeSlot
import com.xingheyuzhuan.shiguangschedule.ui.components.ToastManager
import com.xingheyuzhuan.shiguangschedule.ui.components.LocalNavigationHostPadding
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.HyperLiquidTopBarButton
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.rememberSharedScrollBehavior
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.chrome.HyperGlassTopBar
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.utils.overScrollVertical
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xingheyuzhuan.shiguangschedule.ui.settings.time.TimeSlotViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_add_time_slot
import shiguangschedule.shared.generated.resources.a11y_delete_time_slot
import shiguangschedule.shared.generated.resources.a11y_save_all_settings
import shiguangschedule.shared.generated.resources.action_add
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_save_changes
import shiguangschedule.shared.generated.resources.add_24px
import shiguangschedule.shared.generated.resources.common_action_continue_editing
import shiguangschedule.shared.generated.resources.common_action_exit_without_save
import shiguangschedule.shared.generated.resources.common_dialog_msg_unsaved_changes
import shiguangschedule.shared.generated.resources.common_dialog_title_abandon_changes
import shiguangschedule.shared.generated.resources.delete_24px
import shiguangschedule.shared.generated.resources.dialog_title_add_time_slot
import shiguangschedule.shared.generated.resources.dialog_title_edit_time_slot
import shiguangschedule.shared.generated.resources.label_break_duration_minutes
import shiguangschedule.shared.generated.resources.label_class_duration_minutes
import shiguangschedule.shared.generated.resources.label_time_picker_end
import shiguangschedule.shared.generated.resources.label_time_picker_start
import shiguangschedule.shared.generated.resources.label_time_slot_alias
import shiguangschedule.shared.generated.resources.save_24px
import shiguangschedule.shared.generated.resources.text_no_time_slots_hint
import shiguangschedule.shared.generated.resources.time_slot_section_number
import shiguangschedule.shared.generated.resources.title_default_duration_settings
import shiguangschedule.shared.generated.resources.title_time_slot_management
import shiguangschedule.shared.generated.resources.toast_end_time_must_be_later
import shiguangschedule.shared.generated.resources.toast_settings_saved
import shiguangschedule.shared.generated.resources.toast_slot_added_unsaved
import shiguangschedule.shared.generated.resources.toast_slot_modified_unsaved
import shiguangschedule.shared.generated.resources.toast_slot_removed_unsaved
import shiguangschedule.shared.generated.resources.toast_time_conflict
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.NumberPicker
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.icon.extended.ChevronBackward
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.icon.MiuixIcons

@Composable
internal fun MiuixTimeSlotManagementScreen(
    onBack: () -> Unit,
    viewModel: TimeSlotViewModel = koinViewModel(),
) {
    val uiState by viewModel.timeSlotsUiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val slots = remember { mutableStateListOf<TimeSlot>() }
    var classDuration by remember { mutableIntStateOf(45) }
    var breakDuration by remember { mutableIntStateOf(10) }
    var editingSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState.isDataLoaded) {
            slots.clear()
            slots.addAll(uiState.timeSlots.sortedBy(TimeSlot::number))
            classDuration = uiState.defaultClassDuration
            breakDuration = uiState.defaultBreakDuration
        }
    }

    val hasUnsavedChanges = viewModel.hasUnsavedChanges(
        currentTimeSlots = slots.toList(),
        currentClassDuration = classDuration,
        currentBreakDuration = breakDuration,
    )
    val requestBack = {
        if (hasUnsavedChanges) showExitConfirmation = true else onBack()
    }
    BackHandler(
        enabled = hasUnsavedChanges && !showEditor && !showExitConfirmation,
        onBack = requestBack,
    )

    val savedToast = stringResource(Res.string.toast_settings_saved)
    val removedToast = stringResource(Res.string.toast_slot_removed_unsaved)
    val addedToast = stringResource(Res.string.toast_slot_added_unsaved)
    val modifiedToast = stringResource(Res.string.toast_slot_modified_unsaved)

    fun saveAll() {
        scope.launch {
            val normalized = slots.sortedBy { parseTime(it.startTime) }
                .mapIndexed { index, slot -> slot.copy(number = index + 1) }
            viewModel.onSaveAllSettings(normalized, classDuration, breakDuration) {
                ToastManager.show(savedToast)
            }
        }
    }

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
                title = stringResource(Res.string.title_time_slot_management),
                backdrop = backdrop,
                scrollBehavior = scrollBehavior,
                startAction = { a, s -> HyperLiquidTopBarButton(requestBack, backdrop, MiuixIcons.ChevronBackward, "返回", backdropAlpha = a, shadowAlpha = s) },
                endAction = { backdropAlpha, shadowAlpha ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HyperLiquidTopBarButton(
                    onClick = { editingSlot = null; showEditor = true },
                    backdrop = backdrop,
                    icon = vectorResource(Res.drawable.add_24px),
                    contentDescription = stringResource(Res.string.a11y_add_time_slot),
                    backdropAlpha = backdropAlpha,
                    shadowAlpha = shadowAlpha,
                )
                HyperLiquidTopBarButton(
                    onClick = ::saveAll,
                    backdrop = backdrop,
                    icon = vectorResource(Res.drawable.save_24px),
                    contentDescription = stringResource(Res.string.a11y_save_all_settings),
                    backdropAlpha = backdropAlpha,
                    shadowAlpha = shadowAlpha,
                )
            }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().background(background).layerBackdrop(backdrop)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().overScrollVertical().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                start = padding.calculateLeftPadding(direction) + 20.dp,
                top = padding.calculateTopPadding() + 8.dp,
                end = padding.calculateRightPadding(direction) + 20.dp,
                bottom = padding.calculateBottomPadding() + hostPadding.calculateBottomPadding() + 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                DurationSettings(
                    classDuration = classDuration,
                    onClassDurationChange = { classDuration = it },
                    breakDuration = breakDuration,
                    onBreakDurationChange = { breakDuration = it },
                )
            }
            if (slots.isEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.text_no_time_slots_hint),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            } else {
                items(slots, key = { "${it.number}-${it.startTime}" }) { slot ->
                    TimeSlotCard(
                        slot = slot,
                        onEdit = { editingSlot = slot; showEditor = true },
                        onDelete = {
                            slots.remove(slot)
                            val normalized = slots.sortedBy { parseTime(it.startTime) }
                                .mapIndexed { index, item -> item.copy(number = index + 1) }
                            slots.clear()
                            slots.addAll(normalized)
                            ToastManager.show(removedToast)
                        },
                    )
                }
            }
        }
        }
        if (showEditor) {
                TimeSlotEditorDialog(
                    slots = slots.toList(),
                    editingSlot = editingSlot,
                    classDuration = classDuration,
                    breakDuration = breakDuration,
                    onDismiss = { showEditor = false; editingSlot = null },
                    onConfirm = { updated ->
                        if (editingSlot == null) {
                            slots += updated
                            ToastManager.show(addedToast)
                        } else {
                            val index = slots.indexOfFirst { it.number == editingSlot?.number }
                            if (index >= 0) slots[index] = updated
                            ToastManager.show(modifiedToast)
                        }
                        val normalized = slots.sortedBy { parseTime(it.startTime) }
                            .mapIndexed { index, slot -> slot.copy(number = index + 1) }
                        slots.clear()
                        slots.addAll(normalized)
                        showEditor = false
                        editingSlot = null
                    },
                )
        }
        if (showExitConfirmation) {
                OverlayDialog(
                    show = true,
                    title = stringResource(Res.string.common_dialog_title_abandon_changes),
                    onDismissRequest = { showExitConfirmation = false },
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = stringResource(Res.string.common_dialog_msg_unsaved_changes),
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                        DialogActions(
                            cancelText = stringResource(Res.string.common_action_continue_editing),
                            confirmText = stringResource(Res.string.common_action_exit_without_save),
                            onCancel = { showExitConfirmation = false },
                            onConfirm = { showExitConfirmation = false; onBack() },
                        )
                    }
                }
        }
    }
}

@Composable
private fun DurationSettings(
    classDuration: Int,
    onClassDurationChange: (Int) -> Unit,
    breakDuration: Int,
    onBreakDurationChange: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SmallTitle(stringResource(Res.string.title_default_duration_settings))
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainer),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = classDuration.toString(),
                    onValueChange = { value -> value.toIntOrNull()?.takeIf { it > 0 }?.let(onClassDurationChange) },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.label_class_duration_minutes),
                    singleLine = true,
                )
                TextField(
                    value = breakDuration.toString(),
                    onValueChange = { value -> value.toIntOrNull()?.takeIf { it >= 0 }?.let(onBreakDurationChange) },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.label_break_duration_minutes),
                    singleLine = true,
                )
            }
        }
    }
}

@Composable
private fun TimeSlotCard(slot: TimeSlot, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(start = 20.dp, top = 16.dp, end = 8.dp, bottom = 16.dp),
        colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainer),
        onClick = onEdit,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = stringResource(Res.string.time_slot_section_number, slot.number.toString()),
                    style = MiuixTheme.textStyles.body1,
                )
                slot.alias?.takeIf(String::isNotBlank)?.let { alias ->
                    Text(
                        text = alias,
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = "${slot.startTime} - ${slot.endTime}",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = vectorResource(Res.drawable.delete_24px),
                    contentDescription = stringResource(Res.string.a11y_delete_time_slot),
                    tint = MiuixTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun TimeSlotEditorDialog(
    slots: List<TimeSlot>,
    editingSlot: TimeSlot?,
    classDuration: Int,
    breakDuration: Int,
    onDismiss: () -> Unit,
    onConfirm: (TimeSlot) -> Unit,
) {
    val number = editingSlot?.number ?: ((slots.maxOfOrNull(TimeSlot::number) ?: 0) + 1)
    val defaultStart = editingSlot?.startTime?.let(::parseTime)
        ?: slots.maxByOrNull(TimeSlot::number)?.endTime?.let(::parseTime)?.plusMinutes(breakDuration)
        ?: LocalTime(8, 0)
    val defaultEnd = editingSlot?.endTime?.let(::parseTime) ?: defaultStart.plusMinutes(classDuration)
    var startHour by remember(editingSlot) { mutableIntStateOf(defaultStart.hour) }
    var startMinute by remember(editingSlot) { mutableIntStateOf(defaultStart.minute) }
    var endHour by remember(editingSlot) { mutableIntStateOf(defaultEnd.hour) }
    var endMinute by remember(editingSlot) { mutableIntStateOf(defaultEnd.minute) }
    var alias by remember(editingSlot) { mutableStateOf(editingSlot?.alias.orEmpty()) }
    val start = LocalTime(startHour, startMinute)
    val end = LocalTime(endHour, endMinute)
    val previousEnd = slots.filter { it.number < number }.maxByOrNull(TimeSlot::number)?.endTime?.let(::parseTime)
    val nextStart = slots.filter { it.number > number }.minByOrNull(TimeSlot::number)?.startTime?.let(::parseTime)
    val endError = stringResource(Res.string.toast_end_time_must_be_later)
    val conflictError = stringResource(Res.string.toast_time_conflict)
    val isEditing = editingSlot != null

    OverlayDialog(
        show = true,
        title = stringResource(if (isEditing) Res.string.dialog_title_edit_time_slot else Res.string.dialog_title_add_time_slot),
        onDismissRequest = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            TextField(
                value = alias,
                onValueChange = { if (it.length <= 12) alias = it },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.label_time_slot_alias),
                singleLine = true,
            )
            TimePickerRow(
                title = stringResource(Res.string.label_time_picker_start),
                hour = startHour,
                minute = startMinute,
                onHourChange = { startHour = it },
                onMinuteChange = { startMinute = it },
            )
            TimePickerRow(
                title = stringResource(Res.string.label_time_picker_end),
                hour = endHour,
                minute = endMinute,
                onHourChange = { endHour = it },
                onMinuteChange = { endMinute = it },
            )
            Text(
                text = "${formatTime(start)} - ${formatTime(end)}",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MiuixTheme.textStyles.title3,
            )
            DialogActions(
                cancelText = stringResource(Res.string.action_cancel),
                confirmText = stringResource(if (isEditing) Res.string.action_save_changes else Res.string.action_add),
                onCancel = onDismiss,
                onConfirm = {
                    when {
                        end <= start -> ToastManager.show(endError)
                        previousEnd != null && start < previousEnd -> ToastManager.show(conflictError)
                        nextStart != null && end > nextStart -> ToastManager.show(conflictError)
                        else -> onConfirm(
                            TimeSlot(number, formatTime(start), formatTime(end), courseTableId = "", alias = alias.ifBlank { null }),
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun TimePickerRow(
    title: String,
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MiuixTheme.textStyles.body2, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
        Row(
            modifier = Modifier.fillMaxWidth().height(132.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NumberPicker(
                value = hour,
                onValueChange = onHourChange,
                modifier = Modifier.weight(1f),
                range = 0..23,
                label = { it.toString().padStart(2, '0') },
                visibleItemCount = 3,
                wrapAround = true,
            )
            Text(":", style = MiuixTheme.textStyles.title3)
            NumberPicker(
                value = minute,
                onValueChange = onMinuteChange,
                modifier = Modifier.weight(1f),
                range = 0..59,
                label = { it.toString().padStart(2, '0') },
                visibleItemCount = 3,
                wrapAround = true,
            )
        }
    }
}

@Composable
private fun DialogActions(
    cancelText: String,
    confirmText: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(text = cancelText, onClick = onCancel, modifier = Modifier.weight(1f))
        TextButton(
            text = confirmText,
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.textButtonColorsPrimary(),
        )
    }
}

private fun parseTime(value: String): LocalTime = runCatching { LocalTime.parse(value) }.getOrDefault(LocalTime(0, 0))

private fun formatTime(value: LocalTime): String =
    "${value.hour.toString().padStart(2, '0')}:${value.minute.toString().padStart(2, '0')}"

private fun LocalTime.plusMinutes(minutes: Int): LocalTime {
    val total = (hour * 60 + minute + minutes).coerceIn(0, 23 * 60 + 59)
    return LocalTime(total / 60, total % 60)
}
