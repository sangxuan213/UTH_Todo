package com.example.app_.data

import com.example.app_.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Lỗi có sẵn thông điệp tiếng Việt để hiện thẳng lên UI. */
class ApiException(message: String, val code: Int = 0) : Exception(message)

/** Model đúng như API trả về (camelCase, category/priority là mã chữ thường). */
data class ApiTask(
    val id: Int,
    val title: String,
    val note: String,
    val date: String,      // yyyy-MM-dd
    val time: String,      // HH:mm
    val category: String,  // study | life
    val priority: String,  // low | medium | high
    val done: Boolean
)

object TaskApi {
    private const val BASE_URL = "https://share.tryapps.store/mock/teach"

    /**
     * Bài 6: để thử lỗi, đặt thành "?__status=500" (hoặc "?__delay=3000") rồi chạy lại app.
     * Nhớ trả về "" khi nộp bài.
     */
    private const val DEBUG_LIST_QUERY = ""

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val JSON_TYPE = "application/json; charset=utf-8".toMediaType()

    private suspend fun call(method: String, path: String, body: JSONObject? = null): String =
        withContext(Dispatchers.IO) {
            val builder = Request.Builder()
                .url(BASE_URL + path)
                .header("Authorization", "Bearer ${BuildConfig.MOCK_API_TOKEN}")
                .header("Accept", "application/json")
            val requestBody = (body ?: JSONObject()).toString().toRequestBody(JSON_TYPE)
            when (method) {
                "GET" -> builder.get()
                "POST" -> builder.post(requestBody)
                "PATCH" -> builder.patch(requestBody)
                "DELETE" -> builder.delete()
            }
            try {
                client.newCall(builder.build()).execute().use { resp ->
                    val text = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        // Lỗi trả dạng { "message": "..." } -> hiện nguyên message
                        val msg = runCatching { JSONObject(text).optString("message") }.getOrDefault("")
                        throw ApiException(msg.ifBlank { "Lỗi máy chủ (${resp.code})" }, resp.code)
                    }
                    text
                }
            } catch (e: IOException) {
                throw ApiException("Không kết nối được máy chủ. Kiểm tra mạng rồi thử lại.")
            }
        }

    private fun parse(o: JSONObject) = ApiTask(
        id = o.optInt("id"),
        title = o.optString("title"),
        note = o.optString("note"),
        date = o.optString("date"),
        time = o.optString("time"),
        category = o.optString("category"),
        priority = o.optString("priority"),
        done = o.optBoolean("done")
    )

    /** Đọc 1 task từ body; trả null nếu body rỗng hoặc không có "id" (mock có thể trả khác). */
    private fun parseOrNull(text: String): ApiTask? {
        val obj = runCatching { JSONObject(text) }.getOrNull() ?: return null
        return if (obj.has("id")) parse(obj) else null
    }

    /** Bài 1: GET /tasks -> mảng tasks[] */
    suspend fun getTasks(): List<ApiTask> {
        val arr = try {
            JSONArray(call("GET", "/tasks$DEBUG_LIST_QUERY"))
        } catch (e: JSONException) {
            throw ApiException("Dữ liệu máy chủ trả về không đúng định dạng.")
        }
        return List(arr.length()) { parse(arr.getJSONObject(it)) }
    }

    /** Bài 2: GET /tasks/{id} */
    suspend fun getTask(id: Int): ApiTask =
        parseOrNull(call("GET", "/tasks/$id")) ?: throw ApiException("Không đọc được chi tiết công việc.")

    /** Bài 4: POST /tasks -> 201 + task vừa tạo */
    suspend fun createTask(
        title: String, note: String, date: String, time: String,
        category: String, priority: String
    ): ApiTask {
        val body = JSONObject()
            .put("title", title)
            .put("note", note)
            .put("date", date)
            .put("time", time)
            .put("category", category)
            .put("priority", priority)
        return parseOrNull(call("POST", "/tasks", body))
            ?: throw ApiException("Máy chủ không trả về công việc vừa tạo.")
    }

    /** Bài 3: PATCH /tasks/{id} với { "done": true }. Trả null nếu server không trả task. */
    suspend fun setDone(id: Int, done: Boolean): ApiTask? =
        parseOrNull(call("PATCH", "/tasks/$id", JSONObject().put("done", done)))

    /** Sửa công việc: PATCH /tasks/{id} chỉ gửi các field cần đổi. Trả null nếu server không trả task. */
    suspend fun updateTask(
        id: Int, title: String, note: String, date: String, time: String,
        category: String, priority: String
    ): ApiTask? {
        val body = JSONObject()
            .put("title", title)
            .put("note", note)
            .put("date", date)
            .put("time", time)
            .put("category", category)
            .put("priority", priority)
        return parseOrNull(call("PATCH", "/tasks/$id", body))
    }

    /** Bài 5: DELETE /tasks/{id} -> 204, không có body */
    suspend fun deleteTask(id: Int) {
        call("DELETE", "/tasks/$id")
    }
}
