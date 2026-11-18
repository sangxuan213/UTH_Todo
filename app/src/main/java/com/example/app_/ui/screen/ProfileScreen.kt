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
import androidx.compose.material.icons.filled.Check
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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
    val dayLabels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

    // Lấy thông tin User thật từ Firebase
    val user = Firebase.auth.currentUser
    val displayName = user?.displayName ?: "Khách"
    val email = user?.email ?: "Chưa liên kết tài khoản"
    val initial = displayName.trim().split("\\s+".toRegex()).let { parts ->
        if (parts.size >= 2) "${parts[0].first()}${parts.last().first()}".uppercase()
        else displayName.take(2).uppercase()
    }
    val isAnonymous = user == null || user.isAnonymous

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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(AppPalette.primarySoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(initial, color = AppPalette.primary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(displayName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppPalette.text)
            Text(email, fontSize = 13.sp, color = AppPalette.textSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            
            // Hiển thị nút "Liên kết Google" nếu chưa đăng nhập Google, hoặc Badge "Đã liên kết tài khoản Google" nếu đã đăng nhập Google
            if (!isAnonymous) {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Đã liên kết tài khoản Google",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                OutlinedButton(
                    onClick = { /* Liên kết Google action */ },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = AppPalette.surface, contentColor = AppPalette.text),
                    border = BorderStroke(1.dp, AppPalette.border),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("G", fontWeight = FontWeight.Bold, color = AppPalette.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Liên kết Google", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats row (3 cards)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(modifier = Modifier.weight(1f), value = "$doneCount", label = "Việc đã xong")
                StatCard(modifier = Modifier.weight(1f), value = "$pendingCount", label = "Đang chờ")
                StatCard(modifier = Modifier.weight(1f), value = "$percent%", label = "Hoàn thành")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Weekly Chart Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppPalette.surface, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tuần này", fontWeight = FontWeight.Bold, color = AppPalette.text, fontSize = 15.sp)
                    Text("Việc xong mỗi ngày", color = AppPalette.textTertiary, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dayLabels.forEachIndexed { i, label ->
                        ChartBar(label = label, count = perDay[i], maxCount = maxPerDay)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppPalette.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, tint = Color(0xFFE5484D))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFE5484D))
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, value: String, label: String) {
    Column(
        modifier = modifier
            .background(AppPalette.surface, RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppPalette.text)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = AppPalette.textSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
fun ChartBar(label: String, count: Int, maxCount: Int) {
    val barHeight = if (maxCount > 0 && count > 0) {
        (64 * (count.toFloat() / maxCount)).coerceIn(16f, 64f).dp
    } else {
        4.dp
    }
    val barColor = if (count > 0) AppPalette.primary else AppPalette.border
    val barShape = if (count > 0) RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp) else RoundedCornerShape(2.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(90.dp)
    ) {
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(barHeight)
                .background(barColor, barShape)
        )
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
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
    }
    val d = runCatching { fmt.parse(date) }.getOrNull() ?: return null
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")).apply { time = d }
    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        else -> 6
    }
}
