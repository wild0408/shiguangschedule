package com.xingheyuzhuan.shiguangschedule.ui.service

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.data.model.GradeRecord
import com.xingheyuzhuan.shiguangschedule.data.model.GradeSemesterSummary
import com.xingheyuzhuan.shiguangschedule.ui.components.AdaptiveNavigationScaffold
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.action_import_grade
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.chevron_right_24px
import shiguangschedule.shared.generated.resources.desc_grade_empty
import shiguangschedule.shared.generated.resources.label_course_count
import shiguangschedule.shared.generated.resources.label_credits
import shiguangschedule.shared.generated.resources.label_current_semester
import shiguangschedule.shared.generated.resources.label_exam_type
import shiguangschedule.shared.generated.resources.label_gpa
import shiguangschedule.shared.generated.resources.label_grade_point
import shiguangschedule.shared.generated.resources.label_ranking
import shiguangschedule.shared.generated.resources.label_score
import shiguangschedule.shared.generated.resources.label_score_composition
import shiguangschedule.shared.generated.resources.label_teacher
import shiguangschedule.shared.generated.resources.list_alt_24px
import shiguangschedule.shared.generated.resources.message_grade_import_pending
import shiguangschedule.shared.generated.resources.status_no_data
import shiguangschedule.shared.generated.resources.title_grade_center
import shiguangschedule.shared.generated.resources.title_grade_empty

data class GradeCenterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val semesters: List<String> = emptyList(),
    val selectedSemester: String? = null,
    val summary: GradeSemesterSummary? = null,
    val records: List<GradeRecord> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeCenterScreen(
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit,
    uiState: GradeCenterUiState = GradeCenterUiState()
) {
    var semesterMenuExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val importPendingMessage = stringResource(Res.string.message_grade_import_pending)
    val onImport: () -> Unit = {
        scope.launch { snackbarHostState.showSnackbar(importPendingMessage) }
    }

    AdaptiveNavigationScaffold(
        currentDestination = Destination.GradeCenter,
        onTabSelected = onNavigate,
        showNavigation = false
    ) { navigationPadding ->
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(Res.string.title_grade_center)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_back_24px),
                                contentDescription = stringResource(Res.string.a11y_back)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onImport, enabled = !uiState.isLoading) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.list_alt_24px),
                                contentDescription = stringResource(Res.string.action_import_grade)
                            )
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { contentPadding ->
            when {
                uiState.isLoading -> LoadingContent(
                    Modifier.padding(navigationPadding).padding(contentPadding)
                )
                uiState.errorMessage != null -> ErrorContent(
                    message = uiState.errorMessage,
                    onRetry = onImport,
                    modifier = Modifier.padding(navigationPadding).padding(contentPadding)
                )
                else -> GradeContent(
                    uiState = uiState,
                    semesterMenuExpanded = semesterMenuExpanded,
                    onSemesterMenuChange = { semesterMenuExpanded = it },
                    onImport = onImport,
                    modifier = Modifier.padding(navigationPadding).padding(contentPadding)
                )
            }
        }
    }
}

@Composable
private fun GradeContent(
    uiState: GradeCenterUiState,
    semesterMenuExpanded: Boolean,
    onSemesterMenuChange: (Boolean) -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SemesterSelector(
                semesters = uiState.semesters,
                selectedSemester = uiState.selectedSemester,
                expanded = semesterMenuExpanded,
                onExpandedChange = onSemesterMenuChange
            )
        }
        item { SummaryGrid(uiState.summary) }
        if (uiState.records.isEmpty()) {
            item { EmptyGradeCard(onImport) }
        } else {
            items(uiState.records, key = { it.id }) { record -> GradeRecordCard(record) }
        }
    }
}

@Composable
private fun SemesterSelector(
    semesters: List<String>,
    selectedSemester: String?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Box {
        Card(
            onClick = { if (semesters.isNotEmpty()) onExpandedChange(true) },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(Res.string.label_current_semester),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        selectedSemester ?: stringResource(Res.string.status_no_data),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Icon(
                    imageVector = vectorResource(Res.drawable.chevron_right_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            semesters.forEach { semester ->
                DropdownMenuItem(text = { Text(semester) }, onClick = { onExpandedChange(false) })
            }
        }
    }
}

@Composable
private fun SummaryGrid(summary: GradeSemesterSummary?) {
    BoxWithConstraints {
        val values = listOf(
            stringResource(Res.string.label_gpa) to (summary?.gpa ?: "--"),
            stringResource(Res.string.label_credits) to (summary?.totalCredits ?: "--"),
            stringResource(Res.string.label_course_count) to (summary?.courseCount?.toString() ?: "--"),
            stringResource(Res.string.label_ranking) to (summary?.rankingDisplay() ?: "--")
        )
        if (maxWidth >= 840.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                values.forEach { (label, value) -> SummaryItem(label, value, Modifier.weight(1f)) }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                values.chunked(2).forEach { rowValues ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowValues.forEach { (label, value) -> SummaryItem(label, value, Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

private fun GradeSemesterSummary.rankingDisplay(): String? =
    if (ranking != null && totalStudents != null) "$ranking / $totalStudents" else null

@Composable
private fun SummaryItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun EmptyGradeCard(onImport: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.list_alt_24px),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(stringResource(Res.string.title_grade_empty), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(Res.string.desc_grade_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onImport) { Text(stringResource(Res.string.action_import_grade)) }
        }
    }
}

@Composable
private fun GradeRecordCard(record: GradeRecord) {
    var expanded by remember(record.id) { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(record.courseName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(record.courseType, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(record.score, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            }
            Text("${stringResource(Res.string.label_credits)} ${record.credits}  ·  ${stringResource(Res.string.label_grade_point)} ${record.gradePoint}")
            record.status?.let { Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary) }
            if (expanded) {
                HorizontalDivider()
                DetailLine(stringResource(Res.string.label_teacher), record.teacher)
                DetailLine(stringResource(Res.string.label_exam_type), record.examType)
                DetailLine(stringResource(Res.string.label_score_composition), record.scoreComposition)
                DetailLine(stringResource(Res.string.label_score), record.score)
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value)
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(Res.string.action_import_grade))
        }
    }
}
