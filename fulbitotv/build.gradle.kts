import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.built.in1.kotlin)
    alias(libs.plugins.kotlin.compose)
}

extensions.configure<ApplicationExtension> {
    namespace = "mx.utng.srcp.fulbitotv"
    compileSdk = 36

    defaultConfig {
        applicationId = "mx.utng.srcp.fulbitotv"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.tv.foundation)
    implementation(libs.tv.material)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    debugImplementation(libs.ui.tooling)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    // Coil
    implementation(libs.coil.compose)
    // Socket.io
    implementation(libs.socket.io)
    // Navigation
    implementation(libs.navigation.compose)
    // ViewModel Compose
    implementation(libs.lifecycle.viewmodel.compose)
}
