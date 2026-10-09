package com.driftglass.home.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Align
import com.driftglass.home.game.manager.util.SoundUtil
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.gdxGame

/** Довге натискання: стільки мс без зсуву (як у системного лаунчера). Не companion — він ламає `this` в анонімних ATap. */
private const val LONG_PRESS_MS = 450L

// Базовий — тільки touch логіка, без анімацій
abstract class AButtonBase(
    override val screen: AdvancedScreen,
) : AdvancedGroup() {

    var onTouchDown    : AButtonBase.(x: Float, y: Float) -> Unit = { _, _ -> }
    var onTouchDragged : AButtonBase.(x: Float, y: Float) -> Unit = { _, _ -> }
    var onTouchUp      : AButtonBase.(x: Float, y: Float) -> Unit = { _, _ -> }

    private var onClickBlock : () -> Unit = {}
    private var clickSound   : SoundUtil.AdvancedSound? = null

    private val scrollThreshold = 10f
    private var startX    = 0f
    private var startY    = 0f
    private var isDragged = false

    /**
     * Довге натискання (LONG_PRESS_MS без зсуву), напр. іконка в лаунчері → App info / Uninstall.
     * Повертає true — оброблено: клік після відпускання не спрацює. false — як звичайний тап.
     */
    var onLongPress: (() -> Boolean)? = null
    private var pressedAtMs = 0L
    private var longFired   = false

    override fun addActorsOnGroup() {
        setOrigin(Align.center)
        addListener(buildListener())
    }

    private fun buildListener() = object : InputListener() {
        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            // Вкладені кнопки (⋮ у рядку треку): реагує лише найглибша. event.stop()
            // не годиться — тоді подія не дійде до ScrollPane і список не скролиться.
            if (innermostButton(event?.target) !== this@AButtonBase) return false
            startX = x; startY = y; isDragged = false
            pressedAtMs = System.currentTimeMillis(); longFired = false
            press()
            onTouchDown(x, y)
            return true
        }
        override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
            onTouchDragged(x, y)
            val dx = x - startX; val dy = y - startY
            if (dx * dx + dy * dy > scrollThreshold * scrollThreshold) {
                if (!isDragged) { isDragged = true; unpress() }
            }
        }
        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            onTouchUp(x, y)
            pressedAtMs = 0L
            unpress()
            if (longFired) return
            // Скасований touch focus (ScrollPane почав скрол) приходить з координатами
            // Int.MIN_VALUE — такий touchUp кліком не вважаємо.
            if (!isDragged && x >= 0f && y >= 0f && x <= width && y <= height) {
                clickSound?.let { gdxGame.soundUtil.play(it) }
                onClickBlock()
            }
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        val lp = onLongPress ?: return
        if (pressedAtMs > 0L && !isDragged && System.currentTimeMillis() - pressedAtMs >= LONG_PRESS_MS) {
            pressedAtMs = 0L
            if (lp()) { longFired = true; unpress() }
        }
    }

    private fun innermostButton(target: com.badlogic.gdx.scenes.scene2d.Actor?): AButtonBase? {
        var a = target
        while (a != null) { if (a is AButtonBase && a.touchable == com.badlogic.gdx.scenes.scene2d.Touchable.enabled) return a; a = a.parent }
        return null
    }

    abstract fun press()
    abstract fun unpress()
    abstract fun disable()
    abstract fun enable()

    fun setOnClickListener(
        sound: SoundUtil.AdvancedSound? = gdxGame.soundUtil.CLICK,
        block: () -> Unit,
    ) {
        clickSound   = sound
        onClickBlock = block
    }
}