package com.xingheyuzhuan.shiguangschedule.data.api.electricity

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

data class ElectricityLocation(val name: String, val value: String)

@Serializable
private data class ApiResponse(val code: Int? = null, val msg: String? = null, val map: JsonObject? = null, val access_token: String? = null)

class ElectricityApi(private val client: HttpClient) {
    companion object {
        private const val BASE = "https://icard.nuist.edu.cn"
        private const val AUTH = "$BASE/berserker-auth/oauth/token"
        private const val QUERY = "$BASE/charge/feeitem/getThirdData"
        private const val AUTH_BASIC = "Basic bW9iaWxlX3NlcnZpY2VfcGxhdGZvcm06bW9iaWxlX3NlcnZpY2VfcGxhdGZvcm1fc2VjcmV0"
    }

    private suspend fun post(token: String?, params: Parameters): ApiResponse = client.submitForm(QUERY, params) {
        contentType(ContentType.Application.FormUrlEncoded)
        header("synjones-auth", "bearer $token")
        header("synaccesssource", "pc")
        header("origin", BASE)
        header("referer", "$BASE/")
    }.body()

    suspend fun login(studentId: String, password: String): String {
        val response: ApiResponse = client.submitForm(AUTH, Parameters.build {
            append("username", studentId); append("password", password); append("grant_type", "password")
            append("scope", "all"); append("loginFrom", "pc"); append("logintype", "snoNew")
        }) {
            contentType(ContentType.Application.FormUrlEncoded)
            header("Authorization", AUTH_BASIC); header("synaccesssource", "pc"); header("origin", BASE); header("referer", "$BASE/")
        }.body()
        return response.access_token ?: error(response.msg ?: "登录失败")
    }

    suspend fun locations(token: String, level: Int, campus: String? = null, building: String? = null): List<ElectricityLocation> {
        val response = post(token, Parameters.build {
            append("type", "select"); append("level", level.toString()); append("feeitemid", "448")
            campus?.let { append("xiaoqu_id", it) }; building?.let { append("loudong_id", it) }
        })
        if (response.code != 200) error(response.msg ?: "位置查询失败")
        return response.map?.get("data")?.jsonArray?.mapNotNull { item ->
            val obj = item.jsonObject
            val name = obj["name"]?.jsonPrimitive?.content
            val value = obj["value"]?.jsonPrimitive?.content
            if (name != null && value != null) ElectricityLocation(name, value) else null
        } ?: emptyList()
    }

    suspend fun balance(token: String, campus: String, building: String, room: String): Double {
        val response = post(token, Parameters.build {
            append("type", "IEC"); append("level", "3"); append("feeitemid", "448")
            append("xiaoqu_id", campus); append("loudong_id", building); append("room_id", room)
        })
        if (response.code != 200) error(response.msg ?: "电量查询失败")
        val show = response.map?.get("showData")?.jsonObject ?: error("接口未返回电量")
        val value = show.entries.firstOrNull { it.key.contains("剩余") && it.key.contains("电量") }?.value?.jsonPrimitive?.content
        return value?.toDoubleOrNull() ?: error("电量格式异常")
    }
}
