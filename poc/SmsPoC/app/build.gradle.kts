plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.smspoc"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.smspoc"
        // SMS_RECEIVED works since API 1; ROLE_SMS since 29. Below 29 we fall
        // back to Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT.
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.browser:browser:1.10.0")
}
