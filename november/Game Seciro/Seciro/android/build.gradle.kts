// :android — апка для Play. Kotlin вбудований в AGP 9 (окремий плагін не потрібен)
plugins {
    id("com.android.application")
}

android {
    namespace  = "com.seciro.app"
    compileSdk = 37

    defaultConfig {
        // ⚠️ applicationId — тимчасовий, до першого завантаження в Play його можна змінити, після — ніколи
        applicationId = "com.seciro.app"
        minSdk      = 29          // Android 10 — рішення 24.11.2026
        targetSdk   = 37
        versionCode = libs.versions.appVersionCode.get().toInt()
        versionName = libs.versions.appVersion.get()
    }
    buildTypes {
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
    }
    sourceSets {
        getByName("main") {
            assets.directories.add("../assets")
            jniLibs.directories.add("libs")
        }
    }
    packaging { jniLibs { useLegacyPackaging = false } }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(libs.versions.jvm.get()))
    }
}

val natives: Configuration = configurations.create("natives") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":core"))
    implementation(libs.gdx.backend.android)
    listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64").forEach { abi ->
        natives(variantOf(libs.gdx.platform) { classifier("natives-$abi") })
    }
}

// .so libGDX з natives-jar у libs/<abi> — перед кожним пакуванням (як в Orbit Dash)
tasks.register("copyAndroidNatives") {
    doFirst {
        natives.files.forEach { jar ->
            val outputDir = file("libs/" + jar.nameWithoutExtension.substringAfterLast("natives-"))
            outputDir.mkdirs()
            copy { from(zipTree(jar)); into(outputDir); include("*.so") }
        }
    }
}
tasks.configureEach { if ("package" in name) dependsOn("copyAndroidNatives") }
