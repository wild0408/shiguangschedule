package com.xingheyuzhuan.shiguangschedule.data.repository

import com.xingheyuzhuan.shiguangschedule.data.model.GradeRecord
import com.xingheyuzhuan.shiguangschedule.data.model.GradeSemesterSummary
import com.xingheyuzhuan.shiguangschedule.ui.service.GradeCenterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** 临时成绩导入状态；后续由 GradeRepository 持久化替换。 */
object GradeImportStore {
    private val _state = MutableStateFlow<GradeCenterUiState?>(null)
    val state: StateFlow<GradeCenterUiState?> = _state.asStateFlow()

    fun replace(records: List<GradeRecord>) {
        val semesters = records.map { it.semester }.distinct()
        val summaries = semesters.associateWith { semester -> summarize(records, semester) }
        _state.value = GradeCenterUiState(
            semesters = semesters,
            selectedSemester = semesters.firstOrNull(),
            summary = semesters.firstOrNull()?.let { summaries[it] },
            records = records,
            semesterSummaries = summaries
        )
    }

    private fun summarize(records: List<GradeRecord>, semester: String) =
        GradeSemesterSummary(
            semester = semester,
            gpa = weightedGpa(records.filter { it.semester == semester }),
            totalCredits = records.filter { it.semester == semester }
                .mapNotNull { it.credits.toDoubleOrNull() }.sum().toString(),
            courseCount = records.count { it.semester == semester }
        )

    private fun weightedGpa(records: List<GradeRecord>): String {
        val pairs = records.mapNotNull { r ->
            val c = r.credits.toDoubleOrNull(); val p = r.gradePoint.toDoubleOrNull()
            if (c != null && p != null) c to p else null
        }
        val credits = pairs.sumOf { it.first }
        return if (credits > 0) "%.2f".format(pairs.sumOf { it.first * it.second } / credits) else "--"
    }
}
