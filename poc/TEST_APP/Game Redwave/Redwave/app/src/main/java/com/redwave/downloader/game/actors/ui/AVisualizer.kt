package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AVisualizer — v1 (ShapeDrawer): 48 стовпчиків зі спектра (Media3 Tee →
// SpectrumAnalyzer), градієнт vizTop → red (55 %) → redDeep до середньої лінії,
// віддзеркалення 38 % висоти з альфою 0.18. На паузі смуги плавно спадають.
// ─────────────────────────────────────────────────────────────────────────────
class AVisualizer(screen: AdvancedScreen) : AShape(screen) {

    private val top = Color(); private val bot = Color(); private val refl = Color()

    override fun drawShape(alpha: Float) {
        val bands = gdxGame.bridge.spectrum.bands(gdxGame.player.isPlaying)
        val n = bands.size
        val mid = height * 0.74f            // як у прототипі: віддзеркалення під лінією
        val step = width / n
        val bw = (step - px(2.4f)).coerceAtLeast(1f)
        bot.set(GameColor.redDeep_B8001F).also { it.a *= alpha }
        refl.set(GameColor.red_FF2E4D).also { it.a = 0.18f * alpha }
        val baseY = y + height - mid
        for (i in 0 until n) {
            val env = Math.pow((1.0 - i.toDouble() / n), 0.7).toFloat() * 0.85f + 0.15f
            val h = (bands[i] * env * mid).coerceAtLeast(px(3f))
            val bx = x + i * step + px(1.2f)
            // колір верху = точка градієнта на висоті h від лінії
            val t = 1f - h / mid
            gradientAt(t, top); top.a *= alpha
            drawer.filledRectangle(bx, baseY, bw, h, bot, bot, top, top)
            drawer.setColor(refl)
            drawer.filledRectangle(bx, baseY - px(2f) - h * 0.38f, bw, h * 0.38f)
        }
    }

    private fun gradientAt(t: Float, out: Color) {
        if (t < 0.55f) out.set(GameColor.vizTop_FFB199).lerp(GameColor.red_FF2E4D, t / 0.55f)
        else out.set(GameColor.red_FF2E4D).lerp(GameColor.redDeep_B8001F, (t - 0.55f) / 0.45f)
    }
}
