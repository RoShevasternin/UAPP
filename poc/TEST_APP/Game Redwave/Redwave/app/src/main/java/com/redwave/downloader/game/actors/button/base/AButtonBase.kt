package com.redwave.downloader.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Align
import com.redwave.downloader.game.manager.util.SoundUtil
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.gdxGame

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
            unpress()
            // Скасований touch focus (ScrollPane почав скрол) приходить з координатами
            // Int.MIN_VALUE — такий touchUp кліком не вважаємо.
            if (!isDragged && x >= 0f && y >= 0f && x <= width && y <= height) {
                clickSound?.let { gdxGame.soundUtil.play(it) }
                onClickBlock()
            }
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