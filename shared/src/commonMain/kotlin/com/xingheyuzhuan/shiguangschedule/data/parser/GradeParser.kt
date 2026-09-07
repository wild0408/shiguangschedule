package com.xingheyuzhuan.shiguangschedule.data.parser

import com.xingheyuzhuan.shiguangschedule.data.model.GradeRecord
import com.xingheyuzhuan.shiguangschedule.data.model.GradeSemesterSummary
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val gradeJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

/** 解析适配脚本回传的统一成绩数组，不处理任何学校的原始字段。 */
fun parseGradeRecords(responseJson: String): List<GradeRecord> {
    val root = runCatching { gradeJson.parseToJsonElement(responseJson) }.getOrNull() ?: return emptyList()
    val rows = root.findGradeRows() ?: return emptyList()

    return rows.mapIndexedNotNull { index, element ->
        element.toGradeRecord(index)
    }
}

private fun JsonElement.findGradeRows(): JsonArray? = when (this) {
    is JsonArray -> if (any { it.looksLikeGradeRecord() }) this else firstNotNullOfOrNull { it.findGradeRows() }
    is JsonObject -> {
        this["records"]?.findGradeRows()
    }
    else -> null
}

private fun JsonElement.looksLikeGradeRecord(): Boolean {
    val obj = this as? JsonObject ?: return false
    return obj.containsKey("courseName")
}

/** 按学期生成页面总览；排名必须由教务脚本提供，解析器不会自行估算。 */
fun summarizeGrades(records: List<GradeRecord>, semester: String): GradeSemesterSummary {
    val semesterRecords = records.filter { it.semester == semester }
    val creditPairs = semesterRecords.mapNotNull { record ->
        val credits = record.credits.toDoubleOrNull()
        val point = record.gradePoint.toDoubleOrNull()
        if (credits != null && point != null) credits to point else null
    }
    val credits = semesterRecords.mapNotNull { it.credits.toDoubleOrNull() }.sum()
    val gpa = if (creditPairs.isNotEmpty() && creditPairs.sumOf { it.first } > 0.0) {
        val totalCredits = creditPairs.sumOf { it.first }
        creditPairs.sumOf { it.first * it.second }.div(totalCredits)
    } else null
    return GradeSemesterSummary(
        semester = semester,
        gpa = gpa?.formatNumber() ?: "--",
        totalCredits = credits.formatNumber(),
        courseCount = semesterRecords.size
    )
}

private fun JsonElement.toGradeRecord(index: Int): GradeRecord? {
    val obj = this as? JsonObject ?: return null
    val courseName = obj.text("courseName") ?: return null
    val semester = obj.text("semester") ?: return null
    return GradeRecord(
        id = obj.text("id") ?: "grade-$index",
        courseName = courseName,
        courseType = obj.text("courseType") ?: "未分类",
        credits = obj.text("credits") ?: "--",
        score = obj.text("score") ?: "暂无",
        gradePoint = obj.text("gradePoint") ?: "--",
        semester = semester,
        status = obj.text("status"),
        teacher = obj.text("teacher"),
        examType = obj.text("examType"),
        scoreComposition = obj.text("scoreComposition")
    )
}

private fun JsonObject.text(vararg keys: String): String? = keys.asSequence()
    .mapNotNull { this[it]?.jsonPrimitive?.contentOrNull }
    .map(String::trim)
    .firstOrNull { it.isNotEmpty() && it.lowercase() != "null" }

private fun Double.formatNumber(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.2f".format(this)
