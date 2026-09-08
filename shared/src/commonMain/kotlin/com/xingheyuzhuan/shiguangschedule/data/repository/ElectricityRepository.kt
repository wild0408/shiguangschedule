package com.xingheyuzhuan.shiguangschedule.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xingheyuzhuan.shiguangschedule.data.api.electricity.ElectricityApi
import com.xingheyuzhuan.shiguangschedule.data.api.electricity.ElectricityLocation
import com.xingheyuzhuan.shiguangschedule.data.db.main.ElectricityHistory
import com.xingheyuzhuan.shiguangschedule.data.db.main.ElectricityHistoryDao
import com.xingheyuzhuan.shiguangschedule.tool.SecureCrypto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import kotlin.time.Clock

data class ElectricityConfig(val studentId: String, val password: String, val campus: ElectricityLocation, val building: ElectricityLocation, val room: ElectricityLocation, val token: String = "")

@Single
class ElectricityRepository(
    @Named("ApiConfig") private val store: DataStore<Preferences>,
    private val crypto: SecureCrypto,
    private val historyDao: ElectricityHistoryDao,
    private val api: ElectricityApi
) {
    private object K { val student=stringPreferencesKey("electricity_student"); val pwd=stringPreferencesKey("electricity_pwd"); val iv=stringPreferencesKey("electricity_iv"); val campus=stringPreferencesKey("electricity_campus"); val campusName=stringPreferencesKey("electricity_campus_name"); val building=stringPreferencesKey("electricity_building"); val buildingName=stringPreferencesKey("electricity_building_name"); val room=stringPreferencesKey("electricity_room"); val roomName=stringPreferencesKey("electricity_room_name"); val token=stringPreferencesKey("electricity_token") }
    val configFlow: Flow<ElectricityConfig?> = store.data.map { p ->
        val pwd = if (p[K.pwd] != null && p[K.iv] != null) crypto.decrypt(p[K.pwd]!!, p[K.iv]!!) else null
        if (p[K.student].isNullOrBlank() || pwd == null || p[K.campus].isNullOrBlank() || p[K.building].isNullOrBlank() || p[K.room].isNullOrBlank()) null else ElectricityConfig(p[K.student]!!, pwd, ElectricityLocation(p[K.campusName] ?: "", p[K.campus]!!), ElectricityLocation(p[K.buildingName] ?: "", p[K.building]!!), ElectricityLocation(p[K.roomName] ?: "", p[K.room]!!), p[K.token] ?: "")
    }
    suspend fun save(config: ElectricityConfig) { val c=crypto.encrypt(config.password) ?: error("无法安全保存密码"); store.edit { p -> p[K.student]=config.studentId; p[K.pwd]=c.encryptedData; p[K.iv]=c.iv; p[K.campus]=config.campus.value; p[K.campusName]=config.campus.name; p[K.building]=config.building.value; p[K.buildingName]=config.building.name; p[K.room]=config.room.value; p[K.roomName]=config.room.name; if (config.token.isNotBlank()) p[K.token]=config.token } }
    suspend fun clear(config: ElectricityConfig?) { store.edit { p -> listOf(K.student,K.pwd,K.iv,K.campus,K.campusName,K.building,K.buildingName,K.room,K.roomName,K.token).forEach { p.remove(it) } }; config?.let { historyDao.delete(it.studentId, it.room.value) } }
    suspend fun login(id:String,pwd:String)=api.login(id,pwd)
    suspend fun campuses(token:String)=api.locations(token,0)
    suspend fun buildings(token:String,campus:String)=api.locations(token,1,campus)
    suspend fun rooms(token:String,campus:String,building:String)=api.locations(token,2,campus,building)
    suspend fun query(config: ElectricityConfig, token: String): Pair<Double,String> { val b=api.balance(token,config.campus.value,config.building.value,config.room.value); historyDao.insert(ElectricityHistory(studentId=config.studentId,roomKey=config.room.value,recordedAt=Clock.System.now().toEpochMilliseconds(),balance=b)); historyDao.trim(config.studentId,config.room.value); return b to token }
    fun history(config: ElectricityConfig): Flow<List<ElectricityHistory>> = historyDao.observe(config.studentId, config.room.value)
}
