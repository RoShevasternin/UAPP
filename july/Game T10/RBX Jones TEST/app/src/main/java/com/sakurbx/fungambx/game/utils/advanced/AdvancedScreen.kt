package com.sakurbx.fungambx.game.utils.advanced

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
import com.sakurbx.fungambx.MainActivity
import com.sakurbx.fungambx.adsmodule.AdSizeManager
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.HEIGHT_UI
import com.sakurbx.fungambx.game.utils.ShapeDrawerUtil
import com.sakurbx.fungambx.game.utils.SizeScaler
import com.sakurbx.fungambx.game.utils.WIDTH_UI
import com.sakurbx.fungambx.game.utils.actor.addAndFillActor
import com.sakurbx.fungambx.game.utils.addProcessors
import com.sakurbx.fungambx.game.utils.disposeAll
import com.sakurbx.fungambx.game.utils.font.FontGenerator
import com.sakurbx.fungambx.game.utils.font.FontGenerator.Companion.FontPath
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.global.GlobalStagePositions
import com.sakurbx.fungambx.game.utils.vfx.RenderPipeline
import com.sakurbx.fungambx.util.cancelCoroutinesAll
import com.sakurbx.fungambx.util.currentClassName
import com.sakurbx.fungambx.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class AdvancedScreen(
    val WIDTH : Float = WIDTH_UI,
    val HEIGHT: Float = HEIGHT_UI
) : ScreenAdapter(), IInputAdapter {

    val viewportUI by lazy { ExtendViewport(WIDTH, HEIGHT) }
    val stageUI    by lazy { AdvancedStage(viewportUI) }

    val safeStatusBarPX get() = MainActivity.statusBarHeight
    val safeNavBarPX    get() = MainActivity.navBarHeight

    val screenWidthPX  get() = Gdx.graphics.width
    val screenHeightPX get() = Gdx.graphics.height

    val worldWidth  get() = viewportUI.worldWidth
    val worldHeight get() = viewportUI.worldHeight

    private val scaleScreenToUiY: Float get() = (viewportUI.worldHeight / screenHeightPX)
    private fun Int.toUI() = this * scaleScreenToUiY

    val safeStatusBarUI get() = safeStatusBarPX.toUI()
    val safeNavBarUI    get() = safeNavBarPX.toUI()

    val adBannerUI get() = AdSizeManager.bannerHeightPx.toUI()
    val adBottomUI get() = AdSizeManager.adBottomHeightPx.toUI()

    val inputMultiplexer = InputMultiplexer()

    val backgroundImage = Image()

    val disposableSet = mutableSetOf<Disposable>()
    var coroutine: CoroutineScope? = CoroutineScope(Dispatchers.Default)
        private set

    val drawerUtil by lazy { ShapeDrawerUtil(stageUI.batch) }

    private val scalerVector = Vector2()
    val scalerUItoScreen     = SizeScaler(SizeScaler.Axis.X, WIDTH_UI)

    val fontGenerator_Laila_Bold           = FontGenerator(FontPath.Laila_Bold)
    val fontGenerator_Kedebideri_ExtraBold = FontGenerator(FontPath.Kedebideri_ExtraBold)

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

        stageUI.root.addAndFillActor(backgroundImage)

        rootConstraintLayout.setSize(worldWidth, worldHeight - safeStatusBarUI)
        stageUI.root.addActor(rootConstraintLayout)

        rootConstraintLayout.addActorsOnRootConstraintLayout()

        Gdx.input.inputProcessor = inputMultiplexer.apply { addProcessors(this@AdvancedScreen, stageUI) }
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
    }

    override fun render(delta: Float) {
        stageUI.render()
        drawerUtil.update()
    }

    override fun dispose() {
        log("dispose AdvancedScreen: $currentClassName")
        disposeAll(
            stageUI, drawerUtil,
            renderPipeline,

            fontGenerator_Laila_Bold,
            fontGenerator_Kedebideri_ExtraBold,
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

    open fun Group.addActorsOnStageUI() {}
    open fun AConstraintLayout.addActorsOnRootConstraintLayout() {}

    private fun updateSize() {
        stageUI.update(screenWidthPX, screenHeightPX, true)
        scalerUItoScreen.calculateScale(scalerVector.set(screenWidthPX.toFloat(), screenHeightPX.toFloat()))

        backgroundImage.setSize(worldWidth, worldHeight)
        rootConstraintLayout.setSize(worldWidth, worldHeight - safeStatusBarUI)
    }

    fun setBackground(region: TextureRegion) {
        backgroundImage.drawable = TextureRegionDrawable(region)
    }

    fun setBackground(texture: Texture) {
        backgroundImage.drawable = TextureRegionDrawable(texture)
    }

}