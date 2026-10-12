package com.driftglass.home.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.driftglass.home.game.platform.AdStatus
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.driftglass.home.game.screens.base.DgScreen
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// SplashScreen — лого-арка + «Driftglass», поки вантажаться атлас, звуки й AppState.
// Не менше 1 с. Далі: онбординг (перший запуск), Home (роль наша і прийшли «Додому») або застосунок.
// Лого й світіння — власні текстури (атлас ще в дорозі).
// ─────────────────────────────────────────────────────────────────────────────
class SplashScreen : DgScreen() {

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
        gdxGame.backgroundColor = GameColor.splash_04070A
        super.show()
        startedAt = System.currentTimeMillis()
        gdxGame.spriteManager.loadAll()
        gdxGame.soundManager.load()
        gdxGame.model.load { modelLoaded = true }
        // Перший запуск онлайн: чекаємо Firebase до 3 с (як Redwave), далі — кеш одразу
        if (gdxGame.bridge.hasRemoteFlags() || !gdxGame.bridge.isOnline()) flagsReady = true
        else gdxGame.bridge.refreshRemoteFlags(3_000L) { flagsReady = true }
    }

    override fun buildContent() {
        val w = worldWidth; val h = worldHeight
        content.addActor(Image(texGlow).apply {
            color.set(GameColor.teal_5FF2D1); color.a = 0.22f
            setSize(w * 1.6f, w * 1.6f); setPosition((w - width) / 2f, h * 0.55f - height / 2f)
        })
        val center = Group().apply { isTransform = true }
        content.addActor(center)
        val logoSize = px(96f)
        val logo = Image(texLogo).apply { setSize(logoSize, logoSize); setOrigin(Align.center) }
        val word = lbl("Driftglass", msdf.disp(30f))
        val gap = px(16f)
        val total = logoSize + gap + word.height
        logo.setPosition(-logoSize / 2f, total - logoSize)
        word.setPosition(-word.width / 2f, 0f)
        center.addActor(logo); center.addActor(word)
        center.setPosition(w / 2f, h / 2f - total / 2f)
        center.setOrigin(0f, total / 2f)
        center.color.a = 0f; center.setScale(0.9f)
        center.addAction(Actions.parallel(Actions.fadeIn(0.8f, Interpolation.pow3Out), Actions.scaleTo(1f, 1f, 0.8f, Interpolation.pow3Out)))
        logo.addAction(Actions.forever(Actions.sequence(Actions.scaleTo(1.04f, 1.04f, 1f, Interpolation.sine), Actions.scaleTo(1f, 1f, 1f, Interpolation.sine))))
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (!assetsDone && gdxGame.assetManager.update()) {
            gdxGame.spriteManager.initAll()
            gdxGame.soundManager.init()
            assetsDone = true
        }
        val elapsed = System.currentTimeMillis() - startedAt
        if (!navigated && assetsDone && modelLoaded && flagsReady && elapsed >= 1000L) {
            // App Open при відкритті: лише якщо далі застосунок (не онбординг, не лаунчер) і онлайн.
            // Ще вантажиться — чекаємо до AD_WAIT_MS від старту Splash, далі йдемо без реклами.
            val ad = if (gdxGame.firstScreenIsApp() && gdxGame.bridge.isOnline()) gdxGame.bridge.appOpenStatus() else AdStatus.NONE
            if (ad == AdStatus.LOADING && elapsed < AD_WAIT_MS) return
            navigated = true
            gdxGame.onReady()
            val go = {
                animHideScreen {
                    gdxGame.backgroundColor = GameColor.background
                    gdxGame.navigateFirst()
                }
            }
            if (ad == AdStatus.READY) gdxGame.bridge.showAppOpen(onReturn = false, awayMs = 0L) { go() } else go()
        }
    }

    private companion object {
        /** Скільки Splash максимум чекає App Open (від свого старту). */
        const val AD_WAIT_MS = 3_500L
    }

    override fun onBackPressed() {}
}
