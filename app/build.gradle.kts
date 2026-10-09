plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.a2324802010348_vutruongthanh_gk"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.a2324802010348_vutruongthanh_gk"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Thêm Firebase BoM để quản lý các phiên bản thư viện Firebase đồng bộ
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))

    // Các thư viện Firebase phục vụ đồ án:
    implementation("com.google.firebase:firebase-auth")       // Đăng nhập / Đăng ký
    implementation("com.google.firebase:firebase-firestore")  // Cloud Database lưu món ăn
    implementation("com.google.firebase:firebase-storage")    // Upload hình ảnh avatar / món ăn
}