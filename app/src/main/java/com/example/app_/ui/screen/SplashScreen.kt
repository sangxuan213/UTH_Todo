package com.example.app_.ui.screen

import com.example.app_.ui.theme.AppPalette

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToOnboarding: () -> Unit) {
    // Chuyển sang màn hình Onboarding sau 2 giây (2000 milliseconds)
    LaunchedEffect(key1 = true) {
        delay(2000)
        onNavigateToOnboarding()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.primary), // Nền màu xanh đậm giống thiết kế
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hình ảnh Logo 
            Image(
                painter = painterResource(id = R.drawable.icon_2), // Thay tên icon của bạn vào đây
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(96.dp) // Kích thước logo dự đoán từ ảnh (to hơn logo header)
                    .background(AppPalette.surface, shape = RoundedCornerShape(24.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            // Tên app ghép 2 màu chữ
            Row {
                Text(
                    text = "UTH",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold // weight 800
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Todo",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium // weight 500
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Slogan
            Text(
                text = "Việc học, việc nhà - gọn gàng mỗi ngày",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )
        }

        // Dòng chữ phiên bản dưới cùng
        Text(
            text = "Phiên bản 1.0",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onNavigateToOnboarding = {})
}
