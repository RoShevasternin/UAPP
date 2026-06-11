package com.rbxtreasure.fungamers.game.utils.advanced

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
import com.rbxtreasure.fungamers.MainActivity
import com.rbxtreasure.fungamers.adsmodule.AdSizeManager
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.utils.Block
import com.rbxtreasure.fungamers.game.utils.HEIGHT_UI
import com.rbxtreasure.fungamers.game.utils.ShapeDrawerUtil
import com.rbxtreasure.fungamers.game.utils.SizeScaler
import com.rbxtreasure.fungamers.game.utils.WIDTH_UI
import com.rbxtreasure.fungamers.game.utils.actor.addAndFillActor
import com.rbxtreasure.fungamers.game.utils.addProcessors
import com.rbxtreasure.fungamers.game.utils.disposeAll
import com.rbxtreasure.fungamers.game.utils.font.FontGenerator
import com.rbxtreasure.fungamers.game.utils.font.FontGenerator.Companion.FontPath
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.game.utils.global.GlobalStagePositions
import com.rbxtreasure.fungamers.game.utils.vfx.RenderPipeline
import com.rbxtreasure.fungamers.util.cancelCoroutinesAll
import com.rbxtreasure.fungamers.util.currentClassName
import com.rbxtreasure.fungamers.util.log
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

    val fontGenerator_AlanSans_Bold   = FontGenerator(FontPath.AlanSans_Bold)
    val fontGenerator_AlanSans_Medium = FontGenerator(FontPath.AlanSans_Medium)

    val fontGenerator_Anton_Regular = FontGenerator(FontPath.Anton_Regular)

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

            fontGenerator_AlanSans_Bold,
            fontGenerator_AlanSans_Medium,

            fontGenerator_Anton_Regular,
        )
        disposableSet.disposeAll()
        inputMultiplexer.clear()
        cancelCoroutinesAll(coroutine)
        coroutine = null

        GlobalStagePositions.clear()
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