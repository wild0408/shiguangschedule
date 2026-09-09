package com.xingheyuzhuan.shiguangschedule.ui.settings.additional

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import com.xingheyuzhuan.shiguangschedule.data.model.StartScreen
import com.xingheyuzhuan.shiguangschedule.tool.UpdatePlatform
import com.xingheyuzhuan.shiguangschedule.tool.UpdateStatus
import org.jetbrains.compose.resources.stringResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_confirm
import shiguangschedule.shared.generated.resources.btn_download_update
import shiguangschedule.shared.generated.resources.dialog_checking_update
import shiguangschedule.shared.generated.resources.dialog_current_version_latest
import shiguangschedule.shared.generated.resources.dialog_new_version_found
import shiguangschedule.shared.generated.resources.dialog_select_start_screen
import shiguangschedule.shared.generated.resources.dialog_select_update_channel
import shiguangschedule.shared.generated.resources.dialog_update_check_failed
import shiguangschedule.shared.generated.resources.label_error_message
import shiguangschedule.shared.generated.resources.label_version_prefix
import shiguangschedule.shared.generated.resources.tip_please_wait

/**
 * 启动页面选择弹窗
 */
@Composable
fun StartScreenSelectionDialog(
    showDialog: Boolean,
    currentSelected: StartScreen,
    onDismiss: () -> Unit,
    onConfirm: (StartScreen) -> Unit
) {
    if (!showDialog) return
    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = stringResource(Res.string.dialog_select_start_screen), onDismissRequest = onDismiss, insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)) {
                StartScreen.entries.forEach { screen ->
                    top.yukonga.miuix.kmp.basic.BasicComponent(
                        title = stringResource(screen.labelRes),
                        onClick = { onConfirm(screen) },
                        startAction = { top.yukonga.miuix.kmp.basic.RadioButton(selected = screen == currentSelected, onClick = { onConfirm(screen) }) }
                    )
                }
                top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), onDismiss, Modifier.fillMaxWidth())
            }
        }
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.dialog_select_start_screen)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                StartScreen.entries.forEach { screen ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfirm(screen) },
                        headlineContent = { Text(stringResource(screen.labelRes)) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        leadingContent = {
                            RadioButton(
                                selected = screen == currentSelected,
                                onClick = { onConfirm(screen) }
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.action_cancel))
            }
        }
    )
}

/**
 * 更新渠道选择弹窗
 */
@Composable
fun ChannelSelectionDialog(
    showDialog: Boolean,
    currentSelected: UpdatePlatform,
    onDismiss: () -> Unit,
    onConfirm: (UpdatePlatform) -> Unit
) {
    if (!showDialog) return

    var selectedPlatform by remember(currentSelected) { mutableStateOf(currentSelected) }
    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = stringResource(Res.string.dialog_select_update_channel), onDismissRequest = onDismiss, insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)) {
                UpdatePlatform.entries.forEach { platform ->
                    top.yukonga.miuix.kmp.basic.BasicComponent(
                        title = platform.title,
                        onClick = { selectedPlatform = platform },
                        startAction = { top.yukonga.miuix.kmp.basic.RadioButton(selected = platform == selectedPlatform, onClick = { selectedPlatform = platform }) }
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), onDismiss, Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_confirm), { onConfirm(selectedPlatform) }, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
                }
            }
        }
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.dialog_select_update_channel)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                UpdatePlatform.entries.forEach { platform ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlatform = platform },
                        headlineContent = { Text(text = platform.title) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        leadingContent = {
                            RadioButton(
                                selected = platform == selectedPlatform,
                                onClick = { selectedPlatform = platform }
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedPlatform) }) {
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

/**
 * 更新检查结果弹窗
 */
@Composable
fun UpdateResultDialog(
    showDialog: Boolean,
    updateStatus: UpdateStatus,
    onDismiss: () -> Unit,
    onDownloadClick: (String) -> Unit
) {
    if (!showDialog || updateStatus is UpdateStatus.Idle || updateStatus is UpdateStatus.NotSupported) return

    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        val title = when (updateStatus) {
            is UpdateStatus.Checking -> stringResource(Res.string.dialog_checking_update)
            is UpdateStatus.Found -> stringResource(Res.string.dialog_new_version_found, updateStatus.versionName)
            is UpdateStatus.Latest -> stringResource(Res.string.dialog_current_version_latest)
            is UpdateStatus.Error -> stringResource(Res.string.dialog_update_check_failed)
        }
        val summary = when (updateStatus) {
            is UpdateStatus.Checking -> stringResource(Res.string.tip_please_wait)
            is UpdateStatus.Found -> updateStatus.changelog
            is UpdateStatus.Latest -> stringResource(Res.string.label_version_prefix, updateStatus.currentVersion)
            is UpdateStatus.Error -> stringResource(Res.string.label_error_message, updateStatus.message)
        }
        top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = title, onDismissRequest = if (updateStatus is UpdateStatus.Checking) ({}) else onDismiss, insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                if (updateStatus is UpdateStatus.Checking) top.yukonga.miuix.kmp.basic.CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                top.yukonga.miuix.kmp.basic.Text(summary, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary, modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp).verticalScroll(rememberScrollState()))
                if (updateStatus !is UpdateStatus.Checking) Row(Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(if (updateStatus is UpdateStatus.Found) Res.string.action_cancel else Res.string.action_confirm), onDismiss, Modifier.weight(1f))
                    if (updateStatus is UpdateStatus.Found) top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.btn_download_update), { onDownloadClick(updateStatus.targetUrl) }, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
                }
            }
        }
        return
    }

    if (updateStatus is UpdateStatus.Checking) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(Res.string.dialog_checking_update)) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(16.dp))
                    Text(stringResource(Res.string.tip_please_wait))
                }
            },
            confirmButton = {}
        )
        return
    }

    val (title, text, confirmBtn) = when (updateStatus) {
        is UpdateStatus.Found -> Triple(
            stringResource(Res.string.dialog_new_version_found, updateStatus.versionName),
            updateStatus.changelog,
            @Composable {
                Button(onClick = { onDownloadClick(updateStatus.targetUrl) }) {
                    Text(stringResource(Res.string.btn_download_update))
                }
            }
        )
        is UpdateStatus.Latest -> Triple(
            stringResource(Res.string.dialog_current_version_latest),
            stringResource(Res.string.label_version_prefix, updateStatus.currentVersion),
            null
        )
        is UpdateStatus.Error -> Triple(
            stringResource(Res.string.dialog_update_check_failed),
            stringResource(Res.string.label_error_message, updateStatus.message),
            null
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = { confirmBtn?.invoke() },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(if (updateStatus is UpdateStatus.Found) Res.string.action_cancel else Res.string.action_confirm))
            }
        }
    )
}
