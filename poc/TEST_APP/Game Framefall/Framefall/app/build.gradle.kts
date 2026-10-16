plugins {
    id("com.android.application")
    id("kotlinx-serialization")
}

// FCM: google-services.json лежить у app/ лише коли є Firebase-проєкт апки. Без нього збірка й апка
// працюють (локальні пуші, конфіг, реклама), лише без серверних пушів (Biz.startSession пропускає токен).
if (file("google-services.json").exists()) apply(plugin = "com.google.gms.google-services")

android {
    namespace  = "com.vidtools.framefall"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.vidtools.framefall"
        minSdk        = 24
        targetSdk     = 37
        versionCode   = 2
        versionName   = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled   = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled   = true
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

val natives: Configuration = configurations.create("natives") {
    isCanBeConsumed = false   // цю конфігурацію не публікуємо назовні
    isCanBeResolved = true    // але самі резолвимо, щоб дістати .so з jar-ів
}

dependencies {
    // Test Core ------------------------------------------------------------------------
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.4.20")

    // AndroidX Core ------------------------------------------------------------------------
    implementation("androidx.core:core-ktx:1.19.1")
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // LibGDX Core ------------------------------------------------------------------------
    val gdxVersion = "1.14.2"
    // gdx явно: POM backend-android тягне gdx 1.9.10 (у T35 це маскував gdx-freetype 1.14.2)
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")

    // Other Core ------------------------------------------------------------------------
    implementation("space.earlygrey:shapedrawer:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")

    // Відео: ExoPlayer (плеєр і відеофон Home → OES-текстура), Transformer + effect (Studio) —
    // одна версія Media3 на всі артефакти
    val media3 = "1.11.1"
    implementation("androidx.media3:media3-exoplayer:$media3")
    implementation("androidx.media3:media3-transformer:$media3")
    implementation("androidx.media3:media3-effect:$media3")
    implementation("androidx.media3:media3-common:$media3")

    // Custom Tabs: лендінги через гейтвей (BrowserUtil) — «Додому», «Недавні», app_open, front/back
    implementation("androidx.browser:browser:1.10.0")

    // probe + fetchText (перевірка лінка, API Internet Archive / NASA)
    implementation("com.squareup.okhttp3:okhttp:5.5.0")

    // Firebase: лише FCM (businesModule: токен пушів, PushService). Remote Config прибрано
    // 12.10.2026 — режим і реклама з картки нашого сервера. Без Analytics.
    implementation(platform("com.google.firebase:firebase-bom:35.0.0"))
    implementation("com.google.firebase:firebase-messaging")

    // businesModule + adsmodule (як в UAPP): Gson для конфігу, сирий Install Referrer для атрибуції
    implementation("com.google.code.gson:gson:2.14.0")
    implementation("com.android.installreferrer:installreferrer:2.2")

    // TikTok Business SDK (як в еталоні): app_id / secret — блок `tiktok` у картці сервера
    implementation("com.github.tiktok:tiktok-business-android-sdk:1.7.1")

    // AdMob: App Open + Interstitial (adsmodule, провайдер з картки сервера)
    implementation("com.google.android.gms:play-services-ads:25.5.0")
    // play-services-ads тягне WorkManager 2.7.0 + Room 2.2.5 (2021): їхні правила R8 писані до повного режиму,
    // і в release R8 вирізає конструктор WorkDatabase_Impl → краш на старті (заміряно 08.10.2026).
    // Свіжий WorkManager тягне Room 2.8 з правильними consumer-правилами.
    // Ще й LocalPush (businesModule) планує локальні пуші через WorkManager.
    implementation("androidx.work:work-runtime:2.12.0")
}

tasks.register("copyAndroidNatives") {
    description = "Розпаковує .so з natives-jar у libs/<abi>"
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

// ------------------------------------------------------------------------
// Атлас іконок `all` — gdx-tools TexturePacker (рішення VELDAN 07.10.2026).
// Джерело — ../../assets/all/icons (білі PNG 96 px), результат — assets/atlas/all.*.
// Запуск вручну після зміни іконок:  sh ./gradlew :app:packAtlas
// Мипмапи: іконки 96 px малюються ~40 px — без мипмап лінійний фільтр дає «сходи».
// ------------------------------------------------------------------------
tasks.register("packAtlas") {
    description = "Пакує ../assets/all/icons у app/src/main/assets/atlas/all.atlas"
    doLast {
        System.setProperty("java.awt.headless", "true")
        val settings = com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings().apply {
            maxWidth       = 1024
            maxHeight      = 1024
            paddingX       = 4
            paddingY       = 4
            edgePadding    = true
            duplicatePadding = true
            filterMin      = com.badlogic.gdx.graphics.Texture.TextureFilter.MipMapLinearLinear
            filterMag      = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
            combineSubdirectories = true
            useIndexes     = false
            stripWhitespaceX = false
            stripWhitespaceY = false
        }
        com.badlogic.gdx.tools.texturepacker.TexturePacker.process(
            settings,
            file("../../assets/all/icons").absolutePath,
            file("src/main/assets/atlas").absolutePath,
            "all",
        )
    }
}
