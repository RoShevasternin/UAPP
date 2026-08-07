plugins {
    id("com.android.application")
    id("kotlinx-serialization")
    id("com.google.gms.google-services")
}

android {
    namespace  = "com.racing.funtols"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.racing.funtols"
        minSdk        = 24
        targetSdk     = 37
        versionCode   = 1
        versionName   = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    sourceSets {
        getByName("main") {
            jniLibs.directories.add("libs")
            res.directories += setOf("src/main/res", "src/main/res/launcher")
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging { jniLibs { useLegacyPackaging = true } }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

val natives: Configuration by configurations.creating

dependencies {
    // Test Core ------------------------------------------------------------------------
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    // AndroidX Core ------------------------------------------------------------------------
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.8")
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // LibGDX Core ------------------------------------------------------------------------
    val gdxVersion = "1.14.2"
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
    implementation("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86_64")

    // Other Core ------------------------------------------------------------------------
    implementation("space.earlygrey:shapedrawer:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    // Other ------------------------------------------------------------------------

    // LibGDX Video
    implementation("com.badlogicgames.gdx-video:gdx-video-android:1.3.4")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-messaging")

    // TikTok
    implementation("com.github.tiktok:tiktok-business-android-sdk:1.6.1")

    // Billing
    implementation("com.android.billingclient:billing-ktx:9.1.0")

    // Install Referrer (для визначення organic/paid юзера)
    implementation("com.android.installreferrer:installreferrer:2.2")

    // AdMob
    implementation("com.google.android.gms:play-services-ads:25.4.0")

    // Gson (парсинг JSON з Firebase)
    implementation("com.google.code.gson:gson:2.14.0")

    // Custom Tabs
    implementation("androidx.browser:browser:1.10.0")

    // Lifecycle (для AppOpen реклами)
    implementation("androidx.lifecycle:lifecycle-process:2.11.0")

    // Glide (завантаження картинок для кастомної реклами)
    implementation("com.github.bumptech.glide:glide:5.0.9")
}

tasks.register("copyAndroidNatives") {
    doFirst {
        natives.files.forEach { jar ->
            val outputDir = file("libs/" + jar.nameWithoutExtension.substringAfterLast("natives-"))
            outputDir.mkdirs()
            copy {
                from(zipTree(jar))
                into(outputDir)
                include("*.so")
            }
        }
    }
}
tasks.configureEach {
    if ("package" in name) {
        dependsOn("copyAndroidNatives")
    }
}