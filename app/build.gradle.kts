plugins {
    alias(libs.plugins.android.application) // Ensure you have the Android application plugin
    alias(libs.plugins.google.services)// Apply Google Services plugin
}

android {
    namespace = "com.dev.resourcehub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dev.resourcehub"
        minSdk = 24
        targetSdk = 34
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
    implementation(platform(libs.firebase.bom)) // Use BOM for Firebase
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth) // Corrected to use the correct library reference
    implementation(libs.glide)
    implementation(libs.play.services.auth) // Google Sign-In
    implementation(libs.firebase.storage)
    implementation(libs.recyclerview)
    implementation (libs.appcompat.v170) // or the latest version
    implementation(libs.viewpager2)
    implementation (libs.glide.v4151)
    annotationProcessor (libs.compiler)
    implementation(libs.appcompat)
    implementation (libs.squareup.picasso)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}