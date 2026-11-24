// :core — уся апка без Android: екрани, логіка, стани. Тут нема android.* — тому вона ж іде й на десктоп
plugins {
    alias(libs.plugins.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(libs.versions.jvm.get()))
    }
}
dependencies {
    api(libs.gdx)
    api(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
