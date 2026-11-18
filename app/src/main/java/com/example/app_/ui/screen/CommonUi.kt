package com.example.app_.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_.ui.theme.AppPalette
import kotlinx.coroutines.delay

/** Thanh tab dưới dùng chung với khuyết tròn cho FAB: Trang chủ | Danh sách | (+) | Thông báo | Cài đặt */
@Composable
fun AppBottomBar(
    selected: String,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val density = LocalDensity.current
    val cutoutRadius = 34.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        val barShape = remember {
            GenericShape { size, _ ->
                val width = size.width
                val height = size.height
                val centerX = width / 2f
                val r = with(density) { cutoutRadius.toPx() }
                val corner = with(density) { 24.dp.toPx() }

                moveTo(0f, corner)
                quadraticTo(0f, 0f, corner, 0f)
                lineTo(centerX - r - 12f, 0f)

                arcTo(
                    rect = Rect(
                        left = centerX - r,
                        top = -r,
                        right = centerX + r,
                        bottom = r
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )

                lineTo(width - corner, 0f)
                quadraticTo(width, 0f, width, corner)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(elevation = 16.dp, shape = barShape),
            color = AppPalette.surface,
            shape = barShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TabItemBar("home", "Trang chủ", Icons.Filled.Home, Icons.Outlined.Home, selected, onNavigate)
                TabItemBar("task_list", "Danh sách", Icons.Filled.List, Icons.Outlined.List, selected, onNavigate)
                Spacer(modifier = Modifier.width(56.dp))
                TabItemBar("notifications", "Thông báo", Icons.Filled.Notifications, Icons.Outlined.Notifications, selected, onNavigate)
                TabItemBar("settings", "Cài đặt", Icons.Filled.Tune, Icons.Outlined.Tune, selected, onNavigate)
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = AppPalette.primary,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-4).dp)
                .size(56.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun RowScope.TabItemBar(
    route: String,
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    val isSelected = route == selectedRoute
    val color = if (isSelected) AppPalette.primary else AppPalette.textSecondary
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable { if (!isSelected) onNavigate(route) }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            label,
            fontSize = 11.sp,
            color = color,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
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
