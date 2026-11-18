package com.example.app_.ui.screen

import com.example.app_.ui.theme.AppPalette

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_.model.OnboardingPage
import com.example.app_.R

@Composable
fun BaseOnboardingLayout(
    pageIndex: Int,
    pageData : OnboardingPage,
    onSkip:() -> Unit,
    onNext: () -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.surface)
            .padding(24.dp)
    ){
        // Hàng trên cùng: Logo, Tên app và nút Bỏ qua
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Giả lập Icon Logo (Hình vuông bo góc màu xanh)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(AppPalette.primary, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_1),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                
                Row {
                    Text(
                        text = "UTH",
                        fontWeight = FontWeight.ExtraBold, // weight 800
                        color = AppPalette.text,
                        fontSize = 19.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Todo",
                        fontWeight = FontWeight.Medium, // weight 500
                        color = AppPalette.primary,
                        fontSize = 19.sp
                    )
                }
            }
            TextButton(
                onClick = onSkip,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Bỏ qua", 
                    color = AppPalette.textSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Vùng hiển thị hình ảnh ở giữa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Khối nền bo tròn màu xanh nhạt
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(AppPalette.primary.copy(alpha = 0.1f), RoundedCornerShape(40.dp)), // Màu #2F5BEA với 10% alpha (1A)
                contentAlignment = Alignment.Center
            ) {
                // Hình tròn nền trắng có bóng (Shadow)
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .background(AppPalette.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Hình ảnh icon chính giữa (Lấy từ data class OOP)
                    Image(
                        painter = painterResource(id = pageData.imageRes),
                        contentDescription = pageData.title,
                        modifier = Modifier.size(52.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        Spacer(modifier=Modifier.height(40.dp))

        // Vùng Tiêu đề và Mô tả
        Text(
            text = pageData.title,
            fontSize = 28.sp, // Tiêu đề to hơn
            fontWeight = FontWeight.Bold,
            color = AppPalette.text, // Màu #1C1C1E
            lineHeight = 36.sp // Khoảng cách dòng cho tiêu đề nếu bị rớt dòng
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = pageData.description,
            fontSize = 16.sp, // Theo thiết kế là 16px
            color = AppPalette.textSecondary, // Màu #6B6B70
            lineHeight = 24.sp, // Khoảng cách dòng theo thiết kế
            fontWeight = FontWeight.Normal // Theo thiết kế weight 400
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Vùng 3 chấm (Indicator)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            repeat(3) { index ->
                val isSelected = index == pageIndex
                Box(
                    modifier = Modifier
                        // Nếu là trang hiện tại thì kéo dài hình chữ nhật ra 24dp, ngược lại là hình tròn 8dp
                        .width(if (isSelected) 24.dp else 8.dp)
                        .height(8.dp)
                        .background(
                            color = if (isSelected) AppPalette.primary else AppPalette.placeholder,
                            shape = CircleShape // Bo tròn hoàn toàn 2 đầu
                        )
                )
            }
        }

        //nút tiếp tục/bắt đầu ngay
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppPalette.primary),
            shape = RoundedCornerShape(12.dp)
        ){
            Text(
                text = if (pageIndex == 2) "Bắt đầu ngay" else "Tiếp tục",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )}
        }

}