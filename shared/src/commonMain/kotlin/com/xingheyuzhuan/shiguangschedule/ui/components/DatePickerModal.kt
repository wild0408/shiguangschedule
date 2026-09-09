package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_confirm

/**
 * 日期选择器对话框。
 *
 * @param onDateSelected 用户选择日期后的回调，参数为选中日期的毫秒数。
 * @param onDismiss 对话框被关闭时的回调。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    title: String? = null,
) {
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker
    )
    if (useMiuix) {
        MiuixDateWheelDialog(onDateSelected, onDismiss, title)
        return
    }
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(Res.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.action_cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            modifier = Modifier
        )
    }
}

@Composable
private fun MiuixDateWheelDialog(onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit, title: String?) {
    val today = kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault())
    var year by androidx.compose.runtime.remember { androidx.compose.runtime.mutableIntStateOf(today.year) }
    var month by androidx.compose.runtime.remember { androidx.compose.runtime.mutableIntStateOf(today.month.number) }
    var day by androidx.compose.runtime.remember { androidx.compose.runtime.mutableIntStateOf(today.day) }
    val years = remember { (today.year - 5..today.year + 10).toList() }
    val months = remember { (1..12).toList() }
    val days = remember { (1..31).toList() }
    top.yukonga.miuix.kmp.window.WindowDialog(
        title = title ?: "选择开学日期",
        show = true,
        onDismissRequest = onDismiss,
        insideMargin = DpSize(16.dp, 16.dp),
    ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NativeNumberPicker(years, year, { year = it }, Modifier.weight(1f).height(150.dp))
                    NativeNumberPicker(months, month, { month = it }, Modifier.weight(1f).height(150.dp))
                    NativeNumberPicker(days, day, { day = it }, Modifier.weight(1f).height(150.dp))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), onDismiss, Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_confirm), {
                        val millis = try { LocalDateTime(LocalDate(year, month, day), LocalTime(0, 0)).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds() } catch (_: Exception) { null }
                        onDateSelected(millis)
                        onDismiss()
                    }, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
                }
            }
    }
}
