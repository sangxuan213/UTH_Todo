package com.example.app_.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_.ui.theme.AppPalette
import com.example.app_.ui.theme.BeVietnamProFontFamily

data class SubTaskItem(
    val id: Int,
    val text: String,
    val isDone: Boolean
)

@Composable
fun TaskDetailScreen(
    task: TodoTask?,
    subtasks: List<SubTaskItem>,
    backLabel: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onToggleTask: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onAddSubtask: (String) -> Unit,
    onToggleSubtask: (Int) -> Unit,
    onDeleteSubtask: (Int) -> Unit
) {
    var newSubtaskText by remember { mutableStateOf("") }

    val doneSubtaskCount = subtasks.count { it.isDone }
    val totalSubtasks = subtasks.size
    val isCompleted = task?.isCompleted ?: false
    val dark = AppPalette.isDark

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(fontFamily = BeVietnamProFontFamily)
    ) {
        Scaffold(containerColor = AppPalette.bg) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Thanh trên: ‹ Trang chủ ... Sửa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onBack() }
                    ) {
                        Icon(Icons.Outlined.ChevronLeft, contentDescription = "Trở về", tint = AppPalette.primary)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(backLabel, fontSize = 16.sp, color = AppPalette.primary, fontWeight = FontWeight.Medium)
                    }
                    if (task != null) {
                        Text(
                            "Sửa",
                            fontSize = 16.sp,
                            color = AppPalette.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onEdit() }
                        )
                    }
                }

                if (task == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Đang tải công việc...", fontSize = 14.sp, color = AppPalette.textTertiary)
                    }
                    return@Column
                }

                // Phần cuộn: thẻ, ghi chú, việc nhỏ
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Nhãn: nhóm, ưu tiên, đã xong
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Tag(task.category, task.categoryColor, task.categoryBgColor)
                        when (task.priority) {
                            "high" -> Tag(
                                "Ưu tiên cao", Color(0xFFDC2626),
                                if (dark) Color(0xFF4A1D1D) else Color(0xFFFEE2E2)
                            )
                            "low" -> Tag("Ưu tiên thấp", AppPalette.textSecondary, AppPalette.fill)
                            else -> Tag("Ưu tiên vừa", AppPalette.textSecondary, AppPalette.fill)
                        }
                        if (isCompleted) {
                            Tag(
                                "Đã xong", Color(0xFF16A34A),
                                if (dark) Color(0xFF173B26) else Color(0xFFDCFCE7)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = task.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) AppPalette.textTertiary else AppPalette.text,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Thẻ Hạn chót + Nhắc nhớ
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppPalette.surface, RoundedCornerShape(16.dp))
                    ) {
                        InfoRow(
                            icon = { Icon(Icons.Outlined.CalendarToday, contentDescription = "Lịch", tint = AppPalette.textSecondary, modifier = Modifier.size(18.dp)) },
                            label = "Hạn chót",
                            value = task.time.ifBlank { "Chưa đặt" }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = AppPalette.border)
                        InfoRow(
                            icon = { Icon(Icons.Outlined.Notifications, contentDescription = "Nhắc nhớ", tint = AppPalette.textSecondary, modifier = Modifier.size(18.dp)) },
                            label = "Nhắc nhớ",
                            value = "Trước 15 phút"
                        )
                    }

                    // Ghi chú (chỉ hiện khi có nội dung)
                    if (task.note.isNotBlank()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Ghi chú", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppPalette.textTertiary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AppPalette.surface, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text(task.note, fontSize = 14.sp, color = AppPalette.text, lineHeight = 20.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Việc nhỏ
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Việc nhỏ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppPalette.textTertiary)
                        Text("$doneSubtaskCount/$totalSubtasks", fontSize = 12.sp, color = AppPalette.textTertiary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Thanh tiến độ mảnh
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(AppPalette.border, RoundedCornerShape(2.dp))
                    ) {
                        if (totalSubtasks > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(doneSubtaskCount.toFloat() / totalSubtasks)
                                    .height(4.dp)
                                    .background(AppPalette.primary, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppPalette.surface, RoundedCornerShape(16.dp))
                    ) {
                        if (subtasks.isEmpty()) {
                            Text(
                                "Chưa có việc nhỏ nào. Chia nhỏ để dễ bắt đầu hơn.",
                                fontSize = 13.sp,
                                color = AppPalette.textTertiary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 18.dp)
                            )
                        } else {
                            subtasks.forEach { sub ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(if (sub.isDone) AppPalette.primary else Color.Transparent, CircleShape)
                                            .border(2.dp, if (sub.isDone) AppPalette.primary else AppPalette.border, CircleShape)
                                            .clickable { onToggleSubtask(sub.id) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (sub.isDone) {
                                            Icon(Icons.Default.Check, contentDescription = "Xong", tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = sub.text,
                                        fontSize = 14.sp,
                                        color = if (sub.isDone) AppPalette.textTertiary else AppPalette.text,
                                        textDecoration = if (sub.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = "Xóa",
                                        tint = AppPalette.textTertiary,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable { onDeleteSubtask(sub.id) }
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = AppPalette.border)
                            }
                        }

                        if (subtasks.isEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = AppPalette.border)
                        }

                        // Ô nhập + nút Thêm
                        val canAdd = newSubtaskText.isNotBlank()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Thêm", tint = AppPalette.textTertiary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                BasicTextField(
                                    value = newSubtaskText,
                                    onValueChange = { newSubtaskText = it },
                                    singleLine = true,
                                    textStyle = TextStyle(fontFamily = BeVietnamProFontFamily, fontSize = 14.sp, color = AppPalette.text),
                                    cursorBrush = SolidColor(AppPalette.primary),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = { inner ->
                                        Box {
                                            if (newSubtaskText.isEmpty()) {
                                                Text("Thêm việc nhỏ", fontSize = 14.sp, color = AppPalette.textTertiary)
                                            }
                                            inner()
                                        }
                                    }
                                )
                            }
                            Text(
                                text = "Thêm",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canAdd) AppPalette.primary else AppPalette.textTertiary,
                                modifier = Modifier.clickable(enabled = canAdd) {
                                    onAddSubtask(newSubtaskText)
                                    newSubtaskText = ""
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Nút ghim ở đáy
                Button(
                    onClick = { onToggleTask(task.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = if (isCompleted) BorderStroke(1.dp, AppPalette.border) else null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) AppPalette.surface else AppPalette.primary
                    )
                ) {
                    Text(
                        text = if (isCompleted) "Đánh dấu chưa xong" else "Đánh dấu hoàn thành",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) AppPalette.text else Color.White
                    )
                }

                Text(
                    text = "Xóa công việc",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFDC2626),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDeleteTask(task.id)
                            onBack()
                        }
                        .padding(vertical = 14.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun Tag(text: String, color: Color, bg: Color) {
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun InfoRow(icon: @Composable () -> Unit, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, color = AppPalette.textSecondary, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppPalette.text)
    }
}
