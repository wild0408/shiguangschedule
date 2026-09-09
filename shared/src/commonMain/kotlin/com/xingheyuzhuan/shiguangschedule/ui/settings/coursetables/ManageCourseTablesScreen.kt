package com.xingheyuzhuan.shiguangschedule.ui.settings.coursetables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import com.xingheyuzhuan.shiguangschedule.data.db.main.CourseTable
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.components.ToastManager
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_add_new_table
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.a11y_current_table
import shiguangschedule.shared.generated.resources.a11y_delete
import shiguangschedule.shared.generated.resources.a11y_edit
import shiguangschedule.shared.generated.resources.a11y_save
import shiguangschedule.shared.generated.resources.action_add
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.add_24px
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.check_circle_24px
import shiguangschedule.shared.generated.resources.confirm_delete
import shiguangschedule.shared.generated.resources.course_table_created_at_prefix
import shiguangschedule.shared.generated.resources.course_table_id_prefix
import shiguangschedule.shared.generated.resources.delete_24px
import shiguangschedule.shared.generated.resources.dialog_text_confirm_delete
import shiguangschedule.shared.generated.resources.dialog_title_add_table
import shiguangschedule.shared.generated.resources.dialog_title_edit_table
import shiguangschedule.shared.generated.resources.edit_24px
import shiguangschedule.shared.generated.resources.label_table_name
import shiguangschedule.shared.generated.resources.text_no_tables_hint
import shiguangschedule.shared.generated.resources.title_manage_course_tables
import shiguangschedule.shared.generated.resources.toast_add_table_success
import shiguangschedule.shared.generated.resources.toast_delete_last_table_failed
import shiguangschedule.shared.generated.resources.toast_delete_table_success
import shiguangschedule.shared.generated.resources.toast_edit_table_success
import shiguangschedule.shared.generated.resources.toast_name_empty
import shiguangschedule.shared.generated.resources.toast_switch_table_success
import kotlin.time.Instant
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCourseTablesScreen(
    onBack: () -> Unit,
    viewModel: ManageCourseTablesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX

    // --- 对话框状态管理 ---
    var showAddTableDialog by remember { mutableStateOf(false) }
    var newTableName by remember { mutableStateOf("") }

    var showEditTableDialog by remember { mutableStateOf(false) }
    var editingTableInfo by remember { mutableStateOf<CourseTable?>(null) }
    var editedTableName by remember { mutableStateOf("") }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var tableToDelete by remember { mutableStateOf<CourseTable?>(null) }

    // --- 资源字符串 ---
    val titleManageTables = stringResource(Res.string.title_manage_course_tables)
    val a11yBack = stringResource(Res.string.a11y_back)
    val a11yAddNewTable = stringResource(Res.string.a11y_add_new_table)
    val textNoTablesHint = stringResource(Res.string.text_no_tables_hint)
    val dialogTitleAddTable = stringResource(Res.string.dialog_title_add_table)
    val labelTableName = stringResource(Res.string.label_table_name)
    val actionAdd = stringResource(Res.string.action_add)
    val actionCancel = stringResource(Res.string.action_cancel)
    val toastNameEmpty = stringResource(Res.string.toast_name_empty)
    val toastEditSuccess = stringResource(Res.string.toast_edit_table_success)
    val dialogTitleEditTable = stringResource(Res.string.dialog_title_edit_table)
    val a11ySave = stringResource(Res.string.a11y_save)
    val dialogTitleConfirmDelete = stringResource(Res.string.confirm_delete)
    val actionDelete = stringResource(Res.string.a11y_delete)
    val toastDeleteLastFailed = stringResource(Res.string.toast_delete_last_table_failed)

    Scaffold(
        containerColor = if (useMiuix) MiuixTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
        topBar = {
            if (useMiuix) top.yukonga.miuix.kmp.basic.TopAppBar(
                title = titleManageTables,
                largeTitle = titleManageTables,
                color = MiuixTheme.colorScheme.surface,
                navigationIcon = {
                    top.yukonga.miuix.kmp.basic.IconButton(onBack) {
                        top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.arrow_back_24px), a11yBack, tint = MiuixTheme.colorScheme.onSurface)
                    }
                },
                defaultWindowInsetsPadding = true,
            ) else TopAppBar(
                title = { Text(titleManageTables) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(vectorResource(Res.drawable.arrow_back_24px), contentDescription = a11yBack)
                    }
                }
            )
        },
        floatingActionButton = {
            if (useMiuix) top.yukonga.miuix.kmp.basic.FloatingActionButton(onClick = { showAddTableDialog = true }) {
                top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.add_24px), a11yAddNewTable)
            } else FloatingActionButton(onClick = { showAddTableDialog = true }) {
                Icon(vectorResource(Res.drawable.add_24px), contentDescription = a11yAddNewTable)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.courseTables.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (useMiuix) top.yukonga.miuix.kmp.basic.Text(textNoTablesHint, style = MiuixTheme.textStyles.body1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                    else Text(text = textNoTablesHint, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.courseTables, key = { it.id }) { tableInfo ->
                        val isSelected = tableInfo.id == uiState.currentActiveTableId
                        val switchSuccessMsg = stringResource(Res.string.toast_switch_table_success, tableInfo.name)

                        CourseTableCard(
                            tableInfo = tableInfo,
                            isSelected = isSelected,
                            onDeleteClick = {
                                tableToDelete = it
                                showDeleteConfirmDialog = true
                            },
                            onEditClick = {
                                editingTableInfo = it
                                editedTableName = it.name
                                showEditTableDialog = true
                            },
                            onCardClick = {
                                viewModel.switchCourseTable(it.id)
                                ToastManager.show(switchSuccessMsg)
                            }
                        )
                    }
                }
            }
        }

        // --- Add Dialog ---
        if (showAddTableDialog) {
            val addSuccessMsg = stringResource(Res.string.toast_add_table_success, newTableName)
            val dismissAdd = { showAddTableDialog = false; newTableName = "" }
            val confirmAdd = {
                if (newTableName.isNotBlank()) {
                    viewModel.createNewCourseTable(newTableName)
                    ToastManager.show(addSuccessMsg)
                    dismissAdd()
                } else ToastManager.show(toastNameEmpty)
            }
            if (useMiuix) CourseTableNameMiuixDialog(
                title = dialogTitleAddTable,
                value = newTableName,
                onValueChange = { newTableName = it },
                label = labelTableName,
                confirmText = actionAdd,
                cancelText = actionCancel,
                onConfirm = confirmAdd,
                onDismiss = dismissAdd,
            ) else AlertDialog(
                onDismissRequest = {
                    dismissAdd()
                },
                title = { Text(dialogTitleAddTable) },
                text = {
                    OutlinedTextField(
                        value = newTableName,
                        onValueChange = { newTableName = it },
                        label = { Text(labelTableName) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = confirmAdd) { Text(actionAdd) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        dismissAdd()
                    }) { Text(actionCancel) }
                }
            )
        }

        // --- Edit Dialog ---
        if (showEditTableDialog && editingTableInfo != null) {
            val dismissEdit = { showEditTableDialog = false; editingTableInfo = null; editedTableName = "" }
            val confirmEdit = {
                if (editedTableName.isNotBlank()) {
                    editingTableInfo?.let { viewModel.updateCourseTable(it.copy(name = editedTableName)) }
                    ToastManager.show(toastEditSuccess)
                    dismissEdit()
                } else ToastManager.show(toastNameEmpty)
            }
            if (useMiuix) CourseTableNameMiuixDialog(
                title = dialogTitleEditTable,
                value = editedTableName,
                onValueChange = { editedTableName = it },
                label = labelTableName,
                confirmText = a11ySave,
                cancelText = actionCancel,
                onConfirm = confirmEdit,
                onDismiss = dismissEdit,
            ) else AlertDialog(
                onDismissRequest = {
                    dismissEdit()
                },
                title = { Text(dialogTitleEditTable) },
                text = {
                    OutlinedTextField(
                        value = editedTableName,
                        onValueChange = { editedTableName = it },
                        label = { Text(labelTableName) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = confirmEdit) { Text(a11ySave) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        dismissEdit()
                    }) { Text(actionCancel) }
                }
            )
        }

        // --- Delete Dialog ---
        if (showDeleteConfirmDialog && tableToDelete != null) {
            val confirmDeleteText = stringResource(Res.string.dialog_text_confirm_delete, tableToDelete?.name ?: "")
            val deleteSuccessMsg = stringResource(Res.string.toast_delete_table_success, tableToDelete?.name ?: "")

            val dismissDelete = { showDeleteConfirmDialog = false; tableToDelete = null }
            val confirmDelete = {
                if (uiState.courseTables.size > 1) {
                    tableToDelete?.let { viewModel.deleteCourseTable(it); ToastManager.show(deleteSuccessMsg) }
                } else ToastManager.show(toastDeleteLastFailed)
                dismissDelete()
            }
            if (useMiuix) CourseTableConfirmMiuixDialog(dialogTitleConfirmDelete, confirmDeleteText, actionCancel, actionDelete, dismissDelete, confirmDelete)
            else AlertDialog(
                onDismissRequest = {
                    dismissDelete()
                },
                title = { Text(dialogTitleConfirmDelete) },
                text = { Text(confirmDeleteText) },
                confirmButton = {
                    TextButton(onClick = confirmDelete) {
                        Text(actionDelete, color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        dismissDelete()
                    }) { Text(actionCancel) }
                }
            )
        }
    }
}

@Composable
fun CourseTableCard(
    tableInfo: CourseTable,
    isSelected: Boolean,
    onDeleteClick: (CourseTable) -> Unit,
    onEditClick: (CourseTable) -> Unit,
    onCardClick: (CourseTable) -> Unit
) {
    val a11yCurrentTable = stringResource(Res.string.a11y_current_table)
    val a11yEdit = stringResource(Res.string.a11y_edit)
    val a11yDelete = stringResource(Res.string.a11y_delete)

    val formattedId = tableInfo.id.take(8) + "..."
    val formattedDate = formatTimestamp(tableInfo.createdAt)

    val idText = stringResource(Res.string.course_table_id_prefix, formattedId)
    val createdAtText = stringResource(Res.string.course_table_created_at_prefix, formattedDate)

    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX
    if (useMiuix) top.yukonga.miuix.kmp.basic.Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(16.dp),
        colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(
            color = if (isSelected) MiuixTheme.colorScheme.primaryContainer.copy(alpha = 0.22f) else MiuixTheme.colorScheme.surfaceContainer
        ),
        onClick = { onCardClick(tableInfo) },
    ) {
        CourseTableCardContent(tableInfo, isSelected, idText, createdAtText, a11yCurrentTable, a11yEdit, a11yDelete, onEditClick, onDeleteClick, true)
    } else Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick(tableInfo) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        CourseTableCardContent(tableInfo, isSelected, idText, createdAtText, a11yCurrentTable, a11yEdit, a11yDelete, onEditClick, onDeleteClick, false)
    }
}

@Composable
private fun CourseTableCardContent(
    tableInfo: CourseTable,
    isSelected: Boolean,
    idText: String,
    createdAtText: String,
    currentDescription: String,
    editDescription: String,
    deleteDescription: String,
    onEditClick: (CourseTable) -> Unit,
    onDeleteClick: (CourseTable) -> Unit,
    useMiuix: Boolean,
) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (useMiuix) Modifier else Modifier.padding(16.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (useMiuix) {
                    top.yukonga.miuix.kmp.basic.Text(tableInfo.name, style = MiuixTheme.textStyles.body1)
                    top.yukonga.miuix.kmp.basic.Text(idText, style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                    top.yukonga.miuix.kmp.basic.Text(createdAtText, style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                } else {
                    Text(text = tableInfo.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = idText, style = MaterialTheme.typography.bodySmall)
                    Text(
                    text = createdAtText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected) {
                    if (useMiuix) top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.check_circle_24px), currentDescription, tint = MiuixTheme.colorScheme.primary)
                    else Icon(
                        imageVector = vectorResource(Res.drawable.check_circle_24px),
                        contentDescription = currentDescription,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                if (useMiuix) {
                    top.yukonga.miuix.kmp.basic.IconButton({ onEditClick(tableInfo) }) { top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.edit_24px), editDescription) }
                    top.yukonga.miuix.kmp.basic.IconButton({ onDeleteClick(tableInfo) }) { top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.delete_24px), deleteDescription, tint = MiuixTheme.colorScheme.error) }
                } else {
                    IconButton(onClick = { onEditClick(tableInfo) }) { Icon(vectorResource(Res.drawable.edit_24px), contentDescription = editDescription) }
                    IconButton(onClick = { onDeleteClick(tableInfo) }) { Icon(vectorResource(Res.drawable.delete_24px), contentDescription = deleteDescription) }
                }
            }
        }
}

@Composable
private fun CourseTableNameMiuixDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    top.yukonga.miuix.kmp.window.WindowDialog(show = true, onDismissRequest = onDismiss, title = title, insideMargin = DpSize(16.dp, 16.dp)) {
          Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            top.yukonga.miuix.kmp.basic.TextField(value, onValueChange, Modifier.fillMaxWidth(), label = label, singleLine = true)
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                top.yukonga.miuix.kmp.basic.TextButton(cancelText, onDismiss, Modifier.weight(1f))
                top.yukonga.miuix.kmp.basic.TextButton(confirmText, onConfirm, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
            }
        }
    }
}

@Composable
private fun CourseTableConfirmMiuixDialog(title: String, summary: String, cancelText: String, deleteText: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    top.yukonga.miuix.kmp.window.WindowDialog(show = true, onDismissRequest = onDismiss, title = title, summary = summary, insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                top.yukonga.miuix.kmp.basic.Text(summary, style = MiuixTheme.textStyles.body1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    top.yukonga.miuix.kmp.basic.TextButton(cancelText, onDismiss, Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(deleteText, onConfirm, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColors(color = MiuixTheme.colorScheme.error))
                }
            }
    }
}

/**
 * 跨平台时间戳格式化 (yyyy-MM-dd HH:mm)
 */
private fun formatTimestamp(timestamp: Long): String {
    val localDateTime = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val year = localDateTime.year
    val month = localDateTime.month.number.toString().padStart(2, '0')
    val day = localDateTime.day.toString().padStart(2, '0')
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')

    return "$year-$month-$day $hour:$minute"
}
