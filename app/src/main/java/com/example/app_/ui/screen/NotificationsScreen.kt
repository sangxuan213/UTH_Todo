package com.example.app_.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_.ui.theme.AppPalette
import com.example.app_.ui.theme.BeVietnamProFontFamily

private data class NotificationItem(
    val icon: ImageVector,
    val tint: Color,
    val bg: Color,
    val title: String,
    val body: String,
    val time: String,
    val unread: Boolean = false
)

@Composable
fun NotificationsScreen(
    hasUnread: Boolean,
    onMarkAllRead: () -> Unit,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val dark = AppPalette.isDark
    val items = listOf(
        NotificationItem(
            Icons.Outlined.AccessAlarm, AppPalette.categoryBlue,
            if (dark) Color(0xFF1E2A55) else Color(0xFFE6EDFF),
            "Sắp đến hạn", "Ôn chương 3 – Cấu trúc dữ liệu lúc 14:00.", "13:45", hasUnread
        ),
        NotificationItem(
            Icons.Outlined.Warning, Color(0xFFC62828),
            if (dark) Color(0xFF4A1D1D) else Color(0xFFFDE7E7),
            "Đã quá hạn", "Bài đọc tham khảo môn Mạng máy tính chưa xong.", "10:10", hasUnread
        ),
        NotificationItem(
            Icons.Filled.Check, Color(0xFF1F7A4D),
            if (dark) Color(0xFF173B26) else Color(0xFFDDF5EA),
            "Làm tốt lắm!", "Bạn đã xong 2 việc trong sáng nay.", "11:30"
        ),
        NotificationItem(
            Icons.Outlined.AccessAlarm, AppPalette.categoryBlue,
            if (dark) Color(0xFF1E2A55) else Color(0xFFE6EDFF),
            "Nhắc việc", "Họp nhóm đồ án trong tuần này.", "Hôm qua"
        )
    )

    CompositionLocalProvider(LocalTextStyle provides TextStyle(fontFamily = BeVietnamProFontFamily)) {
        Scaffold(
            containerColor = AppPalette.bg,
            bottomBar = {
                AppBottomBar(selected = "notifications", onNavigate = onNavigate, onAddClick = onAddClick)
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
                    Text("Thông báo", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AppPalette.text)
                    Text(
                        "Đánh dấu đã đọc",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppPalette.primary,
                        modifier = Modifier.clickable { onMarkAllRead() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppPalette.surface)
                ) {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (item.unread) {
                                        if (dark) Color(0xFF14192B) else Color(0xFFF5F8FF)
                                    } else Color.Transparent
                                )
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(item.bg, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(item.icon, contentDescription = item.title, tint = item.tint, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppPalette.text)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(item.body, fontSize = 13.sp, color = AppPalette.textSecondary, lineHeight = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.time, fontSize = 12.sp, color = AppPalette.textTertiary)
                                if (item.unread) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(AppPalette.primary, CircleShape)
                                    )
                                }
                            }
                        }
                        if (index < items.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                color = AppPalette.border
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
