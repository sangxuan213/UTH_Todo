package com.example.app_.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_.ui.theme.AppPalette
import kotlinx.coroutines.delay

/** Thanh tab dưới dùng chung: Trang chủ | Danh sách | (+) | Thông báo | Cài đặt */
@Composable
fun AppBottomBar(
    selected: String,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        NavigationBar(
            containerColor = AppPalette.navBg,
            contentColor = AppPalette.textSecondary,
            tonalElevation = 8.dp
        ) {
            TabItem("home", "Trang chủ", Icons.Filled.Home, Icons.Outlined.Home, selected, onNavigate)
            TabItem("task_list", "Danh sách", Icons.Filled.List, Icons.Outlined.List, selected, onNavigate)
            NavigationBarItem(icon = {}, label = {}, selected = false, onClick = {}, enabled = false)
            TabItem("notifications", "Thông báo", Icons.Filled.Notifications, Icons.Outlined.Notifications, selected, onNavigate)
            TabItem("settings", "Cài đặt", Icons.Filled.Settings, Icons.Outlined.Settings, selected, onNavigate)
        }

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = AppPalette.primary,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .offset(y = (-20).dp)
                .size(56.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun RowScope.TabItem(
    route: String,
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    val isSelected = route == selectedRoute
    NavigationBarItem(
        icon = { Icon(if (isSelected) selectedIcon else unselectedIcon, contentDescription = label) },
        label = {
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
        },
        selected = isSelected,
        onClick = { if (!isSelected) onNavigate(route) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppPalette.primary,
            selectedTextColor = AppPalette.primary,
            unselectedIconColor = AppPalette.textSecondary,
            unselectedTextColor = AppPalette.textSecondary,
            indicatorColor = AppPalette.navBg
        )
    )
}

/** Thông báo nổi ngắn (toast) ở đầu màn hình, tự tắt sau ~2 giây. */
@Composable
fun ToastHost(message: String?, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(message) {
        if (message != null) {
            delay(2000)
            onDismiss()
        }
    }
    if (message != null) {
        Box(
            modifier = modifier
                .padding(top = 12.dp)
                .background(Color(0xFF1C1C1E), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(message, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
