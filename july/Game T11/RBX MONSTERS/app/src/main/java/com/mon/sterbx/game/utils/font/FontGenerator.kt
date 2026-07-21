package com.mon.sterbx.game.utils.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.mon.sterbx.game.utils.disposeAll

class FontGenerator(fontPath: FontPath) : FreeTypeFontGenerator(Gdx.files.internal(fontPath.path)) {

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
            // Атлас керується дефолтним механізмом FreeType. GPU сучасних і навіть
            // старих пристроїв (перевірено: Redmi 9T має GL_MAX_TEXTURE_SIZE=16384)
            // легко тягне потрібні розміри, тож штучно обмежувати НЕ треба —
            // інакше великі шрифти (з border+shadow) не влазять у сторінку і кидають
            // "Page size too small for pixmap".
            super.generateFont(parameter)
        }
    }

    private fun buildCacheKey(p: FreeTypeFontParameter): String =
        "${p.size}_${p.borderWidth}_${p.borderColor}_${p.shadowOffsetX}_${p.shadowOffsetY}_${p.characters.length}"

    override fun dispose() {
        super.dispose()
        fontCache.values.disposeAll()
        fontCache.clear()
    }

    companion object {
        enum class FontPath(val path: String) {
            BeVietnamPro_Bold        ("font/BeVietnamPro-Bold.ttf"),
            BeVietnamPro_BlackItalic ("font/BeVietnamPro-BlackItalic.ttf"),
            BeVietnamPro_Black       ("font/BeVietnamPro-Black.ttf"),
            BeVietnamPro_Regular     ("font/BeVietnamPro-Regular.ttf"),
            BeVietnamPro_MediumItalic("font/BeVietnamPro-MediumItalic.ttf"),

            BricolageGrotesque_ExtraBold("font/BricolageGrotesque-ExtraBold.ttf"),
        }
    }

}