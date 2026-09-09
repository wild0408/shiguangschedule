package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme
import org.jetbrains.compose.resources.stringResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_share
import shiguangschedule.shared.generated.resources.dialog_text_file_saved_share_prompt
import shiguangschedule.shared.generated.resources.dialog_title_file_saved

/**
 * 平台开关：
 * - Android / iOS: true（启用分享管理器与相关提示）
 * - Desktop (JVM): false（直接拦截，不调用分享管理器）
 */
expect val isShareDialogSupported: Boolean

/**
 * 平台底层文件分享执行逻辑
 */
expect fun platformShareFile(filePath: String, mimeType: String)

/**
 * 文件保存成功后的分享确认弹窗（分享管理器的 UI 组件之一）
 */
@Composable
fun ShareDialog(
    filePath: String,
    mimeType: String,
    onDismiss: () -> Unit
) {
    if (!isShareDialogSupported) return

    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.window.WindowDialog(
            show = true,
            onDismissRequest = onDismiss,
            title = stringResource(Res.string.dialog_title_file_saved),
            insideMargin = DpSize(16.dp, 16.dp)
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.dialog_text_file_saved_share_prompt), style = MiuixTheme.textStyles.body1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), onDismiss, Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_share), { platformShareFile(filePath, mimeType); onDismiss() }, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
                }
            }
        }
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.dialog_title_file_saved)) },
        text = { Text(stringResource(Res.string.dialog_text_file_saved_share_prompt)) },
        confirmButton = {
            TextButton(
                onClick = {
                    platformShareFile(filePath, mimeType)
                    onDismiss()
                }
            ) {
                Text(stringResource(Res.string.action_share))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.action_cancel))
            }
        }
    )
}
