plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "mx.utng.cfga.fulbitoapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "mx.utng.cfga.fulbitoapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    
    // Íconos extendidos (EmojiEvents, Groups, ChevronRight, etc.)
    implementation("androidx.compose.material:material-icons-extended")
    
    // Coil para cargar imágenes asíncronas
    implementation("io.coil-kt:coil-compose:2.6.0")
    
    // Retrofit para conexión al backend
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    
    // Navigation
    implementation(libs.navigation.compose)
    
    // Lifecycle ViewModel Compose
    implementation(libs.lifecycle.viewmodel.compose)

    // Socket.IO para notificaciones en tiempo real
    implementation("io.socket:socket.io-client:2.1.0") {
        exclude(group = "org.json", module = "json")
    }
}
