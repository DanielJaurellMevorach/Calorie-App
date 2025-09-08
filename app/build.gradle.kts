plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.example.responsiveness"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.responsiveness"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "OPENAI_API_KEY", "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\"")
        buildConfigField("String", "PROMPT_TEMPLATE", "\"${project.findProperty("PROMPT_TEMPLATE") ?: ""}\"")

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
        viewBinding = true
        buildConfig = true
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
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    implementation("androidx.compose.ui:ui")
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Icons
    implementation("com.composables:icons-lucide:1.0.0")

    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Landscapist
    implementation("com.github.skydoves:landscapist-coil:2.4.0")

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Room for local database
    val room_version = "2.7.2"

    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:${room_version}")

    val camerax_version = "1.4.2"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")

    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    // GSON
    implementation("com.google.code.gson:gson:2.13.1")

    // OkHttp for API calls
    implementation("com.squareup.okhttp3:okhttp:5.1.0")

    // Viewmodel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
}