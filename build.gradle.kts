// Top-level build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false // Correctly reference the Google Services plugin
}

repositories {

}