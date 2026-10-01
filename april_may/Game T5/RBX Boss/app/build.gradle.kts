import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlinx-serialization")
    id("com.google.gms.google-services")
}

// Тестові ключі Meta — лише для debug. Лежать у local.properties (у git не йде):
//   meta.testAppId=…
//   meta.testClientToken=…
// Потрібні, щоб перевірити Meta до того, як сервер почне віддавати блок "meta".
val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}
fun localProp(key: String): String = localProps.getProperty(key, "").trim()

android {
    namespace = "com.bossrbx.rbxcalculator"
    compileSdk = 37


    defaultConfig {
        applicationId = "com.bossrbx.rbxcalculator"
        minSdk = 24
        targetSdk = 37
        versionCode = 4
        versionName = "4.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "META_TEST_APP_ID",       "\"${localProp("meta.testAppId")}\"")
            buildConfigField("String", "META_TEST_CLIENT_TOKEN", "\"${localProp("meta.testClientToken")}\"")
            isMinifyEnabled   = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            // release тестових ключів не бачить ніколи — лише сервер
            buildConfigField("String", "META_TEST_APP_ID",       "\"\"")
            buildConfigField("String", "META_TEST_CLIENT_TOKEN", "\"\"")
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
    // Test ------------------------------------------------------------------------
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    // AndroidX ------------------------------------------------------------------------
    implementation("androidx.core:core-ktx:1.19.1")
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.10.2")
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // LibGDX ------------------------------------------------------------------------
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

    // Other ------------------------------------------------------------------------
    implementation("space.earlygrey:shapedrawer:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    // Business Logic ------------------------------------------------------------------------

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-config")
    // правка 6.3: FCM-токен збираємо з першого релізу (розсилки — етап 2, сервер)
    implementation("com.google.firebase:firebase-messaging")

    // TikTok
    implementation("com.github.tiktok:tiktok-business-android-sdk:1.7.1")

    // Meta (Facebook) — лише App Events: встановлення, запуски й події для реклами Meta
    implementation("com.facebook.android:facebook-core:18.3.0")

    // Billing
    implementation("com.android.billingclient:billing-ktx:9.1.0")

    // Install Referrer (для визначення organic/paid юзера)
    implementation("com.android.installreferrer:installreferrer:2.2")

    // AdMob
    implementation("com.google.android.gms:play-services-ads:25.5.0")

    // Gson (парсинг JSON з Firebase)
    implementation("com.google.code.gson:gson:2.14.0")

    // Custom Tabs
    implementation("androidx.browser:browser:1.10.0")

    // Lifecycle (для AppOpen реклами)
    implementation("androidx.lifecycle:lifecycle-process:2.11.0")

    // Glide (завантаження картинок для кастомної реклами)
    implementation("com.github.bumptech.glide:glide:5.0.9")

    // правка 6.2: локальні пуші — WorkManager планує показ за правилами
    // з конфігу (businesModule/push/LocalPush.kt).
    implementation("androidx.work:work-runtime-ktx:2.12.0")
}

tasks.register("copyAndroidNatives") {
    description = ""
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