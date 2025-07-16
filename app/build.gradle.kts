plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // KAPT plugin’ini doğru ID ile ekliyoruz
    id("org.jetbrains.kotlin.kapt")
    // Room için KSP
    id("com.google.devtools.ksp")
    // Room Gradle plugin (schemaDirectory ayarları için)
    id("androidx.room") version "2.7.1"
    // Hilt Gradle plugin
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.kami_apps.deepwork"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kami_apps.deepwork"
        minSdk = 25
        targetSdk = 35
        versionCode = 91
        versionName = "0.9.1 (Test)"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    room {
        schemaDirectory("$projectDir/schemas")
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
        isCoreLibraryDesugaringEnabled = true
    }
    lint {
        abortOnError = false
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        renderScript = true
        compose = true

    }
}

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM + UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material)

    // Compose Animations, LiveData vs.
    implementation("androidx.compose.animation:animation:1.8.2")
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui.viewbinding)
    implementation(libs.androidx.emoji2.emojipicker)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences.core.android)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.appcompat)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Room
    val roomVersion = "2.7.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Hilt & Hilt Navigation
    implementation("com.google.dagger:hilt-android:2.56.2")
    kapt("com.google.dagger:hilt-android-compiler:2.56.2")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.0")

    // WorkManager + Hilt integration
    val workVersion = "2.10.1"
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("androidx.hilt:hilt-work:1.2.0")

    // Material icons
    implementation("androidx.compose.material:material-icons-extended:1.7.8")


    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation ("androidx.savedstate:savedstate:1.3.0")

    //Haze
    implementation("dev.chrisbanes.haze:haze:1.6.4")
    implementation("dev.chrisbanes.haze:haze-materials:1.6.4")

    //Chart Libs
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m2)
    implementation(libs.vico.compose.m3)
    //Timline Lib for old version
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")



    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}