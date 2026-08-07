package com.racing.funtols.game.actors.panel.pick

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.actor.disable
import com.racing.funtols.game.utils.actor.enable
import com.racing.funtols.game.utils.actor.setOnClickListener
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame

// ------------------------------------------------------------------------
// ACan — одна каністра
// ------------------------------------------------------------------------
// Три картинки одна на одній, перемикаємо видимість:
//   aFuelImg — закрита каністра
//   aWinImg  — приз (персонаж + 25 RBX)
//   aFailImg — пустишка (хрестик)
//
// Відкриття — той самий flip, що в TurboMatch: стискаємо по X до нуля,
// в найвужчій точці підміняємо картинку, розтягуємо назад.
class ACan(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onClick: () -> Unit = {}

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aFuelImg = Image(gdxGame.assetsAll.FUEL)
    private val aWinImg  = Image(gdxGame.assetsAll.WIN)
    private val aFailImg = Image(gdxGame.assetsAll.FAIL)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeFlip = 0.12f // половина перевороту

    // Наскільки приз/хрестик менші за клітинку каністри.
    // Пропорції не спотворюються (Scaling.fit) — це просто розмір.
    // Підбери під свої асети, якщо здаватиметься завеликим/замалим.
    private val revealScale = 0.85f

    var isOpened = false
        private set

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        // Потрібно для scaleX-анімації: без transform група не масштабується
        isTransform = true

        add(aFuelImg) { fillParent() }
        add(aWinImg)  { center(); fillWidth(revealScale); fillHeight(revealScale) }
        add(aFailImg) { center(); fillWidth(revealScale); fillHeight(revealScale) }

        // Зберігаємо пропорції — асети не розтягуються під клітинку
        aWinImg.setScaling(Scaling.fit)
        aFailImg.setScaling(Scaling.fit)

        aWinImg.isVisible  = false
        aFailImg.isVisible = false

        setOnClickListener { onClick() }
    }

    override fun sizeChanged() {
        super.sizeChanged()
        setOrigin(Align.center) // переворот навколо центру, а не кута
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** Відкрити каністру: [isWin] — приз чи пустишка */
    fun reveal(isWin: Boolean, block: () -> Unit = {}) {
        if (isOpened) { block(); return }
        isOpened = true

        disable() // відкриту каністру більше не натиснути

        clearActions()
        addAction(Actions.sequence(
            Actions.scaleTo(0f, 1f, timeFlip, Interpolation.fade),
            Actions.run {
                aFuelImg.isVisible = false
                aWinImg.isVisible  = isWin
                aFailImg.isVisible = !isWin
            },
            Actions.scaleTo(1f, 1f, timeFlip, Interpolation.fade),
            Actions.run(block)
        ))
    }

    /** Повернути в закритий стан (нова гра) */
    fun reset() {
        clearActions()
        isOpened           = false
        scaleX             = 1f
        aFuelImg.isVisible = true
        aWinImg.isVisible  = false
        aFailImg.isVisible = false
        enable()
    }
}