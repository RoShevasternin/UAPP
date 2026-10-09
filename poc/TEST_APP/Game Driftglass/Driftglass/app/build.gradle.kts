plugins {
    id("com.android.application")
    id("kotlinx-serialization")
}

android {
    namespace  = "com.driftglass.home"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.driftglass.home"
        minSdk        = 24
        targetSdk     = 37
        versionCode   = 1
        versionName   = "1.0.0"

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
    implementation("androidx.browser:browser:1.10.0")   // AD_MODE: Custom Tab на «Додому»

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
// Джерело — ../../assets/icons (білі PNG 96 px), результат — assets/atlas/all.*.
// Запуск вручну після зміни іконок:  sh ./gradlew :app:packAtlas
// Мипмапи: іконки 96 px малюються ~40 px — без мипмап лінійний фільтр дає «сходи».
// ------------------------------------------------------------------------
tasks.register("packAtlas") {
    description = "Пакує ../assets/icons у app/src/main/assets/atlas/all.atlas"
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
            file("../../assets/icons").absolutePath,
            file("src/main/assets/atlas").absolutePath,
            "all",
        )
    }
}
