package com.xingheyuzhuan.shiguangschedule.data.repository

import com.xingheyuzhuan.shiguangschedule.data.db.main.GradeDao
import com.xingheyuzhuan.shiguangschedule.data.db.main.toEntity
import com.xingheyuzhuan.shiguangschedule.data.model.GradeRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class GradeRepository(private val gradeDao: GradeDao) {
    fun observeAll(): Flow<List<GradeRecord>> = gradeDao.observeAll().map { rows -> rows.map { it.toModel() } }

    suspend fun replaceAll(records: List<GradeRecord>) {
        records.map { it.semester }.distinct().forEach { gradeDao.deleteSemester(it) }
        gradeDao.upsertAll(records.map { it.toEntity() })
    }
}
