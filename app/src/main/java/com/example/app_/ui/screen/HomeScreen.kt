package com.example.app_.ui.screen

import com.example.app_.ui.theme.AppPalette

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_.ui.theme.BeVietnamProFontFamily
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// --- Data Model cho Công việc ---
data class TodoTask(
    val id: Int,
    val title: String,
    val category: String,
    val time: String,
    val isCompleted: Boolean,
    val categoryColor: Color,
    val categoryBgColor: Color,
    val date: String = "",        // yyyy-MM-dd
    val clock: String = "",       // HH:mm
    val note: String = "",
    val priority: String = "medium" // low | medium | high
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tasks: List<TodoTask>,                 // việc của hôm nay
    onToggleTask: (Int) -> Unit,
    allTasks: List<TodoTask> = tasks,      // tất cả việc (để đếm theo nhóm)
    dateText: String = "",
    hasUnread: Boolean = true,
    onProfileClick: () -> Unit = {},
    onBellClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onTaskClick: (Int) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(fontFamily = BeVietnamProFontFamily)
    ) {
        val completedCount = tasks.count { it.isCompleted }
        val totalCount = tasks.size
        val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
        val progressPercent = (progressFraction * 100).toInt()

        val studyRemaining = allTasks.count { it.category == "Học tập" && !it.isCompleted }
        val lifeRemaining = allTasks.count { it.category == "Cuộc sống" && !it.isCompleted }

        Scaffold(
            containerColor = AppPalette.bg,
            bottomBar = {
                AppBottomBar(selected = "home", onNavigate = onNavigate, onAddClick = onAddClick)
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
                HeaderSection(dateText = dateText, hasUnread = hasUnread, onProfileClick = onProfileClick, onBellClick = onBellClick)
                Spacer(modifier = Modifier.height(20.dp))
                ProgressCard(
                    completedCount = completedCount,
                    totalCount = totalCount,
                    progressFraction = progressFraction,
                    progressPercent = progressPercent
                )
                Spacer(modifier = Modifier.height(20.dp))
                CategorySection(studyRemaining = studyRemaining, lifeRemaining = lifeRemaining)
                Spacer(modifier = Modifier.height(20.dp))
                TaskSection(
                    tasks = tasks,
                    onToggleTask = onToggleTask,
                    onSeeAllClick = { onNavigate("task_list") },
                    onTaskClick = onTaskClick
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(dateText: String, hasUnread: Boolean, onProfileClick: () -> Unit, onBellClick: () -> Unit) {
    val user = Firebase.auth.currentUser
    val displayName = user?.displayName ?: "bạn"
    val initial = displayName.firstOrNull()?.uppercase() ?: "K"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar (Clickable) - Thông số Figma (#E6EDFF, text #1F4FD1, 17sp)
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(AppPalette.primarySoft, CircleShape)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(initial, color = AppPalette.primary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(dateText, fontSize = 14.sp, color = AppPalette.textSecondary)
            Text("Chào $displayName!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppPalette.text)
        }

        // Notification Icon
        Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.clickable { onBellClick() }) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(AppPalette.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Thông báo", tint = AppPalette.text)
            }
            if (hasUnread) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(10.dp)
                        .background(Color(0xFFE5484D), CircleShape)
                        .border(2.dp, AppPalette.surface, CircleShape)
                )
            }
        }
    }
}

@Composable
fun ProgressCard(
    completedCount: Int,
    totalCount: Int,
    progressFraction: Float,
    progressPercent: Int
) {
    val message = when {
        progressPercent == 100 -> "Tuyệt vời, đã hoàn thành hết!"
        progressPercent >= 50 -> "Đã hơn nửa chặng, cố lên!"
        progressPercent > 0 -> "Hãy tiếp tục hoàn thành nhé!"
        else -> "Bắt đầu làm việc ngay nào!"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppPalette.primary, RoundedCornerShape(22.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Tiến độ hôm nay", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$completedCount/$totalCount việc", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(message, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            }
            
            // Vòng tròn Progress tự động cập nhật
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                CircularProgressIndicator(
                    progress = { 1f },
                    color = Color.White.copy(alpha = 0.2f),
                    strokeWidth = 6.dp,
                    modifier = Modifier.fillMaxSize()
                )
                CircularProgressIndicator(
                    progress = { progressFraction },
                    color = Color.White,
                    strokeWidth = 6.dp,
                    modifier = Modifier.fillMaxSize()
                )
                Text("$progressPercent%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CategorySection(studyRemaining: Int, lifeRemaining: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        CategoryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.School,
            iconColor = AppPalette.categoryBlue,
            iconBg = AppPalette.categoryBlueBg,
            title = "Học tập",
            subtitle = "$studyRemaining việc còn lại"
        )
        CategoryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Home,
            iconColor = Color(0xFFA24A08),
            iconBg = Color(0xFFFFEBD9),
            title = "Cuộc sống",
            subtitle = "$lifeRemaining việc còn lại"
        )
    }
}

@Composable
fun CategoryCard(modifier: Modifier, icon: ImageVector, iconColor: Color, iconBg: Color, title: String, subtitle: String) {
    // Đúng thông số Figma: Radius 18dp, Padding 16dp, Gap 10dp
    Column(
        modifier = modifier
            .background(AppPalette.surface, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        // Khung Icon 36x36, Radius 10dp
        Box(
            modifier = Modifier.size(36.dp).background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Title: 15sp SemiBold (weight 600), #1C1C1E
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppPalette.text)
        Spacer(modifier = Modifier.height(2.dp))
        // Subtitle: 13sp Normal (weight 400), #6B6B70
        Text(subtitle, fontSize = 13.sp, color = AppPalette.textSecondary)
    }
}

@Composable
fun TaskSection(
    tasks: List<TodoTask>,
    onToggleTask: (Int) -> Unit,
    onSeeAllClick: () -> Unit = {},
    onTaskClick: (Int) -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Việc hôm nay": 19sp Bold, #1C1C1E
            Text("Việc hôm nay", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = AppPalette.text)
            // "Xem tất cả": 14sp Medium, #2F5BEA
            Text(
                "Xem tất cả",
                fontSize = 14.sp,
                color = AppPalette.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        tasks.forEach { task ->
            TaskItem(task = task, onToggle = { onToggleTask(task.id) }, onClick = { onTaskClick(task.id) })
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun TaskItem(task: TodoTask, onToggle: () -> Unit, onClick: () -> Unit = {}) {
    // Thẻ Công việc - Chuẩn thông số Figma (Radius 16dp, Checkbox 26dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppPalette.surface, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox ô tròn (Kích thước 26x26, Radius 13dp)
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
            // Tiêu đề: 15sp Medium (weight 500), #8A8A8E khi xong / #1C1C1E khi chưa
            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (task.isCompleted) AppPalette.textTertiary else AppPalette.text,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Nhãn danh mục: 12sp SemiBold, Padding 2px 8px, Radius 6dp
                Box(
                    modifier = Modifier
                        .background(task.categoryBgColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(task.category, fontSize = 12.sp, color = task.categoryColor, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Thời gian: 12sp Normal, #6B6B70
                Text(task.clock.ifBlank { task.time }, fontSize = 12.sp, color = AppPalette.textSecondary)
            }
        }
        
        // Mũi tên Chevron: Kích thước 18x18, Màu #AEAEB2
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = "Chi tiết",
            tint = AppPalette.textTertiary,
            modifier = Modifier
                .size(18.dp)
                .clickable { onClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        tasks = emptyList(),
        onToggleTask = {}
    )
}
