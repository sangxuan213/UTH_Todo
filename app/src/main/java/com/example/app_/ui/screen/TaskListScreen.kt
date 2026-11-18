package com.example.app_.ui.screen

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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

@Composable
fun TaskListScreen(
    tasks: List<TodoTask>,
    onToggleTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit,
    onNavigate: (String) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("Tất cả") }

    // Lọc công việc theo ô tìm kiếm và nhóm
    val filteredTasks = tasks.filter { task ->
        val matchesQuery = task.title.contains(searchQuery.trim(), ignoreCase = true)
        val matchesCategory = when (selectedCategoryFilter) {
            "Học tập" -> task.category == "Học tập"
            "Cuộc sống" -> task.category == "Cuộc sống"
            else -> true
        }
        matchesQuery && matchesCategory
    }

    val pendingTasks = filteredTasks.filter { !it.isCompleted }
    val completedTasks = filteredTasks.filter { it.isCompleted }
    val pendingCount = tasks.count { !it.isCompleted }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(fontFamily = BeVietnamProFontFamily)
    ) {
        Scaffold(
            containerColor = AppPalette.bg,
            bottomBar = {
                AppBottomBar(selected = "task_list", onNavigate = onNavigate, onAddClick = onAddClick)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Danh sách", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AppPalette.text)
                    Text("$pendingCount việc chưa xong", fontSize = 13.sp, color = AppPalette.textSecondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ô tìm kiếm (nền xám, có nút xoá)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(AppPalette.border, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Tìm kiếm", tint = AppPalette.textTertiary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(fontFamily = BeVietnamProFontFamily, fontSize = 14.sp, color = AppPalette.text),
                            cursorBrush = SolidColor(AppPalette.primary),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { inner ->
                                Box {
                                    if (searchQuery.isEmpty()) {
                                        Text("Tìm công việc", fontSize = 14.sp, color = AppPalette.textTertiary)
                                    }
                                    inner()
                                }
                            }
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Xóa",
                            tint = AppPalette.textTertiary,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { searchQuery = "" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bộ lọc: Tất cả / Học tập / Cuộc sống
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Tất cả", "Học tập", "Cuộc sống").forEach { category ->
                        val isSelected = selectedCategoryFilter == category
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) AppPalette.text else AppPalette.surface,
                                    RoundedCornerShape(20.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AppPalette.text else AppPalette.border,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCategoryFilter = category }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) AppPalette.bg else AppPalette.text
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (pendingTasks.isEmpty() && completedTasks.isEmpty()) {
                    // Không có kết quả
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppPalette.surface, RoundedCornerShape(16.dp))
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Không tìm thấy công việc nào.",
                            fontSize = 13.sp,
                            color = AppPalette.textTertiary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    if (pendingTasks.isNotEmpty()) {
                        SectionTitle("CẦN LÀM")
                        pendingTasks.forEach { task ->
                            ListItemCard(task = task, onToggle = { onToggleTask(task.id) }, onClick = { onTaskClick(task.id) })
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                    if (completedTasks.isNotEmpty()) {
                        SectionTitle("ĐÃ XONG")
                        completedTasks.forEach { task ->
                            ListItemCard(task = task, onToggle = { onToggleTask(task.id) }, onClick = { onTaskClick(task.id) })
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppPalette.textTertiary)
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun ListItemCard(task: TodoTask, onToggle: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppPalette.surface, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(if (task.isCompleted) AppPalette.primary else Color.Transparent, CircleShape)
                .border(2.dp, if (task.isCompleted) AppPalette.primary else AppPalette.border, CircleShape)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Icon(Icons.Default.Check, contentDescription = "Xong", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (task.isCompleted) AppPalette.textTertiary else AppPalette.text,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
            // Việc đã xong chỉ hiện tiêu đề gạch ngang, giống app mẫu
            if (!task.isCompleted) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(task.categoryBgColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(task.category, fontSize = 12.sp, color = task.categoryColor, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(task.time, fontSize = 12.sp, color = AppPalette.textSecondary)
                }
            }
        }

        // Nhãn "Gấp" cho việc ưu tiên cao chưa xong
        if (!task.isCompleted && task.priority == "high") {
            Box(
                modifier = Modifier
                    .background(
                        if (AppPalette.isDark) Color(0xFF4A1D1D) else Color(0xFFFEE2E2),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Gấp", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
            }
        }
    }
}
