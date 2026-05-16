package com.rbuxdrop.cougame.game.utils.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.utils.Disposable
import com.rbuxdrop.cougame.game.utils.disposeAll

class FontGenerator(fontPath: FontPath): FreeTypeFontGenerator(Gdx.files.internal(fontPath.path)) {

    private val fontCache = mutableMapOf<String, BitmapFont>()

    override fun generateFont(parameter: FreeTypeFontParameter): BitmapFont {
        // захист від розміру < 4
        if (parameter.size < 4) parameter.size = 4

        // захист від відсутності великої літери
        if (!parameter.characters.any { it in 'A'..'Z' }) {
            parameter.characters += "A"
        }

        val key  = buildCacheKey(parameter)  // ← після модифікації
        val font = fontCache.getOrPut(key) { super.generateFont(parameter) }

        return font
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
            Bold   ("font/Inter_28pt-Bold.ttf"),
            Light  ("font/Inter_28pt-Light.ttf"),
            Medium ("font/Inter_28pt-Medium.ttf"),
        }
    }

}