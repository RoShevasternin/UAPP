package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// ASheet — шторка знизу (.sheet + .sh): затемнення black 55 %, панель
// sheet_1B1215 з радіусом 26 зверху, «ручка» 38×4. Відкриття — slide 0.3 с,
// закриття — тап по затемненню, свайп вниз по панелі або «Назад».
//
// Підклас задає contentHeight і будує вміст у buildContent(w) — координати
// вмісту: 0..w × 0..contentHeight, y вгору. Дані прийшли пізніше (RSS) —
// rebuild(): вміст і висота панелі перераховуються на місці.
// ─────────────────────────────────────────────────────────────────────────────
abstract class ASheet(override val screen: AdvancedScreen) : AdvancedGroup() {

    protected val dim = Image(screen.drawerUtil.whiteRegion).apply { color.set(GameColor.black_55) }
    protected val panel = object : AdvancedGroup() {
        override val screen = this@ASheet.screen
        override fun addActorsOnGroup() {}
    }
    private val panelBg = ARect(screen, px(26f), GameColor.sheet_1B1215, stroke = Color(1f, 1f, 1f, 0.08f))
    private val grab    = ARect(screen, 999f, GameColor.white_25)
    private var body: AdvancedGroup? = null

    /** Висота вмісту під ручкою (без шапки з ручкою і без нижньої safe area). */
    abstract val contentHeight: Float
    abstract fun buildContent(w: Float)

    var onClosed: () -> Unit = {}
    var isClosing = false
        private set

    protected val padX = px(18f)
    private val headTop = px(10f + 4f + 4f + 12f)    // padding-top + ручка + відступ + gap
    private val bottomInset get() = screen.safeNavBarUI + px(18f)

    override fun addActorsOnGroup() {
        addAndFillActor(dim)
        dim.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) = true
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) { close() }
        })

        addActor(panel)
        panel.addActor(panelBg)
        panel.addActor(grab)
        panel.touchable = Touchable.enabled
        panel.addListener(object : InputListener() {
            var startY = 0f
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean { startY = event.stageY; return true }
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                if (startY - event.stageY > px(60f)) close()
            }
        })
        layoutPanel()

        // поява
        dim.color.a = 0f
        dim.addAction(Actions.alpha(GameColor.black_55.a, 0.2f))
        panel.y = -panel.height * 0.4f
        panel.color.a = 0f
        panel.addAction(Actions.parallel(Actions.moveTo(0f, 0f, 0.3f, Interpolation.pow3Out), Actions.fadeIn(0.15f)))
    }

    private fun layoutPanel() {
        val ph = (headTop + contentHeight + bottomInset).coerceAtMost(height * 0.88f)
        panel.setSize(width, ph)
        panelBg.setBounds(0f, -px(30f), width, ph + px(30f))       // нижні кути — під краєм екрана
        grab.setBounds((width - px(38f)) / 2f, ph - px(10f) - px(4f), px(38f), px(4f))

        body?.remove()
        val b = object : AdvancedGroup() {
            override val screen = this@ASheet.screen
            override fun addActorsOnGroup() { target = this; buildContent(width) }
        }
        body = b
        panel.addActor(b)
        b.setBounds(padX, bottomInset, width - padX * 2, contentHeight)
    }

    private var target: AdvancedGroup? = null

    /** Додати актора у вміст (координати вмісту, y вгору від низу). */
    protected fun add(actor: Actor) { target!!.addActor(actor) }

    /** Дані змінились — перебудувати вміст і висоту. */
    fun rebuild() { if (stage != null && !isClosing) layoutPanel() }

    fun close() {
        if (isClosing) return
        isClosing = true
        touchable = Touchable.disabled
        dim.addAction(Actions.fadeOut(0.18f))
        panel.addAction(Actions.sequence(
            Actions.moveTo(0f, -panel.height, 0.22f, Interpolation.pow2In),
            Actions.run { onClosed(); remove() },
        ))
    }
}
