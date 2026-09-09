package com.xingheyuzhuan.shiguangschedule.ui.settings.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import com.xingheyuzhuan.shiguangschedule.data.model.AutoControlMode
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.components.ToastManager
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_close
import shiguangschedule.shared.generated.resources.action_confirm
import shiguangschedule.shared.generated.resources.action_go_to_settings
import shiguangschedule.shared.generated.resources.auto_mode_dnd
import shiguangschedule.shared.generated.resources.auto_mode_dnd_permission_warning
import shiguangschedule.shared.generated.resources.auto_mode_off
import shiguangschedule.shared.generated.resources.auto_mode_silent
import shiguangschedule.shared.generated.resources.dialog_text_clear_confirmation
import shiguangschedule.shared.generated.resources.dialog_text_dnd_permission
import shiguangschedule.shared.generated.resources.dialog_text_exact_alarm_permission
import shiguangschedule.shared.generated.resources.dialog_title_auto_mode_selection
import shiguangschedule.shared.generated.resources.dialog_title_clear_confirmation
import shiguangschedule.shared.generated.resources.dialog_title_dnd_permission
import shiguangschedule.shared.generated.resources.dialog_title_exact_alarm_permission
import shiguangschedule.shared.generated.resources.dialog_title_set_remind_time
import shiguangschedule.shared.generated.resources.dialog_title_view_skipped_dates
import shiguangschedule.shared.generated.resources.label_minutes_input
import shiguangschedule.shared.generated.resources.skipped_dates_none
import shiguangschedule.shared.generated.resources.toast_clear_failed
import shiguangschedule.shared.generated.resources.toast_clear_success

/**
 * Android 专属的弹窗派发器
 * 移除多余的 Worker 触发回调，由后台的 SyncManager 统一通过 Flow 响应式调度
 */
@Composable
fun NotificationDialogDispatcher(
    uiState: NotificationSettingsUiState,
    viewModel: NotificationSettingsViewModel
) {
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX
    val coroutineScope = rememberCoroutineScope()
    var showDndGuideDialog by remember { mutableStateOf(false) }

    when (uiState.activeDialog) {
        is NotificationDialogType.EditRemindMinutes -> {
            var tempInput by remember(uiState.remindBeforeMinutes) {
                mutableStateOf(uiState.remindBeforeMinutes.toString())
            }
            EditRemindMinutesDialog(
                currentMinutes = tempInput,
                onMinutesChange = { tempInput = it.filter { c -> c.isDigit() } },
                onConfirm = {
                    val mins = tempInput.toIntOrNull() ?: 15
                    viewModel.updateRemindBeforeMinutes(mins)
                    viewModel.dismissDialog()
                },
                onDismiss = { viewModel.dismissDialog() }
            )
        }

        is NotificationDialogType.AutoModeSelection -> {
            AutoModeSelectionDialog(
                currentAutoModeEnabled = uiState.autoModeEnabled,
                currentAutoControlMode = uiState.autoControlMode,
                hasDndPermission = uiState.dndPermissionStatus,
                onModeSelected = { selectedKey ->
                    if (selectedKey == "OFF") {
                        viewModel.updateAutoMode(false, uiState.autoControlMode)
                    } else if (selectedKey is AutoControlMode) {
                        viewModel.updateAutoMode(true, selectedKey)
                    }
                    viewModel.dismissDialog()
                },
                onRequireDndPermission = {
                    showDndGuideDialog = true
                },
                onDismiss = { viewModel.dismissDialog() }
            )
        }

        is NotificationDialogType.ClearConfirmation -> {
            val successMsg = stringResource(Res.string.toast_clear_success)

            if (useMiuix) top.yukonga.miuix.kmp.window.WindowDialog(
                show = true,
                onDismissRequest = { viewModel.dismissDialog() },
                title = stringResource(Res.string.dialog_title_clear_confirmation),
                insideMargin = DpSize(16.dp, 16.dp)
            ) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.dialog_text_clear_confirmation), style = MiuixTheme.textStyles.body1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                    MiuixDialogActions(onDismiss = { viewModel.dismissDialog() }, onConfirm = {
                        viewModel.clearSkippedDates { result -> result.fold(onSuccess = { ToastManager.show(successMsg) }, onFailure = { e -> coroutineScope.launch { ToastManager.show(getString(Res.string.toast_clear_failed, e.message ?: "")) } }) }
                        viewModel.dismissDialog()
                    })
                }
            } else AlertDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = { Text(stringResource(Res.string.dialog_title_clear_confirmation)) },
                text = { Text(stringResource(Res.string.dialog_text_clear_confirmation)) },
                confirmButton = {
                    Button(onClick = {
                        viewModel.clearSkippedDates { result ->
                            result.fold(
                                onSuccess = { ToastManager.show(successMsg) },
                                onFailure = { e ->
                                    coroutineScope.launch {
                                        val errorMsg = getString(Res.string.toast_clear_failed, e.message ?: "")
                                        ToastManager.show(errorMsg)
                                    }
                                }
                            )
                        }
                        viewModel.dismissDialog()
                    }) { Text(stringResource(Res.string.action_confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDialog() }) { Text(stringResource(Res.string.action_cancel)) }
                }
            )
        }

        is NotificationDialogType.ViewSkippedDates -> {
            ViewSkippedDatesDialog(
                dates = uiState.skippedDates,
                onDismiss = { viewModel.dismissDialog() }
            )
        }

        else -> {}
    }

    // 选中自动模式无权限时，弹出勿扰权限引导弹窗
    if (showDndGuideDialog) {
        DndPermissionGuideDialog(
            onDismiss = { showDndGuideDialog = false }
        )
    }
}

/**
 * Android 专属的权限引导弹窗组
 * 由 Android 侧组件按照本地 UI State 显隐控制并调用
 */
@Composable
fun ExactAlarmPermissionGuideDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    PermissionGuideDialog(
        title = stringResource(Res.string.dialog_title_exact_alarm_permission),
        text = stringResource(Res.string.dialog_text_exact_alarm_permission),
        onConfirm = { openExactAlarmSettings(context) },
        onDismiss = onDismiss
    )
}

@Composable
fun DndPermissionGuideDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    PermissionGuideDialog(
        title = stringResource(Res.string.dialog_title_dnd_permission),
        text = stringResource(Res.string.dialog_text_dnd_permission),
        onConfirm = { openDndSettings(context) },
        onDismiss = onDismiss
    )
}

@Composable
fun AutoModeSelectionDialog(
    currentAutoModeEnabled: Boolean,
    currentAutoControlMode: AutoControlMode,
    hasDndPermission: Boolean,
    onModeSelected: (Any) -> Unit,
    onRequireDndPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedKey by remember { mutableStateOf<Any>(if (currentAutoModeEnabled) currentAutoControlMode else "OFF") }

    val modeOptions = listOf(
        "OFF" to stringResource(Res.string.auto_mode_off),
        AutoControlMode.DND to stringResource(Res.string.auto_mode_dnd),
        AutoControlMode.SILENT to stringResource(Res.string.auto_mode_silent)
    )

    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.window.WindowDialog(
            show = true,
            onDismissRequest = onDismiss,
            title = stringResource(Res.string.dialog_title_auto_mode_selection),
            insideMargin = DpSize(16.dp, 16.dp)
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!hasDndPermission) top.yukonga.miuix.kmp.basic.Text(
                    stringResource(Res.string.auto_mode_dnd_permission_warning),
                    color = MiuixTheme.colorScheme.error,
                    style = MiuixTheme.textStyles.footnote1
                )
                modeOptions.forEach { (optionKey, label) ->
                    top.yukonga.miuix.kmp.basic.BasicComponent(
                        title = label,
                        onClick = { selectedKey = optionKey },
                        endActions = { top.yukonga.miuix.kmp.basic.RadioButton(selected = selectedKey == optionKey, onClick = { selectedKey = optionKey }) }
                    )
                }
                MiuixDialogActions(onDismiss = onDismiss, onConfirm = {
                    if (selectedKey != "OFF" && !hasDndPermission) { onDismiss(); onRequireDndPermission() } else onModeSelected(selectedKey)
                })
            }
        }
        return
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.dialog_title_auto_mode_selection)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!hasDndPermission) {
                    Text(
                        text = stringResource(Res.string.auto_mode_dnd_permission_warning),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                modeOptions.forEach { (optionKey, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedKey = optionKey }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(selected = (selectedKey == optionKey), onClick = { selectedKey = optionKey })
                        Text(label, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (selectedKey != "OFF" && !hasDndPermission) {
                    onDismiss()
                    onRequireDndPermission()
                } else {
                    onModeSelected(selectedKey)
                }
            }) {
                Text(stringResource(Res.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.action_cancel))
            }
        }
    )
}

@Composable
fun EditRemindMinutesDialog(
    currentMinutes: String,
    onMinutesChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.window.WindowDialog(show = true, onDismissRequest = onDismiss, title = stringResource(Res.string.dialog_title_set_remind_time), insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                top.yukonga.miuix.kmp.basic.TextField(value = currentMinutes, onValueChange = onMinutesChange, modifier = Modifier.fillMaxWidth(), label = stringResource(Res.string.label_minutes_input), singleLine = true)
                MiuixDialogActions(onDismiss, onConfirm)
            }
        }
        return
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.dialog_title_set_remind_time)) },
        text = {
            OutlinedTextField(
                value = currentMinutes,
                onValueChange = onMinutesChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(stringResource(Res.string.label_minutes_input)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text(stringResource(Res.string.action_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_cancel)) }
        }
    )
}

@Composable
fun ViewSkippedDatesDialog(dates: Set<String>, onDismiss: () -> Unit) {
    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.window.WindowDialog(show = true, onDismissRequest = onDismiss, title = stringResource(Res.string.dialog_title_view_skipped_dates), insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (dates.isEmpty()) {
                    top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.skipped_dates_none), style = MiuixTheme.textStyles.body1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                } else {
                    LazyVerticalGrid(columns = GridCells.Adaptive(100.dp), modifier = Modifier.heightIn(max = 300.dp)) {
                        items(dates.toList().sorted()) { date ->
                            top.yukonga.miuix.kmp.basic.Card(modifier = Modifier.padding(4.dp), colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainer)) {
                                top.yukonga.miuix.kmp.basic.Text(date, modifier = Modifier.padding(10.dp), style = MiuixTheme.textStyles.footnote1)
                            }
                        }
                    }
                }
                top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_close), onDismiss, Modifier.fillMaxWidth(), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
            }
        }
        return
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.dialog_title_view_skipped_dates)) },
        text = {
            if (dates.isEmpty()) {
                Text(stringResource(Res.string.skipped_dates_none))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(dates.toList().sorted()) { date ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = date,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text(stringResource(Res.string.action_close)) }
        }
    )
}

/**
 * 通用权限引导弹窗基础 UI Component
 */
@Composable
fun PermissionGuideDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.window.WindowDialog(show = true, onDismissRequest = onDismiss, title = title, insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                top.yukonga.miuix.kmp.basic.Text(text, style = MiuixTheme.textStyles.body1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), onDismiss, Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_go_to_settings), { onConfirm(); onDismiss() }, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
                }
            }
        }
        return
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) { Text(stringResource(Res.string.action_go_to_settings)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_cancel)) }
        }
    )
}

@Composable
private fun MiuixDialogActions(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), onDismiss, Modifier.weight(1f))
        top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_confirm), onConfirm, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
    }
}
