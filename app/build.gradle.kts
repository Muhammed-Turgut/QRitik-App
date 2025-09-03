plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)  // Uncomment this line

    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.RealizeStudio.qritik"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.RealizeStudio.qritik"
        minSdk = 27
        targetSdk = 35
        versionCode = 2
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"  // Add this block
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
    implementation(libs.play.services.ads)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Navigasyon kütüphanesi
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation ("app.rive:rive-android:4.1.0")
    implementation ("androidx.startup:startup-runtime:1.1.1")



    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    // CameraX
    implementation ("androidx.camera:camera-core:1.1.0")
    implementation ("androidx.camera:camera-camera2:1.1.0")
    implementation ("androidx.camera:camera-lifecycle:1.1.0")
    implementation ("androidx.camera:camera-view:1.1.0")
    implementation ("androidx.concurrent:concurrent-futures:1.1.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.6.2")

    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    //Barkoda dönüştürmek için
    implementation ("com.google.zxing:core:3.5.3")

    // Bu da gerekli olabilir
    implementation("androidx.camera:camera-core:1.3.0")

    // Admob için


    // Lifecycle (ViewModel için)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    val roomVersion = "2.6.1"

    // Room Data Base için kullanılan kütüphaneler.
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("com.google.code.gson:gson:2.12.1")

    //SpalsScreen özelleştire bilmek için
    implementation ("androidx.core:core-splashscreen:1.0.1")

    //Geçiş animasyonları için
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.32.0")

    //hillt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

     //Constrait layout
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.1.1")





}