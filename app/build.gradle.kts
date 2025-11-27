plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // KSP 插件已就位
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.emojigallery"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.emojigallery"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // === 原有的基础依赖 (保持不变) ===
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ============================================
    // === 以下是作业新增的依赖 (复制这里！) ===
    // ============================================

    // 1. Coil: 图片加载 (满足技术要求: 网络 + 文件存储)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 2. Room: 数据库 (满足技术要求: DB存储)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // 3. KSP: 数据库编译器 (关键！必须用 ksp)
    ksp("androidx.room:room-compiler:$roomVersion")

    // 4. ViewModel: 数据管理
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
}
