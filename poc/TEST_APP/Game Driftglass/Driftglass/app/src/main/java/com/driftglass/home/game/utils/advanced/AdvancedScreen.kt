package com.driftglass.home.game.utils.advanced

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
import com.driftglass.home.game.actors.layout.constraintLayout.AConstraintLayout
import com.driftglass.home.game.utils.Block
import com.driftglass.home.game.utils.HEIGHT_UI
import com.driftglass.home.game.utils.ShapeDrawerUtil
import com.driftglass.home.game.utils.SizeScaler
import com.driftglass.home.game.utils.WIDTH_UI
import com.driftglass.home.game.utils.actor.addAndFillActor
import com.driftglass.home.game.utils.addProcessors
import com.driftglass.home.game.utils.disposeAll
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.global.GlobalStagePositions
import com.driftglass.home.game.utils.runGDX
import com.driftglass.home.game.utils.vfx.RenderPipeline
import com.driftglass.home.util.cancelCoroutinesAll
import com.driftglass.home.util.currentClassName
import com.driftglass.home.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class AdvancedScreen(
    val WIDTH : Float = WIDTH_UI,
    val HEIGHT: Float = HEIGHT_UI
) : ScreenAdapter(), IInputAdapter {

    val viewportUI by lazy { ExtendViewport(WIDTH, HEIGHT) }
    val stageUI    by lazy { AdvancedStage(viewportUI) }

    val safeStatusBarPX get() = gdxGame.bridge.statusBarPx
    val safeNavBarPX    get() = gdxGame.bridge.navBarPx

    val screenWidthPX  get() = Gdx.graphics.width
    val screenHeightPX get() = Gdx.graphics.height

    val worldWidth  get() = viewportUI.worldWidth
    val worldHeight get() = viewportUI.worldHeight

    private val scaleScreenToUiY: Float get() = (viewportUI.worldHeight / screenHeightPX)
    private fun Int.toUI() = this * scaleScreenToUiY

    val safeStatusBarUI get() = safeStatusBarPX.toUI()
    val safeNavBarUI    get() = safeNavBarPX.toUI()

    val inputMultiplexer = InputMultiplexer()

    val backgroundImage = Image()

    val disposableSet = mutableSetOf<Disposable>()
    var coroutine: CoroutineScope? = CoroutineScope(Dispatchers.Default)
        private set

    val drawerUtil by lazy { ShapeDrawerUtil(stageUI.batch) }

    private val scalerVector = Vector2()
    val scalerUItoScreen     = SizeScaler(SizeScaler.Axis.X, WIDTH_UI)

    // ─── RenderPipeline ───────────────────────────────────────────────────────
    // Shared VfxPool для всіх VfxGroup на цьому екрані.
    // VfxGroup звертається до нього через screen.renderPipeline.vfxPool.
    // Один екземпляр на екран — створюється разом з екраном, dispose в dispose().
    val renderPipeline = RenderPipeline()

    val rootConstraintLayout = AConstraintLayout(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun resize(width: Int, height: Int) {
        updateSize()
    }

    override fun show() {
        log("show AdvancedScreen: $currentClassName")
        updateSize()

        stageUI.root.addAndFillActor(backgroundImage)
        stageUI.root.addActorsOnStageUI()

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
        )
        disposableSet.disposeAll()
        inputMultiplexer.clear()
        cancelCoroutinesAll(coroutine)
        coroutine = null

        GlobalStagePositions.clear()
    }

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.BACK -> onBackPressed()
        }
        return true
    }

    /**
     * «Назад». Дефолт — як у T35, але порожній бекстек = exit() = moveTaskToBack:
     * Redwave може бути головним екраном, а лаунчер не закривається.
     * LauncherScreen перевизначає це на «нічого не робити».
     */
    open fun onBackPressed() {
        if (gdxGame.navigationManager.isBackStackEmpty()) gdxGame.navigationManager.exit()
        else animHideScreen { gdxGame.navigationManager.back() }
    }

    abstract fun animShowScreen(blockEnd: Block = {})
    abstract fun animHideScreen(blockEnd: Block = {})

    open fun Group.addActorsOnStageUI() {}
    open fun AConstraintLayout.addActorsOnRootConstraintLayout() {}

    // ------------------------------------------------------------------------
    // Update Size
    // ------------------------------------------------------------------------
    private fun updateSize() {
        stageUI.update(screenWidthPX, screenHeightPX, true)
        scalerUItoScreen.calculateScale(scalerVector.set(screenWidthPX.toFloat(), screenHeightPX.toFloat()))

        backgroundImage.setSize(worldWidth, worldHeight)

        // Safe area: зверху статус-бар, знизу навігація (бари системи видимі й прозорі)
        rootConstraintLayout.setPosition(0f, safeNavBarUI)
        rootConstraintLayout.setSize(worldWidth, worldHeight - safeStatusBarUI - safeNavBarUI)
    }

    // ------------------------------------------------------------------------
    // Background
    // ------------------------------------------------------------------------
    fun setBackground(region: TextureRegion) {
        backgroundImage.drawable = TextureRegionDrawable(region)
    }

    fun setBackground(texture: Texture) {
        backgroundImage.drawable = TextureRegionDrawable(texture)
    }


}