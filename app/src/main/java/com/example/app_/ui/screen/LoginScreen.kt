package com.example.app_.ui.screen

import com.example.app_.ui.theme.AppPalette

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.app_.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

// Hàm tiện ích để lấy Activity từ Context trong Compose
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onGuestLogin: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val auth = Firebase.auth

    // Hàm xử lý đăng nhập Google
    fun signInWithGoogle() {
        isLoading = true
        coroutineScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                // Sử dụng trực tiếp Web Client ID (Client Type 3) từ file google-services.json
                val webClientId = "1058376248383-597nu0p12raml81dumv8gfd5vmn7cqdo.apps.googleusercontent.com"

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val activity = context.findActivity()
                if (activity == null) {
                    isLoading = false
                    return@launch
                }

                // Bật popup chọn tài khoản Google của Android lên
                val result = credentialManager.getCredential(
                    request = request,
                    context = activity
                )

                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    // Lấy Token nhét vào Firebase
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                    auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        } else {
                            Toast.makeText(context, "Lỗi Firebase: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    isLoading = false
                    Toast.makeText(context, "Lỗi: Token không hợp lệ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                isLoading = false
                Log.e("LoginScreen", "Lỗi đăng nhập: ${e.message}")
                // Báo lỗi nếu người dùng tắt popup hoặc chưa có mạng
                Toast.makeText(context, "Đăng nhập thất bại hoặc đã huỷ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hàm xử lý đăng nhập Ẩn danh (Dùng thử)
    fun signInAnonymously() {
        isLoading = true
        auth.signInAnonymously().addOnCompleteListener { task ->
            isLoading = false
            if (task.isSuccessful) {
                Toast.makeText(context, "Bắt đầu dùng thử!", Toast.LENGTH_SHORT).show()
                onGuestLogin()
            } else {
                Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.surface)
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Image(
            painter = painterResource(id = R.drawable.icon_2),
            contentDescription = "Logo",
            modifier = Modifier
                .size(96.dp)
                .background(Color.Transparent, RoundedCornerShape(24.dp))
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Chào mừng đến với\nUTH Todo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AppPalette.text,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
            fontSize = 16.sp,
            color = AppPalette.textSecondary,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Hiển thị vòng xoay nếu đang load
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppPalette.primary)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Nút Đăng nhập Google
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, AppPalette.border, RoundedCornerShape(12.dp))
                .clickable(enabled = !isLoading) { signInWithGoogle() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(1.dp, AppPalette.border, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("G", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Tiếp tục với Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppPalette.text
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = AppPalette.border)
            Text(
                text = "hoặc",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 14.sp,
                color = AppPalette.textTertiary
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = AppPalette.border)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Dùng thử không cần tài khoản",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isLoading) { signInAnonymously() },
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppPalette.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",
            fontSize = 12.sp,
            color = AppPalette.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {}, onGuestLogin = {})
}