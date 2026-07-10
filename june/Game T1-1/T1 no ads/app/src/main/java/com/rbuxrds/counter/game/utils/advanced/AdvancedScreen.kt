package com.rbuxrds.counter.game.utils.advanced

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
import com.rbuxrds.counter.MainActivity
import com.rbuxrds.counter.game.utils.Block
import com.rbuxrds.counter.game.utils.HEIGHT_UI
import com.rbuxrds.counter.game.utils.ShapeDrawerUtil
import com.rbuxrds.counter.game.utils.SizeScaler
import com.rbuxrds.counter.game.utils.WIDTH_UI
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.addProcessors
import com.rbuxrds.counter.game.utils.disposeAll
import com.rbuxrds.counter.game.utils.font.FontGenerator
import com.rbuxrds.counter.game.utils.font.FontGenerator.Companion.FontPath
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.game.utils.vfx.RenderPipeline
import com.rbuxrds.counter.util.cancelCoroutinesAll
import com.rbuxrds.counter.util.currentClassName
import com.rbuxrds.counter.util.log
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

    val safeTop    get() = MainActivity.statusBarHeight
    val safeBottom get() = MainActivity.navBarHeight

    val scaleScreenToUiY: Float
        get() = viewportUI.worldHeight / (Gdx.graphics.height - (safeTop + safeBottom)).toFloat()

    val safeTopUI    get() = safeTop    * scaleScreenToUiY
    val safeBottomUI get() = safeBottom * scaleScreenToUiY

    val inputMultiplexer    = InputMultiplexer()

    val backBackgroundImage = Image()
    val uiBackgroundImage   = Image()

    val disposableSet = mutableSetOf<Disposable>()
    var coroutine: CoroutineScope? = CoroutineScope(Dispatchers.Default)
        private set

    val drawerUtil by lazy { ShapeDrawerUtil(stageUI.batch) }

    private val scalerVector = Vector2()
    val scalerUItoScreen     = SizeScaler(SizeScaler.Axis.X, WIDTH_UI)

    val fontGenerator_InterTight_Bold     = FontGenerator(FontPath.InterTight_Bold)
    val fontGenerator_InterTight_Medium   = FontGenerator(FontPath.InterTight_Medium)
    val fontGenerator_InterTight_SemiBold = FontGenerator(FontPath.InterTight_SemiBold)

    // ─── RenderPipeline ───────────────────────────────────────────────────────
    // Shared VfxPool для всіх VfxGroup на цьому екрані.
    // VfxGroup звертається до нього через screen.renderPipeline.vfxPool.
    // Один екземпляр на екран — створюється разом з екраном, dispose в dispose().
    val renderPipeline = RenderPipeline()

    override fun show() {
        log("show AdvancedScreen: $currentClassName")
        val screenWidth  = Gdx.graphics.width
        val screenHeight = Gdx.graphics.height - (safeTop + safeBottom)

        scalerUItoScreen.calculateScale(scalerVector.set(screenWidth.toFloat(), screenHeight.toFloat()))

        stageBack.update(screenWidth, screenHeight, true)
        stageUI.update(screenWidth, screenHeight, true)

        // Зміщуємо viewport вниз щоб рендер починався від safeBottom
        stageBack.viewport.screenY = safeBottom
        stageUI.viewport.screenY   = safeBottom

        stageBack.root.addAndFillActor(backBackgroundImage)
        stageUI.root.addAndFillActor(uiBackgroundImage)

        stageBack.root.addActorsOnStageBack()
        stageUI.root.addActorsOnStageUI()

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

            fontGenerator_InterTight_Bold,
            fontGenerator_InterTight_Medium,
            fontGenerator_InterTight_SemiBold,
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