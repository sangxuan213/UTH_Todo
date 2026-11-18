package com.example.app_.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * Bảng màu + cài đặt giao diện của app. Mọi màn hình đọc màu từ đây nên khi người dùng
 * đổi Sáng/Tối/Tự động hoặc màu chủ đạo ở màn Cài đặt, toàn bộ app đổi theo ngay.
 */
object AppPalette {
    // Màu cố định của nhóm công việc (không đổi theo màu chủ đạo, giống app mẫu)
    val categoryBlue = Color(0xFF2F5BEA)
    val categoryBlueBg = Color(0xFFE0E7FF)

    /** 4 màu chủ đạo: xanh dương, xanh lá, cam đất, tím */
    val accents = listOf(
        Color(0xFF2F5BEA),
        Color(0xFF0F8A5F),
        Color(0xFFC2410C),
        Color(0xFF7C3AED)
    )

    /** 0 = Sáng, 1 = Tối, 2 = Tự động (theo hệ thống) */
    private var _themeMode by mutableStateOf(0)
    val themeMode: Int get() = _themeMode
    var accentIndex by mutableStateOf(0)
        private set
    var systemDark by mutableStateOf(false)

    // Công tắc trong màn Cài đặt
    var remindOn by mutableStateOf(true)
        private set
    var soundOn by mutableStateOf(true)
        private set
    var dailySummaryOn by mutableStateOf(false)
        private set

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        val p = context.applicationContext.getSharedPreferences("uth_todo_settings", Context.MODE_PRIVATE)
        prefs = p
        _themeMode = p.getInt("theme", 0).coerceIn(0, 2)
        accentIndex = p.getInt("accent", 0).coerceIn(0, accents.lastIndex)
        remindOn = p.getBoolean("remind", true)
        soundOn = p.getBoolean("sound", true)
        dailySummaryOn = p.getBoolean("summary", false)
    }

    fun setThemeMode(mode: Int) {
        _themeMode = mode
        prefs?.edit()?.putInt("theme", mode)?.apply()
    }

    fun setAccent(index: Int) {
        accentIndex = index
        prefs?.edit()?.putInt("accent", index)?.apply()
    }

    fun setRemind(on: Boolean) {
        remindOn = on
        prefs?.edit()?.putBoolean("remind", on)?.apply()
    }

    fun setSound(on: Boolean) {
        soundOn = on
        prefs?.edit()?.putBoolean("sound", on)?.apply()
    }

    fun setDailySummary(on: Boolean) {
        dailySummaryOn = on
        prefs?.edit()?.putBoolean("summary", on)?.apply()
    }

    val isDark: Boolean
        get() = when (themeMode) {
            0 -> false
            1 -> true
            else -> systemDark
        }

    val primary: Color get() = accents[accentIndex]
    val primarySoft: Color
        get() = when {
            isDark -> lerp(Color(0xFF1C1C1E), primary, 0.28f)
            accentIndex == 0 -> Color(0xFFE6EDFF)
            else -> lerp(Color.White, primary, 0.14f)
        }

    val bg: Color get() = if (isDark) Color(0xFF000000) else Color(0xFFF5F5F7)
    val surface: Color get() = if (isDark) Color(0xFF1C1C1E) else Color.White
    val navBg: Color get() = if (isDark) Color(0xFF0B0B0C) else Color.White
    val text: Color get() = if (isDark) Color(0xFFFFFFFF) else Color(0xFF1C1C1E)
    val textSecondary: Color get() = if (isDark) Color(0xFFAEAEB2) else Color(0xFF6B6B70)
    val textTertiary: Color get() = if (isDark) Color(0xFF8E8E93) else Color(0xFF8E8E93)
    val border: Color get() = if (isDark) Color(0xFF38383A) else Color(0xFFE5E5EA)
    val fill: Color get() = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF3F4F6)
    val placeholder: Color get() = if (isDark) Color(0xFF48484A) else Color(0xFFC7C7CC)
    val onPrimary: Color get() = Color.White
}
