// :desktop — апка у вікні Мака 375×812 (кадр Figma): перевірка екранів без телефона. Запуск: sh ./gradlew :desktop:run
plugins {
    alias(libs.plugins.kotlin.jvm)
    application
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
    implementation(project(":core"))
    implementation(libs.gdx.backend.lwjgl3)
    implementation(variantOf(libs.gdx.platform) { classifier("natives-desktop") })
}
application {
    mainClass.set("com.seciro.app.desktop.DesktopLauncherKt")
}
tasks.named<JavaExec>("run") {
    val runDir = file("run").also { it.mkdirs() }
    workingDir = runDir
    if (System.getProperty("os.name").lowercase().contains("mac")) jvmArgs("-XstartOnFirstThread")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    systemProperty("seciro.assets", rootProject.file("assets").absolutePath)
    // -Pseciro.shot=<файл.png> — зробити знімок першого кадру й вийти (перевірка без рук)
    System.getProperties().filter { (k, _) -> k.toString().startsWith("seciro.") }
        .forEach { (k, v) -> systemProperty(k.toString(), v) }
    providers.gradleProperty("seciro.shot").orNull?.let { systemProperty("seciro.shot", it) }
}
