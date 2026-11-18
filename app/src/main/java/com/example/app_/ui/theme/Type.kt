package com.example.app_.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.app_.R

// Họ font chuẩn Be Vietnam Pro từ file local trong res/font
val BeVietnamProFontFamily = FontFamily(
    Font(resId = R.font.be_vietnam_pro_regular, weight = FontWeight.Normal),
    Font(resId = R.font.be_vietnam_pro_medium, weight = FontWeight.Medium),
    Font(resId = R.font.be_vietnam_pro_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.be_vietnam_pro_bold, weight = FontWeight.Bold)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = BeVietnamProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = BeVietnamProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = BeVietnamProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    titleLarge = TextStyle(
        fontFamily = BeVietnamProFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = BeVietnamProFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = BeVietnamProFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)
