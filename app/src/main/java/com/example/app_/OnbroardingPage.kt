package com.example.app_.model

import android.media.Image
import androidx.annotation.DrawableRes
import com.example.app_.R

//1.data class định nghĩa cấu trúc của 1 trang onboarding
data class OnboardingPage(
    val title: String,
    val description : String,
    @DrawableRes val imageRes: Int
)

//2. tạo sẵn 1 danh sách 3 trang ( dữ liệu tính)
val onboardingPages = listOf(
    OnboardingPage(
        title = "Mọi việc, gọn trong một chỗ",
        "Ghi lại bài tập, việc nhà, lịch hẹn, không còn quên việc nào",
        imageRes = R.drawable.icon_2, //tạm thời dùng icon mặc định
    ),
    OnboardingPage(
        title ="Chia theo học tập và Cuộc sống",
        description = "Mỗi việc có nhóm riêng, nhìn là biết luôn hôm nay cần lo gì",
        imageRes = R.drawable.icon_21,
    ),
    OnboardingPage(
        title = "Nhắc đúng lúc",
        description = "Bật thông báo để UTH Todo nhắc bạn trước hạn chót",
        imageRes = R.drawable.icon_22
    ),
)
