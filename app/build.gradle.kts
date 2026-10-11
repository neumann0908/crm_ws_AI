plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace 'com.crm.chatflow'
    compileSdk 35

    defaultConfig {
        applicationId "com.crm.chatflow"
        minSdk 24
        targetSdk 35
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = '11'
    }
    buildFeatures {
        compose true
    }
}

dependencies {

    // Core Android & Lifecycle
    implementation libs.androidx.core.ktx
    implementation libs.androidx.lifecycle.runtime.ktx
    implementation libs.androidx.activity.compose

    // Jetpack Compose UI
    implementation platform(libs.androidx.compose.bom)
    implementation libs.androidx.ui
    implementation libs.androidx.ui.graphics
    implementation libs.androidx.ui.tooling.preview
    implementation libs.androidx.material3
    implementation 'androidx.compose.material:material-icons-extended'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7'

    // Room Database + KSP
    implementation "androidx.room:room-runtime:2.6.1"
    implementation "androidx.room:room-ktx:2.6.1"
    ksp "androidx.room:room-compiler:2.6.1"

    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0'

    // MediaPipe (IA Local)
    implementation 'com.google.mediapipe:tasks-genai:0.10.32'

    // Testing
    testImplementation libs.junit
    androidTestImplementation libs.androidx.junit
    androidTestImplementation libs.androidx.espresso.core
    androidTestImplementation platform(libs.androidx.compose.bom)
    androidTestImplementation libs.androidx.ui.test.junit4
    debugImplementation libs.androidx.ui.tooling
    debugImplementation libs.androidx.ui.test.manifest
}
