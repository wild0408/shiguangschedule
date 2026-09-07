package com.xingheyuzhuan.shiguangschedule.data.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class GradeParserTest {
    @Test
    fun rejectsSchoolSpecificRawRecord() {
        val records = parseGradeRecords(
            """
            [{
              "WID":"52EDE6CCEAF176C8E0633201A7C02B68",
              "XSKCM":"人工智能与科技前沿（科学广场）",
              "KCXZDM_DISPLAY":"通识(选)",
              "XF":2.0,
              "ZCJ":83.0,
              "XFJD":3.3,
              "XNXQDM":"2025-2026-2",
              "CXCKDM_DISPLAY":"初修",
              "KSLXDM_DISPLAY":"线上考试",
              "QMCJXS":"100"
            }]
            """.trimIndent()
        )

        assertEquals(0, records.size)
    }

    @Test
    fun parsesUnifiedScriptRecordAndSummarizesIt() {
        val records = parseGradeRecords(
            """
            {"records":[{
              "id":"course-1",
              "courseName":"数据结构",
              "courseType":"专业必修",
              "credits":"4.0",
              "score":"86",
              "gradePoint":"3.7",
              "semester":"2025-2026-2"
            }]}
            """.trimIndent()
        )

        val summary = summarizeGrades(records, "2025-2026-2")
        assertEquals("3.70", summary.gpa)
        assertEquals("4", summary.totalCredits)
        assertEquals(1, summary.courseCount)
        assertEquals(null, summary.ranking)
    }
}
