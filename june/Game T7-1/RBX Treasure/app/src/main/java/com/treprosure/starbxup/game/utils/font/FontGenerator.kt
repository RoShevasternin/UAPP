package com.treprosure.starbxup.game.utils.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.treprosure.starbxup.game.utils.disposeAll

class FontGenerator(fontPath: FontPath): FreeTypeFontGenerator(Gdx.files.internal(fontPath.path)) {

    private val fontCache = mutableMapOf<String, BitmapFont>()

    override fun generateFont(parameter: FreeTypeFontParameter): BitmapFont {
        // Захист від розміру < 4
        if (parameter.size < 4) parameter.size = 4

        // Захист від відсутності великої літери
        if (!parameter.characters.any { it in 'A'..'Z' }) {
            parameter.characters += "A"
        }

        val key = buildCacheKey(parameter)

        return fontCache.getOrPut(key) {
            // LibGDX дефолт = 512×512 — для великих шрифтів цього мало.
            // Гліф 264px + shadow 10px + border 3px ≈ 290px — не влізає в 512.
            val pageSize = when {
                parameter.size >= 200 -> 4096
                parameter.size >= 80  -> 2048
                else                  -> 1024
            }

            val packer = PixmapPacker(pageSize, pageSize, Pixmap.Format.RGBA8888, 2, false)
            parameter.packer = packer

            val font = super.generateFont(parameter)

            // Після генерації дані скопійовані в текстури — packer більше не потрібен
            packer.dispose()
            parameter.packer = null

            font
        }
    }

    private fun buildCacheKey(p: FreeTypeFontParameter): String {
        return "${p.size}_${p.borderWidth}_${p.borderColor}_${p.shadowOffsetX}_${p.shadowOffsetY}_${p.characters.length}"
    }

    override fun dispose() {
        super.dispose()
        fontCache.values.disposeAll()
        fontCache.clear()
    }

    companion object {
        enum class FontPath(val path: String) {
            AlanSans_Bold  ("font/AlanSans-Bold.ttf"),
            AlanSans_Medium("font/AlanSans-Medium.ttf"),

            Anton_Regular("font/Anton-Regular.ttf"),

        }
    }

}