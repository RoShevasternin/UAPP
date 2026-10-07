// ═════════════════════════════════════════════════════════════════════════════
//  port-kit — чиста Kotlin-логіка Redwave без Android і без LibGDX-рантайму.
//
//  Навіщо окремий JVM-проєкт: ці файли переносяться в апку як є, а тут їх можна
//  прогнати тестами без девайса й без Android SDK:
//      gradle test        (або ./gradlew test, якщо додати wrapper)
//
//  У самій апці цього build-файла немає — копіюються лише src/main/kotlin/**.
// ═════════════════════════════════════════════════════════════════════════════
plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
}

repositories {
    mavenCentral()
    gradlePluginPortal() // дзеркало Maven Central — рятує, коли Central віддає 429
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Лише щоб скомпілювати GameColor/Dimens (com.badlogic.gdx.graphics.Color).
    compileOnly("com.badlogicgames.gdx:gdx:1.14.2")

    testImplementation(kotlin("test-junit"))
    testImplementation("com.badlogicgames.gdx:gdx:1.14.2")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.test {
    testLogging { events("passed", "failed"); showStandardStreams = false }
}
