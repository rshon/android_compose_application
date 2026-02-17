plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

val playerEngineType = project.findProperty("PLAYER_ENGINE_TYPE")?.toString() ?: "EXO_PLAYER"

android {
    namespace = "com.example.mediaapplication"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.mediaapplication"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "PLAYER_ENGINE_TYPE", "\"${playerEngineType}\"")
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

    // Room 어노테이션 정의 + 런타임
    implementation("androidx.room:room-runtime:2.7.0")
    // (선택) 코틀린 확장 함수
    implementation("androidx.room:room-ktx:2.7.0")
    // 스텁 생성 및 애노테이션 프로세싱
    kapt("androidx.room:room-compiler:2.7.0")

    // Hilt 도 동일 버전으로 맞춰주세요
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0") // 또는 최신 버전
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

//    implementation("com.google.dagger:hilt-android:2.48")
//    kapt("com.google.dagger:hilt-android-compiler:2.48")
//    implementation("androidx.hilt:hilt-navigation-compose:1.1.0") // Compose에서 ViewModel 사용 시 (선택 사항)

    kapt("androidx.room:room-compiler:2.7.0")

    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-core:1.6.7") // 버전 확인
    implementation("androidx.compose.material:material-icons-extended:1.6.7") // 버전 확인

    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

    implementation("com.google.accompanist:accompanist-permissions:0.30.1")

    // ExoPlayer 코어
    implementation("com.google.android.exoplayer:exoplayer-core:2.18.5")
    // ExoPlayer UI (PlayerView 등)
    implementation("com.google.android.exoplayer:exoplayer-ui:2.18.5")
    // (선택) 확장: HLS, DASH, RTSP 등
    implementation("com.google.android.exoplayer:exoplayer-hls:2.18.5")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.18.5")
}