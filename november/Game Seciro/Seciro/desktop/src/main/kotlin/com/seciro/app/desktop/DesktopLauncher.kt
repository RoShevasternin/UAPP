package com.seciro.app.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.seciro.app.SeciroApp

// Вікно Мака розміром кадру Figma (375×812) — перевірка екранів до телефона
fun main() {
    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("Seciro")
        setWindowedMode(375, 812)
        setBackBufferConfig(8, 8, 8, 8, 16, 0, 4)
    }
    Lwjgl3Application(SeciroApp(), config)
}
