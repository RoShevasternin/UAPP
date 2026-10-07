package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.redwave.downloader.game.utils.advanced.AdvancedScreen

// ─────────────────────────────────────────────────────────────────────────────
// AShape — актор, що малює себе ShapeDrawer-ом (кільце прогресу, спінер,
// стовпчики). Батч той самий, що в сцені, тож порядок малювання природний.
// ─────────────────────────────────────────────────────────────────────────────
abstract class AShape(val screen: AdvancedScreen) : Actor() {

    protected val drawer get() = screen.drawerUtil.drawer
    private val tmp = Color()

    init { touchable = Touchable.disabled }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val old = drawer.packedColor
        drawShape(parentAlpha * color.a)
        drawer.setColor(old)
    }

    abstract fun drawShape(alpha: Float)

    protected fun col(c: Color, alpha: Float) { drawer.setColor(tmp.set(c).also { it.a *= alpha }) }
}

/** .dlb.run: кільце прогресу 38, товщина 3, старт зверху, за годинниковою. */
class AProgressRing(screen: AdvancedScreen, private val thickness: Float, private val back: Color, private val front: Color) : AShape(screen) {
    var progress = 0f
    private var shown = 0f

    override fun act(delta: Float) {
        super.act(delta)
        shown = MathUtils.lerp(shown, progress, (delta * 10f).coerceAtMost(1f))
    }

    override fun drawShape(alpha: Float) {
        val r = width / 2f - thickness / 2f
        val cx = x + width / 2f; val cy = y + height / 2f
        col(back, alpha)
        drawer.circle(cx, cy, r, thickness)
        if (shown > 0.005f) {
            col(front, alpha)
            val sweep = MathUtils.PI2 * shown
            drawer.arc(cx, cy, r, MathUtils.PI / 2f - sweep, sweep, thickness)
        }
    }
}

/** .spin: кільце 16, рамка 2 white 20 %, верхня чверть red, оберт 0.7 с. */
class ASpinner(screen: AdvancedScreen, private val thickness: Float, private val back: Color, private val front: Color) : AShape(screen) {
    private var angle = 0f
    override fun act(delta: Float) { super.act(delta); angle -= delta * MathUtils.PI2 / 0.7f }
    override fun drawShape(alpha: Float) {
        val r = width / 2f - thickness / 2f
        val cx = x + width / 2f; val cy = y + height / 2f
        col(back, alpha); drawer.circle(cx, cy, r, thickness)
        col(front, alpha); drawer.arc(cx, cy, r, angle, MathUtils.PI / 2f, thickness)
    }
}

/**
 * .eqb: індикатор «грає» — 4 стовпчики 3 px, висота 20…100 %, ping-pong 0.9 с,
 * зсуви фаз 0 / −.3 / −.6 / −.15 с. На паузі — завмирають на 30 %.
 */
class AEqBars(screen: AdvancedScreen, private val barW: Float, private val gap: Float, private val barColor: Color, private val count: Int = 4) : AShape(screen) {
    var playing = true
    private var t = 0f
    private val phases = floatArrayOf(0f, -0.3f, -0.6f, -0.15f, -0.45f, -0.75f, -0.05f)

    override fun act(delta: Float) { super.act(delta); if (playing) t += delta }

    override fun drawShape(alpha: Float) {
        col(barColor, alpha)
        val total = count * barW + (count - 1) * gap
        var bx = x + (width - total) / 2f
        for (i in 0 until count) {
            val k = if (playing) {
                val p = ((t - phases[i % phases.size]) / 0.9f).let { it - MathUtils.floor(it) }   // 0..1
                val tri = if ((((t - phases[i % phases.size]) / 0.9f).toInt() and 1) == 0) p else 1f - p
                0.2f + 0.8f * (0.5f - 0.5f * MathUtils.cos(tri * MathUtils.PI))
            } else 0.3f
            val h = height * k
            drawer.filledRectangle(bx, y, barW, h)
            bx += barW + gap
        }
    }
}
