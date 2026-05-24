package com.rbuxrds.counterds.game.utils.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.utils.Disposable
import com.rbuxrds.counterds.game.utils.disposeAll
import com.rbuxrds.counterds.util.log

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
            InterTight_Bold    ("font/InterTight-Bold.ttf"),
            InterTight_Medium  ("font/InterTight-Medium.ttf"),
            InterTight_SemiBold("font/InterTight-SemiBold.ttf"),
        }
    }

}