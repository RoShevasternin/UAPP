package com.rsbuxs.rcounbux.game.actors.panel.miniGame

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rsbuxs.rcounbux.game.utils.actor.setOnClickListener
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedGroup
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame

class ARbuxFalling(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val aRbuxImg = Image(gdxGame.assetsAll.rbux)

    // Колбек коли юзер клікнув
    var onHit: (() -> Unit)? = null

    // Колбек коли досяг низу (для повернення в пул)
    var onMiss: (() -> Unit)? = null

    override fun addActorsOnGroup() {
        addAndFillActor(aRbuxImg)

        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                gdxGame.soundUtil.apply { play(CLICK) }
                clearActions()
                onHit?.invoke()
                event?.stop()
                return true
            }
        })
    }

    // Запускаємо падіння
    // parentHeight — висота контейнера (AMask)
    // parentWidth  — ширина контейнера
    fun startFall(parentWidth: Float, parentHeight: Float) {
        clearActions()
        isVisible = true

        // Рандомна X позиція
        val randomX = MathUtils.random(0f, parentWidth - width)
        setPosition(randomX, parentHeight) // стартуємо зверху

        // Рандомна швидкість падіння
        val duration = MathUtils.random(2f, 4f)

        addAction(
            Actions.sequence(
                Actions.moveTo(randomX, -height, duration),
                Actions.run { onMiss?.invoke() }
            )
        )
    }

    fun reset() {
        clearActions()
        isVisible = false
        onHit  = null
        onMiss = null
    }
}