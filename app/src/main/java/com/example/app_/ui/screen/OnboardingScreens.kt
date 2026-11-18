package com.example.app_.ui.screen

import androidx.compose.runtime.Composable
import com.example.app_.model.onboardingPages

@Composable
fun OnboardingScreen1(onSkip: () -> Unit, onNext: () -> Unit) {
    BaseOnboardingLayout(
        pageIndex = 0,
        pageData = onboardingPages[0],
        onSkip = onSkip,
        onNext = onNext
    )
}

@Composable
fun OnboardingScreen2(onSkip: () -> Unit, onNext: () -> Unit) {
    BaseOnboardingLayout(
        pageIndex = 1,
        pageData = onboardingPages[1],
        onSkip = onSkip,
        onNext = onNext
    )
}

@Composable
fun OnboardingScreen3(onSkip: () -> Unit, onStart: () -> Unit) {
    BaseOnboardingLayout(
        pageIndex = 2,
        pageData = onboardingPages[2],
        onSkip = onSkip,
        onNext = onStart // Ở trang 3, nút này đóng vai trò là "Bắt đầu"
    )
}