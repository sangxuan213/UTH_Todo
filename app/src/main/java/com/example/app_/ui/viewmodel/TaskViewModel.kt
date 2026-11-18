package com.example.app_.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_.data.ApiException
import com.example.app_.data.ApiTask
import com.example.app_.data.TaskApi
import com.example.app_.ui.screen.SubTaskItem
import com.example.app_.ui.screen.TodoTask
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** Trạng thái form dùng chung cho "Việc mới" và "Sửa công việc". */
data class AddTaskUiState(
    val title: String = "",
    val note: String = "",
    val category: String = "Học tập",
    val dateOption: String = "Hôm nay",
    val time: String = "19:00",
    val isReminderEnabled: Boolean = true,
    val reminderOption: String = "Trước 15 phút",
    val priority: String = "Vừa",
    // Dùng khi sửa: giữ nguyên ngày cũ nếu người dùng không đổi ngày
    val originalDate: String = "",
    val originalDateOption: String = ""
)

// ---------- Ngày giờ (giờ Việt Nam, không dùng java.time vì minSdk 24) ----------
private val VN_TZ: TimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

private fun dateFormat() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = VN_TZ }

private fun realToday(): String = dateFormat().format(Calendar.getInstance(VN_TZ).time)

private fun addDays(date: String, days: Int): String {
    val fmt = dateFormat()
    val base = runCatching { fmt.parse(date) }.getOrNull() ?: return date
    val cal = Calendar.getInstance(VN_TZ).apply { time = base; add(Calendar.DAY_OF_YEAR, days) }
    return fmt.format(cal.time)
}

private fun daysBetween(from: String, to: String): Int? {
    val fmt = dateFormat()
    val a = runCatching { fmt.parse(from) }.getOrNull() ?: return null
    val b = runCatching { fmt.parse(to) }.getOrNull() ?: return null
    return Math.round((b.time - a.time) / 86_400_000.0).toInt()
}

/** "Hôm nay" / "Ngày mai" / "Tuần này" / dd/MM */
fun dateLabel(date: String, today: String): String {
    if (date.isBlank()) return ""
    val diff = daysBetween(today, date)
    return when {
        diff == 0 -> "Hôm nay"
        diff == 1 -> "Ngày mai"
        diff != null && diff in 2..7 -> "Tuần này"
        date.length == 10 -> "${date.substring(8, 10)}/${date.substring(5, 7)}"
        else -> date
    }
}

/** "Thứ Ba, 20 tháng 10" cho phần chào ở Trang chủ */
fun headerDateText(date: String): String {
    val d = runCatching { dateFormat().parse(date) }.getOrNull() ?: return ""
    val cal = Calendar.getInstance(VN_TZ).apply { time = d }
    val dow = when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Thứ Hai"
        Calendar.TUESDAY -> "Thứ Ba"
        Calendar.WEDNESDAY -> "Thứ Tư"
        Calendar.THURSDAY -> "Thứ Năm"
        Calendar.FRIDAY -> "Thứ Sáu"
        Calendar.SATURDAY -> "Thứ Bảy"
        else -> "Chủ Nhật"
    }
    return "$dow, ${cal.get(Calendar.DAY_OF_MONTH)} tháng ${cal.get(Calendar.MONTH) + 1}"
}

// ---------- Mã API <-> nhãn tiếng Việt ----------
private fun categoryToCode(label: String) = if (label == "Cuộc sống") "life" else "study"

private fun priorityToCode(label: String) = when (label) {
    "Thấp" -> "low"
    "Cao" -> "high"
    else -> "medium"
}

private fun priorityToLabel(code: String) = when (code) {
    "low" -> "Thấp"
    "high" -> "Cao"
    else -> "Vừa"
}

private fun ApiTask.toTodo(today: String): TodoTask {
    val isLife = category == "life"
    val timeText = listOf(dateLabel(date, today), time).filter { it.isNotBlank() }.joinToString(", ")
    return TodoTask(
        id = id,
        title = title,
        category = if (isLife) "Cuộc sống" else "Học tập",
        time = timeText,
        isCompleted = done,
        categoryColor = if (isLife) Color(0xFFA24A08) else Color(0xFF2F5BEA),
        categoryBgColor = if (isLife) Color(0xFFFFEBD9) else Color(0xFFE0E7FF),
        date = date,
        clock = time,
        note = note,
        priority = priority.ifBlank { "medium" }
    )
}

class TaskViewModel : ViewModel() {
    // Danh sách công việc (nguồn dữ liệu duy nhất cho UI), nạp từ API
    val tasks = mutableStateListOf<TodoTask>()

    /** "Hôm nay" của app (yyyy-MM-dd). Mặc định là ngày thật; xem loadTasks(). */
    var todayStr by mutableStateOf(realToday())
        private set

    var isLoading by mutableStateOf(false)
        private set

    /** Thông điệp lỗi (nguyên văn "message" của API) để UI hiển thị; null = không lỗi. */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** Thông báo nổi ngắn ("Đã lưu thay đổi"...). */
    var toast by mutableStateOf<String?>(null)
        private set

    /** Chấm đỏ trên chuông ở Trang chủ, tắt khi bấm "Đánh dấu đã đọc". */
    var hasUnreadNotifications by mutableStateOf(true)
        private set

    /** Việc nhỏ (chỉ lưu trong app, API không có). Khoá = id công việc. */
    val subtasks = mutableStateMapOf<Int, List<SubTaskItem>>()

    private var isSaving = false

    /** Id các việc tạo trong app: server mock không lưu nên GET/PATCH/DELETE có thể trả 404. */
    private val localIds = mutableSetOf<Int>()

    // Trạng thái form Thêm/Sửa
    var addTaskUiState by mutableStateOf(AddTaskUiState())
        private set

    fun dismissError() {
        errorMessage = null
    }

    fun showToast(message: String) {
        toast = message
    }

    fun clearToast() {
        toast = null
    }

    fun markAllNotificationsRead() {
        hasUnreadNotifications = false
    }

    // ------------------------------------------------------------------ API
    /** Bài 1 + Bài 6: nạp danh sách, có loading và lỗi/thử lại. */
    fun loadTasks() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = TaskApi.getTasks()
                // Nếu dữ liệu mock không có việc nào của ngày thật hôm nay, lấy ngày sớm nhất làm "hôm nay"
                val real = realToday()
                todayStr = if (result.isEmpty() || result.any { it.date == real }) {
                    real
                } else {
                    result.map { it.date }.filter { it.length == 10 }.minOrNull() ?: real
                }
                tasks.clear()
                tasks.addAll(result.map { it.toTodo(todayStr) })
            } catch (e: ApiException) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    /** Bài 2: lấy chi tiết một việc và cập nhật lại phần tử trong danh sách. */
    fun loadTask(id: Int) {
        if (id in localIds) return // việc vừa tạo trong app: đã có đủ dữ liệu, không cần hỏi server
        viewModelScope.launch {
            try {
                val updated = TaskApi.getTask(id).toTodo(todayStr)
                val index = tasks.indexOfFirst { it.id == id }
                if (index != -1) tasks[index] = updated else tasks.add(updated)
            } catch (e: ApiException) {
                // 404 mà trong danh sách đã có bản của việc này thì bỏ qua, không làm phiền người dùng
                if (!(e.code == 404 && tasks.any { it.id == id })) errorMessage = e.message
            }
        }
    }

    /** Bài 3: PATCH { done }. Cập nhật ngay trên UI, lỗi thì hoàn tác. */
    fun toggleTask(id: Int) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index == -1) return
        val before = tasks[index]
        val newDone = !before.isCompleted
        tasks[index] = before.copy(isCompleted = newDone)
        if (newDone) showToast("Tuyệt, đã xong một việc!")
        viewModelScope.launch {
            try {
                TaskApi.setDone(id, newDone)
            } catch (e: ApiException) {
                // Việc tạo trong app mà server mock không biết (404): giữ nguyên thay đổi trên máy
                if (e.code == 404 && id in localIds) return@launch
                val i = tasks.indexOfFirst { it.id == id }
                if (i != -1) tasks[i] = tasks[i].copy(isCompleted = before.isCompleted)
                errorMessage = e.message
            }
        }
    }

    /** Bài 5: DELETE. Server trả 204 rồi mới xoá khỏi danh sách. */
    fun deleteTask(id: Int) {
        viewModelScope.launch {
            try {
                try {
                    TaskApi.deleteTask(id)
                } catch (e: ApiException) {
                    // Việc tạo trong app mà server mock không biết (404) thì vẫn xoá trên máy
                    if (!(e.code == 404 && id in localIds)) throw e
                }
                tasks.removeAll { it.id == id }
                subtasks.remove(id)
                localIds.remove(id)
                showToast("Đã xóa công việc")
            } catch (e: ApiException) {
                errorMessage = e.message
            }
        }
    }

    // ------------------------------------------------------------------ Form
    fun updateTitle(title: String) {
        addTaskUiState = addTaskUiState.copy(title = title)
    }

    fun updateNote(note: String) {
        addTaskUiState = addTaskUiState.copy(note = note)
    }

    fun updateCategory(category: String) {
        addTaskUiState = addTaskUiState.copy(category = category)
    }

    fun updateDateOption(dateOption: String) {
        addTaskUiState = addTaskUiState.copy(dateOption = dateOption)
    }

    fun updateTime(time: String) {
        addTaskUiState = addTaskUiState.copy(time = time)
    }

    fun updateReminderEnabled(isEnabled: Boolean) {
        addTaskUiState = addTaskUiState.copy(isReminderEnabled = isEnabled)
    }

    fun updatePriority(priority: String) {
        addTaskUiState = addTaskUiState.copy(priority = priority)
    }

    fun resetAddTaskForm() {
        addTaskUiState = AddTaskUiState()
    }

    /** Mở màn Sửa: nạp dữ liệu công việc vào form. */
    fun startEdit(id: Int) {
        val task = tasks.find { it.id == id } ?: return
        val diff = daysBetween(todayStr, task.date)
        val option = when (diff) {
            0 -> "Hôm nay"
            1 -> "Ngày mai"
            else -> "Tuần này"
        }
        addTaskUiState = AddTaskUiState(
            title = task.title,
            note = task.note,
            category = task.category,
            dateOption = option,
            time = task.clock.ifBlank { "19:00" },
            priority = priorityToLabel(task.priority),
            originalDate = task.date,
            originalDateOption = option
        )
    }

    /**
     * Bài 4 (thêm) và sửa: POST /tasks hoặc PATCH /tasks/{id}.
     * Chỉ gọi onSuccess (đóng màn hình) khi server trả thành công.
     */
    fun saveForm(editId: Int?, onSuccess: (Int) -> Unit) {
        val form = addTaskUiState
        if (form.title.isBlank() || isSaving) return
        isSaving = true
        viewModelScope.launch {
            try {
                val date = if (editId != null && form.originalDate.isNotBlank() &&
                    form.dateOption == form.originalDateOption
                ) {
                    form.originalDate
                } else {
                    when (form.dateOption) {
                        "Ngày mai" -> addDays(todayStr, 1)
                        "Tuần này" -> addDays(todayStr, 2)
                        else -> todayStr
                    }
                }
                val title = form.title.trim()
                val note = form.note.trim()
                val category = categoryToCode(form.category)
                val priority = priorityToCode(form.priority)

                if (editId == null) {
                    val created = TaskApi.createTask(title, note, date, form.time, category, priority)
                    // Server mock trả dữ liệu mẫu và không lưu, nên hiển thị đúng những gì người dùng đã nhập.
                    // Nếu id server trả về trùng việc đã có thì tự cấp id mới để không lẫn lộn.
                    val newId = if (created.id <= 0 || tasks.any { it.id == created.id }) {
                        (tasks.maxOfOrNull { it.id } ?: 0) + 1
                    } else {
                        created.id
                    }
                    localIds.add(newId)
                    val shown = ApiTask(newId, title, note, date, form.time, category, priority, false)
                    tasks.add(0, shown.toTodo(todayStr)) // Thêm vào đầu danh sách
                    resetAddTaskForm()
                    showToast("Đã thêm công việc")
                    onSuccess(newId)
                } else {
                    val index = tasks.indexOfFirst { it.id == editId }
                    val wasDone = if (index != -1) tasks[index].isCompleted else false
                    val fromServer: ApiTask? = try {
                        TaskApi.updateTask(editId, title, note, date, form.time, category, priority)
                    } catch (e: ApiException) {
                        if (e.code == 404 && editId in localIds) null else throw e
                    }
                    val saved = fromServer
                        ?: ApiTask(editId, title, note, date, form.time, category, priority, wasDone)
                    val todo = saved.toTodo(todayStr)
                    if (index != -1) tasks[index] = todo else tasks.add(todo)
                    resetAddTaskForm()
                    showToast("Đã lưu thay đổi")
                    onSuccess(editId)
                }
            } catch (e: ApiException) {
                errorMessage = e.message
            } finally {
                isSaving = false
            }
        }
    }

    // ------------------------------------------------------------------ Việc nhỏ
    fun addSubtask(taskId: Int, text: String) {
        val current = subtasks[taskId] ?: emptyList()
        val nextId = (current.maxOfOrNull { it.id } ?: 0) + 1
        subtasks[taskId] = current + SubTaskItem(nextId, text.trim(), false)
    }

    fun toggleSubtask(taskId: Int, subId: Int) {
        subtasks[taskId] = (subtasks[taskId] ?: return).map {
            if (it.id == subId) it.copy(isDone = !it.isDone) else it
        }
    }

    fun deleteSubtask(taskId: Int, subId: Int) {
        subtasks[taskId] = (subtasks[taskId] ?: return).filter { it.id != subId }
    }
}
