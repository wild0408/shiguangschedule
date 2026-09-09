package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xingheyuzhuan.shiguangschedule.data.db.main.CourseTable
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.data.repository.AppSettingsRepository
import com.xingheyuzhuan.shiguangschedule.data.repository.CourseTableRepository
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_add_new_table
import shiguangschedule.shared.generated.resources.action_add
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_confirm
import shiguangschedule.shared.generated.resources.add_24px
import shiguangschedule.shared.generated.resources.course_table_created_at_prefix
import shiguangschedule.shared.generated.resources.course_table_id_prefix
import shiguangschedule.shared.generated.resources.dialog_title_add_table
import shiguangschedule.shared.generated.resources.label_current
import shiguangschedule.shared.generated.resources.label_table_name
import shiguangschedule.shared.generated.resources.text_no_course_tables
import shiguangschedule.shared.generated.resources.toast_add_table_success
import shiguangschedule.shared.generated.resources.toast_name_empty
import kotlin.time.Instant
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 适配 kotlinx-datetime 0.8.0 的格式化器定义
 */
private val defaultDateTimeFormat = LocalDateTime.Format {
    year()
    char('-')
    monthNumber()
    char('-')
    day()
    char(' ')
    hour()
    char(':')
    minute()
}

/**
 * 格式化毫秒时间戳为 yyyy-MM-dd HH:mm 格式
 */
private fun formatEpochMillis(epochMillis: Long): String {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return defaultDateTimeFormat.format(localDateTime)
}

/**
 * 专门为弹窗提供 Koin 注入的仓库 ViewModel
 */
@KoinViewModel
class CourseTablePickerDeps(
    val courseTableRepository: CourseTableRepository,
    val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    fun createNewCourseTable(name: String) {
        viewModelScope.launch {
            courseTableRepository.createNewCourseTable(name)
        }
    }
}

@Composable
fun CourseTablePickerDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onTableSelected: (CourseTable) -> Unit,
    deps: CourseTablePickerDeps = koinViewModel()
) {
    val courseTables by deps.courseTableRepository.getAllCourseTables().collectAsState(initial = emptyList())
    val appSettings by deps.appSettingsRepository.getAppSettings().collectAsState(initial = null)

    var selectedTable by remember { mutableStateOf<CourseTable?>(null) }

    var showAddTableDialog by remember { mutableStateOf(false) }
    var newTableName by remember { mutableStateOf("") }
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX

    LaunchedEffect(courseTables, appSettings) {
        if (selectedTable == null && appSettings?.currentCourseTableId != null) {
            selectedTable = courseTables.find { it.id == appSettings?.currentCourseTableId }
        }
    }

    if (useMiuix) {
        top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = title, onDismissRequest = onDismissRequest, insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth()) {
                if (courseTables.isEmpty()) {
                    top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.text_no_course_tables), style = MiuixTheme.textStyles.body1)
                } else {
                    LazyColumn(Modifier.fillMaxWidth().heightIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(courseTables) { courseTable ->
                            val isCurrentActive = courseTable.id == appSettings?.currentCourseTableId
                            CourseTablePickerMiuixCard(courseTable, courseTable.id == selectedTable?.id, isCurrentActive) { selectedTable = it }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    top.yukonga.miuix.kmp.basic.IconButton({ showAddTableDialog = true }) {
                        top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.add_24px), stringResource(Res.string.a11y_add_new_table), tint = MiuixTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), onDismissRequest)
                    top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_confirm), { selectedTable?.let(onTableSelected); onDismissRequest() }, enabled = selectedTable != null, colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
                }
            }
        }
    } else AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                if (courseTables.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.text_no_course_tables),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(courseTables) { courseTable ->
                            val isCurrentActive = courseTable.id == appSettings?.currentCourseTableId
                            val isSelectedForDialog = courseTable.id == selectedTable?.id

                            CourseTablePickerCard(
                                courseTable = courseTable,
                                isSelected = isSelectedForDialog,
                                isCurrentActive = isCurrentActive,
                                onCardClick = {
                                    selectedTable = it
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = { showAddTableDialog = true },
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.add_24px),
                        contentDescription = stringResource(Res.string.a11y_add_new_table),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(stringResource(Res.string.action_cancel))
                }
                Button(
                    onClick = {
                        selectedTable?.let { onTableSelected(it) }
                        onDismissRequest()
                    },
                    enabled = selectedTable != null
                ) {
                    Text(stringResource(Res.string.action_confirm))
                }
            }
        },
        dismissButton = null
    )

    if (showAddTableDialog) {
        val toastAddSuccess = stringResource(Res.string.toast_add_table_success, newTableName)
        val toastNameEmpty = stringResource(Res.string.toast_name_empty)

        if (useMiuix) {
            top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = stringResource(Res.string.dialog_title_add_table), onDismissRequest = { showAddTableDialog = false; newTableName = "" }, insideMargin = DpSize(16.dp, 16.dp)) {
                Column(Modifier.fillMaxWidth()) {
                    top.yukonga.miuix.kmp.basic.TextField(newTableName, { newTableName = it }, Modifier.fillMaxWidth(), label = stringResource(Res.string.label_table_name), singleLine = true)
                    Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_cancel), { showAddTableDialog = false; newTableName = "" }, Modifier.weight(1f))
                        top.yukonga.miuix.kmp.basic.TextButton(stringResource(Res.string.action_add), {
                            if (newTableName.isNotBlank()) { deps.createNewCourseTable(newTableName); ToastManager.show(toastAddSuccess); showAddTableDialog = false; newTableName = "" } else ToastManager.show(toastNameEmpty)
                        }, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
                    }
                }
            }
        } else AlertDialog(
            onDismissRequest = {
                showAddTableDialog = false
                newTableName = ""
            },
            title = { Text(stringResource(Res.string.dialog_title_add_table)) },
            text = {
                OutlinedTextField(
                    value = newTableName,
                    onValueChange = { newTableName = it },
                    label = { Text(stringResource(Res.string.label_table_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newTableName.isNotBlank()) {
                        deps.createNewCourseTable(newTableName)

                        // 跨平台 ToastManager 提示
                        ToastManager.show(toastAddSuccess)

                        showAddTableDialog = false
                        newTableName = ""
                    } else {
                        ToastManager.show(toastNameEmpty)
                    }
                }) {
                    Text(stringResource(Res.string.action_add))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddTableDialog = false
                    newTableName = ""
                }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun CourseTablePickerCard(
    courseTable: CourseTable,
    isSelected: Boolean,
    isCurrentActive: Boolean,
    onCardClick: (CourseTable) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick(courseTable) },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                isCurrentActive -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = courseTable.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(
                        Res.string.course_table_id_prefix,
                        courseTable.id.take(8) + "..."
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(
                        Res.string.course_table_created_at_prefix,
                        formatEpochMillis(courseTable.createdAt)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            if (isCurrentActive) {
                Text(
                    stringResource(Res.string.label_current),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CourseTablePickerMiuixCard(courseTable: CourseTable, isSelected: Boolean, isCurrentActive: Boolean, onClick: (CourseTable) -> Unit) {
    top.yukonga.miuix.kmp.basic.Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(courseTable) },
        colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = when { isSelected -> MiuixTheme.colorScheme.primaryContainer.copy(alpha = 0.24f); isCurrentActive -> MiuixTheme.colorScheme.secondaryContainer; else -> MiuixTheme.colorScheme.surfaceContainer })
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                top.yukonga.miuix.kmp.basic.Text(courseTable.name, style = MiuixTheme.textStyles.body1)
                top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.course_table_id_prefix, courseTable.id.take(8) + "..."), style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.course_table_created_at_prefix, formatEpochMillis(courseTable.createdAt)), style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
            }
            if (isCurrentActive) top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.label_current), style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.primary)
        }
    }
}
