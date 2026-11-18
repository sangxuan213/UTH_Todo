package com.example.app_.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun App_Theme(content: @Composable () -> Unit) {
    val system = isSystemInDarkTheme()
    SideEffect { AppPalette.systemDark = system }

    val dark = AppPalette.isDark
    val colorScheme = if (dark) {
        darkColorScheme(
            primary = AppPalette.primary,
            onPrimary = Color.White,
            background = AppPalette.bg,
            onBackground = AppPalette.text,
            surface = AppPalette.surface,
            onSurface = AppPalette.text,
            onSurfaceVariant = AppPalette.textSecondary,
            outline = AppPalette.border
        )
    } else {
        lightColorScheme(
            primary = AppPalette.primary,
            onPrimary = Color.White,
            background = AppPalette.bg,
            onBackground = AppPalette.text,
            surface = AppPalette.surface,
            onSurface = AppPalette.text,
            onSurfaceVariant = AppPalette.textSecondary,
            outline = AppPalette.border
        )
    }

    // Biểu tượng thanh trạng thái đổi theo sáng/tối
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !dark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
