package com.example.app_.ui.screen

import com.example.app_.ui.theme.AppPalette

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    tasks: List<TodoTask> = emptyList(),   // dữ liệu thật từ ViewModel
    todayStr: String = ""                  // "hôm nay" của app (yyyy-MM-dd)
) {
    // ---- Số liệu thống kê tính từ danh sách công việc thật ----
    val doneCount = tasks.count { it.isCompleted }
    val pendingCount = tasks.count { !it.isCompleted }
    val percent = if (tasks.isEmpty()) 0 else Math.round(doneCount * 100f / tasks.size)

    // Biểu đồ "Việc xong mỗi ngày": đếm việc đã xong theo thứ trong tuần của ngày hạn
    val perDay = IntArray(7)
    tasks.filter { it.isCompleted }.forEach { t -> weekdayIndex(t.date)?.let { perDay[it]++ } }
    val maxPerDay = (perDay.maxOrNull() ?: 0).coerceAtLeast(1)
    val todayIndex = weekdayIndex(todayStr)
    val dayLabels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

    // Lấy thông tin User thật từ Firebase
    val user = Firebase.auth.currentUser
    val displayName = user?.displayName ?: "Người dùng ẩn danh"
    val email = user?.email ?: "Tài khoản dùng thử"
    val initial = displayName.firstOrNull()?.uppercase() ?: "K"
    val isAnonymous = user?.isAnonymous ?: true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về", tint = AppPalette.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cài đặt", color = AppPalette.primary, fontSize = 16.sp)
                    }
                },
                actions = {
                    // Để cân bằng khoảng trống bên phải
                    Spacer(modifier = Modifier.width(72.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppPalette.bg)
            )
        },
        containerColor = AppPalette.bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(AppPalette.primarySoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(initial, color = AppPalette.primary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(displayName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppPalette.text)
            Text(email, fontSize = 14.sp, color = AppPalette.textSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            
            // Badge liên kết Google (Chỉ hiện nếu không phải tài khoản dùng thử)
            if (!isAnonymous) {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Đã liên kết tài khoản Google", color = Color(0xFF4CAF50), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stats row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("$doneCount", "Việc đã xong")
                StatItem("$pendingCount", "Đang chờ")
                StatItem("$percent%", "Hoàn thành")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Chart Placeholder
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppPalette.surface, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tuần này", fontWeight = FontWeight.Bold, color = AppPalette.text)
                    Text("Việc xong mỗi ngày", color = AppPalette.textTertiary, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dayLabels.forEachIndexed { i, label ->
                        ChartBar(label, perDay[i] * 0.9f / maxPerDay, isToday = (i == todayIndex))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = BorderStroke(1.dp, Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppPalette.text)
        Text(label, fontSize = 12.sp, color = AppPalette.textSecondary)
    }
}

@Composable
fun ChartBar(label: String, fraction: Float, isToday: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight()) {
        if (fraction > 0f) {
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .fillMaxHeight(fraction)
                    .background(
                        if (isToday) AppPalette.primary else Color(0xFF93A7F4),
                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                    )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 10.sp, color = AppPalette.textSecondary)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(onBack = {}, onLogout = {})
}
/** 0 = Thứ Hai ... 6 = Chủ Nhật; null nếu ngày không hợp lệ. */
private fun weekdayIndex(date: String): Int? {
    if (date.length != 10) return null
    val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).apply {
        timeZone = java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
    }
    val d = runCatching { fmt.parse(date) }.getOrNull() ?: return null
    val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh")).apply { time = d }
    return when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
        java.util.Calendar.MONDAY -> 0
        java.util.Calendar.TUESDAY -> 1
        java.util.Calendar.WEDNESDAY -> 2
        java.util.Calendar.THURSDAY -> 3
        java.util.Calendar.FRIDAY -> 4
        java.util.Calendar.SATURDAY -> 5
        else -> 6
    }
}
