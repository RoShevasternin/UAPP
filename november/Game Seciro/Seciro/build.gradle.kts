// AGP — у classpath кореня поруч із Kotlin-плагіном (як в Orbit Dash): вбудований Kotlin в AGP 9 посилається на класи AGP
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}
