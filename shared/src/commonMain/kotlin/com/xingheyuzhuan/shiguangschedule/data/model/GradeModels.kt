package com.xingheyuzhuan.shiguangschedule.data.model

data class GradeRecord(
    val id: String,
    val courseName: String,
    val courseType: String,
    val credits: String,
    val score: String,
    val gradePoint: String,
    val semester: String,
    val status: String? = null,
    val teacher: String? = null,
    val examType: String? = null,
    val scoreComposition: String? = null
)

data class GradeSemesterSummary(
    val semester: String,
    val gpa: String,
    val totalCredits: String,
    val courseCount: Int,
    val ranking: Int? = null,
    val totalStudents: Int? = null
)
