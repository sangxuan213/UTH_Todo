import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services") // Khai báo sử dụng Firebase Plugin
}

// Token API mock đọc từ local.properties (không commit lên git):  MOCK_API_TOKEN=xxxx
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

android {
    namespace = "com.example.app_"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.app_"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "MOCK_API_TOKEN", "\"${localProps.getProperty("MOCK_API_TOKEN", "")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.5")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.core.ktx)
    
    // Khai báo Firebase BoM (Bill of Materials) để quản lý chung phiên bản
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    
    // Firebase Authentication (Đăng nhập)
    implementation("com.google.firebase:firebase-auth")
    // Thư viện để lấy R.string cho Web Client ID
    implementation("com.google.android.gms:play-services-base:18.5.0")
    // Thư viện để làm Đăng nhập Google (Google Sign-in)
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    // Thông tin đăng nhập mới (Credential Manager) - Đề xuất bởi Google
    implementation("androidx.credentials:credentials:1.5.0-alpha05")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0-alpha05")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("androidx.navigation:navigation-compose:2.8.4")
    // Gọi API (hỗ trợ PATCH)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}