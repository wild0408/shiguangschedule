package com.xingheyuzhuan.shiguangschedule.ui.miuix.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.data.model.GradeRecord
import com.xingheyuzhuan.shiguangschedule.data.model.GradeSemesterSummary
import com.xingheyuzhuan.shiguangschedule.data.repository.GradeImportStore
import com.xingheyuzhuan.shiguangschedule.data.repository.GradeRepository
import com.xingheyuzhuan.shiguangschedule.ui.service.GradeCenterUiState
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.HyperLiquidTopBarButton
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.SharedScrollBehavior
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.rememberSharedScrollBehavior
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.chrome.HyperGlassTopBar
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.utils.overScrollVertical
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject
import com.xingheyuzhuan.shiguangschedule.ui.components.LocalNavigationHostPadding
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.action_import_grade
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.chevron_right_24px
import shiguangschedule.shared.generated.resources.check_24px
import shiguangschedule.shared.generated.resources.desc_grade_empty
import shiguangschedule.shared.generated.resources.label_course_count
import shiguangschedule.shared.generated.resources.label_credits
import shiguangschedule.shared.generated.resources.label_current_semester
import shiguangschedule.shared.generated.resources.label_all_semesters
import shiguangschedule.shared.generated.resources.label_exam_type
import shiguangschedule.shared.generated.resources.label_gpa
import shiguangschedule.shared.generated.resources.label_grade_point
import shiguangschedule.shared.generated.resources.label_ranking
import shiguangschedule.shared.generated.resources.label_score
import shiguangschedule.shared.generated.resources.label_score_composition
import shiguangschedule.shared.generated.resources.label_teacher
import shiguangschedule.shared.generated.resources.list_alt_24px
import shiguangschedule.shared.generated.resources.status_no_data
import shiguangschedule.shared.generated.resources.title_grade_center
import shiguangschedule.shared.generated.resources.title_grade_empty
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ChevronBackward
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MiuixGradeCenterScreen(
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit,
    uiState: GradeCenterUiState? = null
) {
    val importedState by GradeImportStore.state.collectAsState()
    val gradeRepository: GradeRepository = koinInject()
    val savedRecords by gradeRepository.observeAll().collectAsState(initial = emptyList())
    val savedState = if (savedRecords.isEmpty()) null else GradeCenterUiState(
        semesters = savedRecords.map { it.semester }.distinct(),
        selectedSemester = savedRecords.firstOrNull()?.semester,
        records = savedRecords,
        semesterSummaries = savedRecords.map { it.semester }.distinct().associateWith { semester ->
            summarizeRecords(savedRecords, semester)
        }
    )
    // The database is the source of truth across process restarts; the store only
    // provides an immediate in-process update while Room emits its new snapshot.
    val effectiveUiState = savedState ?: importedState ?: uiState ?: GradeCenterUiState()
    var semesterMenuExpanded by remember { mutableStateOf(false) }
    var selectedSemester by remember { mutableStateOf(effectiveUiState.selectedSemester) }
    LaunchedEffect(effectiveUiState.semesters, effectiveUiState.selectedSemester) {
        if (selectedSemester == null || selectedSemester !in effectiveUiState.semesters && selectedSemester != ALL_SEMESTERS) {
            selectedSemester = effectiveUiState.selectedSemester
        }
    }
    val onImport: () -> Unit = {
        onNavigate(
            Destination.WebView(
                initialUrl = "https://jwxt.nuist.edu.cn/jwapp/sys/cjcx/*default/index.do?EMAP_LANG=zh",
                assetJsPath = "NUIST/nuist_grade.js",
                completionDestination = "grade",
                repoRoot = "grades"
            )
        )
    }

    val background = MiuixTheme.colorScheme.surface
    val scrollBehavior = rememberSharedScrollBehavior()
    val backdrop = rememberLayerBackdrop { drawRect(background); drawContent() }
    val hostPadding = LocalNavigationHostPadding.current
    val direction = LocalLayoutDirection.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = background,
        topBar = {
            HyperGlassTopBar(
                title = stringResource(Res.string.title_grade_center),
                backdrop = backdrop,
                scrollBehavior = scrollBehavior,
                startAction = { a, s ->
                    HyperLiquidTopBarButton(
                        onClick = onBack,
                        backdrop = backdrop,
                        icon = MiuixIcons.ChevronBackward,
                        contentDescription = stringResource(Res.string.a11y_back),
                        backdropAlpha = a,
                        shadowAlpha = s,
                    )
                },
                endAction = { a, s ->
                    HyperLiquidTopBarButton(
                        onClick = onImport,
                        backdrop = backdrop,
                        icon = vectorResource(Res.drawable.list_alt_24px),
                        contentDescription = stringResource(Res.string.action_import_grade),
                        backdropAlpha = a,
                        shadowAlpha = s,
                    )
                },
            )
        },
    ) { padding ->
        val contentPadding = PaddingValues(
            start = padding.calculateLeftPadding(direction) + 20.dp,
            top = padding.calculateTopPadding() + 12.dp,
            end = padding.calculateRightPadding(direction) + 20.dp,
            bottom = padding.calculateBottomPadding() + hostPadding.calculateBottomPadding() + 20.dp,
        )
        Box(Modifier.fillMaxSize().background(background).layerBackdrop(backdrop)) {
            when {
                effectiveUiState.isLoading -> LoadingContent(Modifier.padding(contentPadding))
                effectiveUiState.errorMessage != null -> ErrorContent(
                    message = effectiveUiState.errorMessage,
                    onRetry = onImport,
                    modifier = Modifier.padding(contentPadding),
                )
                else -> GradeContent(
                    uiState = effectiveUiState.forSemester(selectedSemester),
                    semesterMenuExpanded = semesterMenuExpanded,
                    onSemesterMenuChange = { semesterMenuExpanded = it },
                    onSemesterSelected = { selectedSemester = it },
                    onImport = onImport,
                    contentPadding = contentPadding,
                    scrollBehavior = scrollBehavior,
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
    onSemesterSelected: (String) -> Unit,
    onImport: () -> Unit,
    contentPadding: PaddingValues,
    scrollBehavior: SharedScrollBehavior,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().overScrollVertical().nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(
            start = contentPadding.calculateLeftPadding(LocalLayoutDirection.current),
            top = contentPadding.calculateTopPadding(),
            end = contentPadding.calculateRightPadding(LocalLayoutDirection.current),
            bottom = contentPadding.calculateBottomPadding(),
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SemesterSelector(
                semesters = uiState.semesters,
                selectedSemester = uiState.selectedSemester,
                expanded = semesterMenuExpanded,
                onExpandedChange = onSemesterMenuChange,
                onSemesterSelected = onSemesterSelected
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
    onExpandedChange: (Boolean) -> Unit,
    onSemesterSelected: (String) -> Unit
) {
    Box {
            top.yukonga.miuix.kmp.basic.Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { if (semesters.isNotEmpty()) onExpandedChange(true) },
                colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer)
            ) {
                top.yukonga.miuix.kmp.basic.BasicComponent(
                    title = stringResource(Res.string.label_current_semester),
                    summary = when (selectedSemester) { ALL_SEMESTERS -> stringResource(Res.string.label_all_semesters); else -> selectedSemester ?: stringResource(Res.string.status_no_data) },
                    onClick = { if (semesters.isNotEmpty()) onExpandedChange(true) },
                    endActions = { top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.chevron_right_24px), null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantActions) }
                )
            }
            if (expanded) {
                top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = stringResource(Res.string.label_current_semester), onDismissRequest = { onExpandedChange(false) }, insideMargin = androidx.compose.ui.unit.DpSize(16.dp, 16.dp)) {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val options = listOf(ALL_SEMESTERS) + semesters
                        options.forEach { semester ->
                            top.yukonga.miuix.kmp.basic.BasicComponent(
                                title = if (semester == ALL_SEMESTERS) stringResource(Res.string.label_all_semesters) else semester,
                                onClick = { onSemesterSelected(semester); onExpandedChange(false) },
                                endActions = { if (semester == selectedSemester) top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.check_24px), null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary) }
                            )
                        }
                    }
                }
            }
    }
}

private fun GradeCenterUiState.forSemester(semester: String?): GradeCenterUiState {
    if (semester.isNullOrBlank()) return this
    if (semester == ALL_SEMESTERS) {
        return copy(selectedSemester = semester, summary = summarizeAll(), records = records)
    }
    return copy(
        selectedSemester = semester,
        summary = semesterSummaries[semester] ?: summary,
        records = records.filter { it.semester == semester }
    )
}

private const val ALL_SEMESTERS = "__ALL_SEMESTERS__"

private fun GradeCenterUiState.summarizeAll(): GradeSemesterSummary? {
    if (records.isEmpty()) return null
    val pairs = records.mapNotNull { record ->
        val credits = record.credits.toDoubleOrNull()
        val point = record.gradePoint.toDoubleOrNull()
        if (credits != null && point != null) credits to point else null
    }
    val totalCredits = records.mapNotNull { it.credits.toDoubleOrNull() }.sum()
    val gpa = pairs.takeIf { it.isNotEmpty() }?.let {
        val credits = it.sumOf { pair -> pair.first }
        if (credits > 0) it.sumOf { pair -> pair.first * pair.second } / credits else 0.0
    }
    return GradeSemesterSummary(
        semester = ALL_SEMESTERS,
        gpa = gpa?.let { "%.2f".format(it) } ?: "--",
        totalCredits = if (totalCredits % 1.0 == 0.0) totalCredits.toInt().toString() else "%.2f".format(totalCredits),
        courseCount = records.size
    )
}

private fun summarizeRecords(records: List<GradeRecord>, semester: String): GradeSemesterSummary {
    val selected = records.filter { it.semester == semester }
    val credits = selected.mapNotNull { it.credits.toDoubleOrNull() }.sum()
    val pairs = selected.mapNotNull { r ->
        val c = r.credits.toDoubleOrNull(); val p = r.gradePoint.toDoubleOrNull()
        if (c != null && p != null) c to p else null
    }
    val total = pairs.sumOf { it.first }
    val gpa = if (total > 0) "%.2f".format(pairs.sumOf { it.first * it.second } / total) else "--"
    return GradeSemesterSummary(semester, gpa, if (credits % 1.0 == 0.0) credits.toInt().toString() else "%.2f".format(credits), selected.size)
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
    top.yukonga.miuix.kmp.basic.Card(
        modifier = modifier,
        colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            top.yukonga.miuix.kmp.basic.Text(label, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote1, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
            top.yukonga.miuix.kmp.basic.Text(value, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title2)
        }
    }
}

@Composable
private fun EmptyGradeCard(onImport: () -> Unit) {
    top.yukonga.miuix.kmp.basic.Card(
            modifier = Modifier.fillMaxWidth(),
            colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer)
        ) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.list_alt_24px), null, modifier = Modifier.size(48.dp), tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary)
                top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.title_grade_empty), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title3)
                top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.desc_grade_empty), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
                top.yukonga.miuix.kmp.basic.Button(onClick = onImport) { top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.action_import_grade)) }
            }
    }
}

@Composable
private fun GradeRecordCard(record: GradeRecord) {
    var expanded by remember(record.id) { mutableStateOf(false) }
    top.yukonga.miuix.kmp.basic.Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { expanded = !expanded },
            colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        top.yukonga.miuix.kmp.basic.Text(record.courseName, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body1, fontWeight = FontWeight.SemiBold)
                        top.yukonga.miuix.kmp.basic.Text(record.courseType, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
                    }
                    top.yukonga.miuix.kmp.basic.Text(record.score, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title2, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary)
                }
                top.yukonga.miuix.kmp.basic.Text("${stringResource(Res.string.label_credits)} ${record.credits}  ·  ${stringResource(Res.string.label_grade_point)} ${record.gradePoint}", style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2)
                record.status?.let { top.yukonga.miuix.kmp.basic.Text(it, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote1, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.secondary) }
                if (expanded) {
                    DetailLineMiuix(stringResource(Res.string.label_teacher), record.teacher)
                    DetailLineMiuix(stringResource(Res.string.label_exam_type), record.examType)
                    DetailLineMiuix(stringResource(Res.string.label_score_composition), record.scoreComposition)
                    DetailLineMiuix(stringResource(Res.string.label_score), record.score)
                }
            }
    }
}

@Composable
private fun DetailLineMiuix(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(Modifier.fillMaxWidth()) {
            top.yukonga.miuix.kmp.basic.Text(label, Modifier.weight(1f), color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
            top.yukonga.miuix.kmp.basic.Text(value)
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        top.yukonga.miuix.kmp.basic.CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        top.yukonga.miuix.kmp.basic.Text(message, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.error)
        top.yukonga.miuix.kmp.basic.Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.action_import_grade)) }
    }
}
