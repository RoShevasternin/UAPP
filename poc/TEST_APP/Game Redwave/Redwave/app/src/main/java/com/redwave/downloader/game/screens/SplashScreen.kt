package com.redwave.downloader.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.game.actors.ui.AShape
import com.redwave.downloader.game.screens.base.RedwaveScreen
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// SplashScreen — лого + смужки, поки вантажаться атласи, звуки й AppState.
// Не менше 1.2 с. Далі: Onboarding (перший запуск) або AppScreen / Launcher.
// Перший запуск онлайн (прапорців Remote Config ще немає в кеші) — чекаємо Firebase до 3 с;
// не відповів або офлайн → RemoteFlags.DEFAULT. Далі прапорці з кешу одразу, оновлення — у фоні.
// Лого й світіння — власні текстури (атласи ще в дорозі).
// ─────────────────────────────────────────────────────────────────────────────
class SplashScreen : RedwaveScreen() {

    private val texLogo = Texture(Gdx.files.internal("textures/logo.png"), true).apply {
        setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear)
    }
    private val texGlow = Texture(Gdx.files.internal("textures/fx/glow_radial.png")).apply {
        setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
    }

    private var startedAt   = 0L
    private var assetsDone  = false
    private var modelLoaded = false
    private var navigated   = false
    private var flagsReady  = false

    init {
        disposableSet += texLogo
        disposableSet += texGlow
    }

    override fun show() {
        gdxGame.backgroundColor = GameColor.splash_070405
        super.show()
        startedAt = System.currentTimeMillis()
        gdxGame.spriteManager.loadAll()
        gdxGame.soundManager.load()
        gdxGame.model.load { modelLoaded = true }
        // Офлайн чекати нема чого: прапорці все одно DEFAULT
        if (gdxGame.bridge.hasRemoteFlags() || !gdxGame.bridge.isOnline()) flagsReady = true
        else gdxGame.bridge.refreshRemoteFlags(3_000L) { flagsReady = true }
    }

    override fun buildContent() {
        val w = worldWidth; val h = worldHeight

        // radial-gradient(60% 38% at 50% 44%, rgba(255,46,77,.38), transparent 70%)
        content.addActor(Image(texGlow).apply {
            color.set(GameColor.red_FF2E4D); color.a = 0.38f
            setSize(w * 1.2f / 0.7f * 0.6f, h * 0.76f / 0.7f * 0.6f)
            setPosition((w - width) / 2f, h * 0.56f - height / 2f)
        })

        val center = Group().apply { isTransform = true }
        content.addActor(center)

        val logoSize = px(96f)
        val glowLogo = Image(texGlow).apply {
            color.set(GameColor.red_FF2E4D); color.a = 0.7f
            setSize(logoSize * 2.4f, logoSize * 2.4f)
        }
        val logo  = Image(texLogo).apply { setSize(logoSize, logoSize); setOrigin(Align.center) }
        val word  = lbl(Copy.APP_NAME, msdf.disp(36f))
        val sub   = lbl(Copy.APP_SUBTITLE.uppercase(), msdf.monoSemi(11f, GameColor.muted_A8949B, spacing = 34f))

        val gap = px(14f)
        val total = logoSize + gap + word.height + gap + sub.height
        var y = total
        y -= logoSize; logo.setPosition(-logoSize / 2f, y)
        glowLogo.setPosition(-glowLogo.width / 2f, y + logoSize / 2f - glowLogo.height / 2f - px(18f))
        y -= gap + word.height; word.setPosition(-word.width / 2f, y)
        y -= gap + sub.height; sub.setPosition(-sub.width / 2f, y)
        center.addActor(glowLogo); center.addActor(logo); center.addActor(word); center.addActor(sub)
        center.setPosition(w / 2f, h / 2f - total / 2f)
        center.setOrigin(0f, total / 2f)

        // spIn: scale .86 → 1 + alpha за 0.9 с
        center.color.a = 0f
        center.setScale(0.86f)
        center.addAction(Actions.parallel(Actions.fadeIn(0.9f, Interpolation.pow3Out), Actions.scaleTo(1f, 1f, 0.9f, Interpolation.pow3Out)))
        // spPulse: лого дихає 1.8 с
        logo.addAction(Actions.forever(Actions.sequence(
            Actions.scaleTo(1.05f, 1.05f, 0.9f, Interpolation.sine), Actions.scaleTo(1f, 1f, 0.9f, Interpolation.sine))))

        // Смужки: 7 × 4 px, 4…28, gap 5, 64 від низу
        content.addActor(SplashBars().apply {
            setSize(px(7 * 4f + 6 * 5f), px(28f))
            setPosition((w - width) / 2f, safeNavBarUI + px(64f))
        })
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (!assetsDone && gdxGame.assetManager.update()) {
            gdxGame.spriteManager.initAll()
            gdxGame.soundManager.init()
            assetsDone = true
        }
        if (!navigated && assetsDone && modelLoaded && flagsReady && System.currentTimeMillis() - startedAt >= 1200L) {
            navigated = true
            gdxGame.onReady()
            animHideScreen {
                gdxGame.backgroundColor = GameColor.background
                gdxGame.navigateFirst()
            }
        }
    }

    override fun onBackPressed() {}

    /** .sp-bars: 7 стовпчиків, ping-pong 0.9 с із затримкою 110 мс на кожен. */
    private inner class SplashBars : AShape(this@SplashScreen) {
        private var t = 0f
        override fun act(delta: Float) { super.act(delta); t += delta }
        override fun drawShape(alpha: Float) {
            col(GameColor.red_FF2E4D, alpha)
            val bw = px(4f); val gap = px(5f)
            for (i in 0 until 7) {
                val tt = t - i * 0.11f
                val cycle = tt / 0.9f
                val p = cycle - MathUtils.floor(cycle)
                val tri = if ((MathUtils.floor(cycle) and 1) == 0) p else 1f - p
                val k = 0.5f - 0.5f * MathUtils.cos(tri * MathUtils.PI)
                val bh = px(4f) + (px(28f) - px(4f)) * (if (tt < 0) 0f else k)
                drawer.filledRectangle(x + i * (bw + gap), y, bw, bh)
            }
        }
    }
}
