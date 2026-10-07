buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // gdx-tools: безкоштовний TexturePacker від LibGDX (без водяних знаків) — таск packAtlas у app/
        classpath("com.badlogicgames.gdx:gdx-tools:1.14.2")
    }
}

plugins {
    id("com.android.application") version "9.3.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.10" apply false
}
