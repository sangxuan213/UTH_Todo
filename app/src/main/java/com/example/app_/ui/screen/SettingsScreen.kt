package com.example.app_.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_.ui.theme.AppPalette
import com.example.app_.ui.theme.BeVietnamProFontFamily
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val displayName = Firebase.auth.currentUser?.displayName ?: "bạn"
    val initial = displayName.firstOrNull()?.uppercase() ?: "K"

    CompositionLocalProvider(LocalTextStyle provides TextStyle(fontFamily = BeVietnamProFontFamily)) {
        Scaffold(
            containerColor = AppPalette.bg,
            bottomBar = {
                AppBottomBar(selected = "settings", onNavigate = onNavigate, onAddClick = onAddClick)
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
                    Text("Cài đặt", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AppPalette.text)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(AppPalette.primarySoft, CircleShape)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initial, color = AppPalette.primary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // ---- GIAO DIỆN ----
                SectionLabel("GIAO DIỆN")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppPalette.surface, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ThemeOption(Modifier.weight(1f), "Sáng", 0)
                        ThemeOption(Modifier.weight(1f), "Tối", 1)
                        ThemeOption(Modifier.weight(1f), "Tự động", 2)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = AppPalette.border)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Màu chủ đạo", fontSize = 15.sp, color = AppPalette.text, modifier = Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AppPalette.accents.forEachIndexed { index, color ->
                                val selected = AppPalette.accentIndex == index
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .then(
                                            if (selected) Modifier.border(2.dp, color, CircleShape) else Modifier
                                        )
                                        .padding(if (selected) 3.dp else 0.dp)
                                        .background(color, CircleShape)
                                        .clickable { AppPalette.setAccent(index) }
                                )
                            }
                        }
                    }
                }

                // ---- THÔNG BÁO ----
                SectionLabel("THÔNG BÁO")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppPalette.surface, RoundedCornerShape(16.dp))
                ) {
                    SwitchRow("Nhắc việc", "Báo trước hạn chót 15 phút", AppPalette.remindOn) { AppPalette.setRemind(it) }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = AppPalette.border)
                    SwitchRow("Âm thanh", "Phát âm khi có thông báo", AppPalette.soundOn) { AppPalette.setSound(it) }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = AppPalette.border)
                    SwitchRow("Tổng kết cuối ngày", "Gửi lúc 21:00 mỗi tối", AppPalette.dailySummaryOn) { AppPalette.setDailySummary(it) }
                }

                // ---- CHUNG ----
                SectionLabel("CHUNG")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppPalette.surface, RoundedCornerShape(16.dp))
                ) {
                    ValueRow("Ngôn ngữ", "Tiếng Việt")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = AppPalette.border)
                    ValueRow("Giới thiệu UTH Todo", "Phiên bản 1.0")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Spacer(modifier = Modifier.height(24.dp))
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppPalette.textTertiary)
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun ThemeOption(modifier: Modifier, label: String, mode: Int) {
    val selected = AppPalette.themeMode == mode
    val lightBg = Color(0xFFF5F5F7)
    val darkBg = Color(0xFF000000)

    Column(
        modifier = modifier.clickable { AppPalette.setThemeMode(mode) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .border(
                    if (selected) 1.5.dp else 0.dp,
                    if (selected) AppPalette.primary else Color.Transparent,
                    RoundedCornerShape(12.dp)
                )
                .padding(if (selected) 3.dp else 0.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            // Nền: sáng, tối, hoặc nửa sáng nửa tối
            Row(modifier = Modifier.fillMaxSize()) {
                if (mode == 2) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(lightBg))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(darkBg))
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(if (mode == 0) lightBg else darkBg))
                }
            }
            // Các thanh minh hoạ: 1 thanh ngắn (tiêu đề) + 2 thẻ
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(5.dp)
                        .background(
                            if (mode == 1) Color(0xFF636366) else Color(0xFFC7C7CC),
                            RoundedCornerShape(3.dp)
                        )
                )
                repeat(2) {
                    if (mode == 2) {
                        Row(modifier = Modifier.fillMaxWidth().height(14.dp)) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White, RoundedCornerShape(4.dp)))
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF2C2C2E), RoundedCornerShape(4.dp)))
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .background(
                                    if (mode == 0) Color.White else Color(0xFF2C2C2E),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) AppPalette.primary else AppPalette.textSecondary
        )
    }
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, color = AppPalette.text)
            Text(subtitle, fontSize = 12.sp, color = AppPalette.textTertiary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF34C759),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = AppPalette.border,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun ValueRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 15.sp, color = AppPalette.text, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, color = AppPalette.textSecondary)
    }
}
