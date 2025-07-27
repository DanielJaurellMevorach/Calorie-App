plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.example.caloriecounter"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.caloriecounter"
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // Keep this line
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation3.ui.android) // This will now get its version from the BOM
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Keep this line
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("br.com.devsrsouza.compose.icons:font-awesome:1.1.1")

    implementation("com.composables:icons-lucide:1.0.0")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

// Already added
    implementation("com.google.accompanist:accompanist-drawablepainter:0.34.0")
// May be needed for some blur implementations
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
// Not needed for blur
    implementation("com.google.accompanist:accompanist-flowlayout:0.34.0")
// Not needed for blur
    implementation("com.google.accompanist:accompanist-placeholder:0.34.0")
// Not needed for blur
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")
// Not needed for blur
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    //Camera

    val cameraVersion = "1.3.3"

    implementation("androidx.camera:camera-core:${cameraVersion}")
    implementation("androidx.camera:camera-camera2:${cameraVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraVersion}")
    implementation("androidx.camera:camera-video:${cameraVersion}")
    implementation("androidx.camera:camera-view:${cameraVersion}")
    implementation("androidx.camera:camera-extensions:${cameraVersion}")

    // Icons
    implementation("androidx.compose.material:material-icons-extended:0.1.0-dev1")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Responsive Layout
    implementation("androidx.compose.material3:material3-window-size-class")

    implementation("androidx.compose.ui:ui-graphics:1.6.0")

    // or latest stable
    implementation("androidx.compose.ui:ui-tooling:1.6.0")
    implementation("androidx.compose.foundation:foundation:1.6.0")

    //Coil Image Loading
    implementation("com.github.skydoves:landscapist-coil:2.2.6")
    implementation("com.github.skydoves:landscapist-placeholder:2.2.6")

    //OPENAI
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.activity:activity-compose:1.7.2")


}