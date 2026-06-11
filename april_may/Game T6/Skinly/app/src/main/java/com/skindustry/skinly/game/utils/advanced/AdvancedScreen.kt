package com.skindustry.skinly.game.utils.advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.skindustry.skinly.MainActivity
import com.skindustry.skinly.adsmodule.AdSizeManager
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.Block
import com.skindustry.skinly.game.utils.HEIGHT_UI
import com.skindustry.skinly.game.utils.ShapeDrawerUtil
import com.skindustry.skinly.game.utils.SizeScaler
import com.skindustry.skinly.game.utils.WIDTH_UI
import com.skindustry.skinly.game.utils.actor.addAndFillActor
import com.skindustry.skinly.game.utils.addProcessors
import com.skindustry.skinly.game.utils.disposeAll
import com.skindustry.skinly.game.utils.font.FontGenerator
import com.skindustry.skinly.game.utils.font.FontGenerator.Companion.FontPath
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.vfx.RenderPipeline
import com.skindustry.skinly.util.cancelCoroutinesAll
import com.skindustry.skinly.util.currentClassName
import com.skindustry.skinly.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class AdvancedScreen(
    val WIDTH : Float = WIDTH_UI,
    val HEIGHT: Float = HEIGHT_UI
) : ScreenAdapter(), IInputAdapter {

    val viewportBack by lazy { ScreenViewport() }
    val stageBack    by lazy { AdvancedStage(viewportBack) }

    val viewportUI by lazy { ExtendViewport(WIDTH, HEIGHT) }
    val stageUI    by lazy { AdvancedStage(viewportUI) }

    val safeStatusBarPX get() = MainActivity.statusBarHeight
    val safeNavBarPX    get() = MainActivity.navBarHeight

    val screenWidthPX  get() = Gdx.graphics.width
    val screenHeightPX get() = Gdx.graphics.height - safeStatusBarPX

    private val scaleScreenToUiY: Float get() = (viewportUI.worldHeight / screenHeightPX)
    private fun Int.toUI() = this * scaleScreenToUiY

    val safeStatusBarUI get() = safeStatusBarPX.toUI()
    val safeNavBarUI    get() = safeNavBarPX.toUI()

    val adBannerUI get() = AdSizeManager.bannerHeightPx.toUI()
    val adBottomUI get() = AdSizeManager.adBottomHeightPx.toUI()

    val inputMultiplexer    = InputMultiplexer()

    val backBackgroundImage = Image()
    val uiBackgroundImage   = Image()

    val disposableSet = mutableSetOf<Disposable>()
    var coroutine: CoroutineScope? = CoroutineScope(Dispatchers.Default)
        private set

    val drawerUtil by lazy { ShapeDrawerUtil(stageUI.batch) }

    private val scalerVector = Vector2()
    val scalerUItoScreen     = SizeScaler(SizeScaler.Axis.X, WIDTH_UI)

    val fontGenerator_Black     = FontGenerator(FontPath.Black)
    val fontGenerator_Bold      = FontGenerator(FontPath.Bold)
    val fontGenerator_SemiBold  = FontGenerator(FontPath.SemiBold)
    val fontGenerator_Light     = FontGenerator(FontPath.Light)
    val fontGenerator_Medium    = FontGenerator(FontPath.Medium)
    val fontGenerator_Regular   = FontGenerator(FontPath.Regular)

    // ─── RenderPipeline ───────────────────────────────────────────────────────
    // Shared VfxPool для всіх VfxGroup на цьому екрані.
    // VfxGroup звертається до нього через screen.renderPipeline.vfxPool.
    // Один екземпляр на екран — створюється разом з екраном, dispose в dispose().
    val renderPipeline = RenderPipeline()

    val rootConstraintLayout = AConstraintLayout(this)

    override fun resize(width: Int, height: Int) {
        updateSize()
    }

    override fun show() {
        log("show AdvancedScreen: $currentClassName")
        updateSize()

        stageBack.root.addAndFillActor(backBackgroundImage)
        stageUI.root.addAndFillActor(uiBackgroundImage)

        stageUI.root.addAndFillActor(rootConstraintLayout)

        stageBack.root.addActorsOnStageBack()
        stageUI.root.addActorsOnStageUI()
        rootConstraintLayout.addActorsOnRootConstraintLayout()

        Gdx.input.inputProcessor = inputMultiplexer.apply { addProcessors(this@AdvancedScreen, stageUI, stageBack) }
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
    }

    override fun render(delta: Float) {
        stageBack.render()
        stageUI.render()
        drawerUtil.update()
    }

    override fun dispose() {
        log("dispose AdvancedScreen: $currentClassName")
        disposeAll(
            stageBack, stageUI, drawerUtil,
            renderPipeline,

            fontGenerator_Black,
            fontGenerator_Bold,
            fontGenerator_SemiBold,
            fontGenerator_Light,
            fontGenerator_Medium,
            fontGenerator_Regular,
        )
        disposableSet.disposeAll()
        inputMultiplexer.clear()
        cancelCoroutinesAll(coroutine)
        coroutine = null
    }

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.BACK -> {
                if (gdxGame.navigationManager.isBackStackEmpty()) gdxGame.navigationManager.exit()
                else animHideScreen { gdxGame.navigationManager.back() }
            }
        }
        return true
    }

    abstract fun animShowScreen(blockEnd: Block = {})
    abstract fun animHideScreen(blockEnd: Block = {})

    open fun Group.addActorsOnStageBack() {}
    open fun Group.addActorsOnStageUI() {}
    open fun AConstraintLayout.addActorsOnRootConstraintLayout() {}

    private fun updateSize() {
        stageBack.update(Gdx.graphics.width, Gdx.graphics.height, true)
        stageUI.update(screenWidthPX, screenHeightPX, true)
        scalerUItoScreen.calculateScale(scalerVector.set(screenWidthPX.toFloat(), screenHeightPX.toFloat()))
        rootConstraintLayout.setSize(viewportUI.worldWidth, viewportUI.worldHeight)
    }

    fun setBackBackground(region: TextureRegion) {
        backBackgroundImage.drawable = TextureRegionDrawable(region)
    }

    fun setBackBackground(texture: Texture) {
        backBackgroundImage.drawable = TextureRegionDrawable(texture)
    }

    fun setUIBackground(region: TextureRegion) {
        uiBackgroundImage.drawable = TextureRegionDrawable(region)
    }

    fun setUIBackground(texture: Texture) {
        uiBackgroundImage.drawable = TextureRegionDrawable(texture)
    }

    fun setBackgrounds(backRegion: TextureRegion, uiRegion: TextureRegion = backRegion) {
        setBackBackground(backRegion)
        setUIBackground(uiRegion)
    }

    fun setBackgrounds(backTexture: Texture, uiTexture: Texture = backTexture) {
        setBackBackground(backTexture)
        setUIBackground(uiTexture)
    }

}