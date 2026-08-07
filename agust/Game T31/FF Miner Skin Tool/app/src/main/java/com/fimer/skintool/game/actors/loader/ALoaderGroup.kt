package com.fimer.skintool.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.fimer.skintool.game.actors.layout.AlignH
import com.fimer.skintool.game.actors.layout.AlignV
import com.fimer.skintool.game.utils.actor.addActorAligned
import com.fimer.skintool.game.utils.actor.animHideAndDisable
import com.fimer.skintool.game.utils.actor.animShowAndEnable
import com.fimer.skintool.game.utils.advanced.AdvancedGroup
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPers      = Image(gdxGame.assetsLoader.pers)
    private val aLoader    = Image(gdxGame.assetsLoader.loader)

    private val aPanelNoWifi = APanelNoWifi(screen)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Базові позиції — з них стартує анімація і до них повертається
    private val persX = 9f
    private val persY = 91f

    // Поява
    private val timeShowPers   = 0.55f
    private val timeShowLoader = 0.35f
    private val delayLoader    = 0.25f  // лоадер зʼявляється після персонажа
    private val offsetPers     = 35f    // персонаж спливає знизу
    private val scalePersIn    = 0.94f  // і трохи розростається на місце

    // Дихання персонажа
    private val breathScale = 1.025f  // мікроскопічно — інакше це вже пульсація
    private val timeBreath  = 1.6f
    private val floatUp     = 8f      // ледь помітний дрейф угору
    private val timeFloat   = 2.3f    // період НЕ кратний timeBreath

    // Обертання лоадера
    private val timeSpin = 1.1f  // повний оберт

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onRetry = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPersImg()
        addLoaderImg()

        addPanelNoWifi()

        animShow()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPersImg() {
        addActor(aPers)
        aPers.setBounds(persX, persY, 383f, 576f)
    }

    private fun addLoaderImg() {
        addActor(aLoader)
        aLoader.setBounds(152f, 101f, 71f, 71f)
    }

    private fun addPanelNoWifi() {
        aPanelNoWifi.animHideAndDisable()

        aPanelNoWifi.setSize(316f, 316f)
        addActorAligned(aPanelNoWifi, AlignH.CENTER, AlignV.CENTER)

        aPanelNoWifi.onRetry = { onRetry() }
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------

    /** Поява: персонаж спливає знизу, слідом проявляється лоадер */
    private fun animShow() {
        aPers.clearActions()
        aLoader.clearActions()

        // Origin по центру — інакше scale тягнув би від лівого нижнього кута
        aPers.setOrigin(Align.center)
        aLoader.setOrigin(Align.center)

        // Стартові стани
        aPers.color.a = 0f
        aPers.setPosition(persX, persY - offsetPers)
        aPers.setScale(scalePersIn)

        aLoader.color.a = 0f
        aLoader.setScale(0.5f)

        aPers.addAction(Actions.sequence(
            Actions.parallel(
                Actions.alpha(1f, timeShowPers),
                Actions.moveTo(persX, persY, timeShowPers, Interpolation.pow2Out),
                Actions.scaleTo(1f, 1f, timeShowPers, Interpolation.pow3Out)
            ),
            // доїхав на місце — віддаємо його диханню
            Actions.run { animPers() }
        ))

        aLoader.addAction(Actions.sequence(
            Actions.delay(delayLoader),
            Actions.parallel(
                Actions.alpha(1f, timeShowLoader),
                Actions.scaleTo(1f, 1f, timeShowLoader, Interpolation.swingOut)
            ),
            Actions.run { animLoader() }
        ))
    }

    /** Персонаж: легке дихання + ледь помітний дрейф */
    private fun animPers() {
        aPers.clearActions()
        aPers.setOrigin(Align.center)

        // Дихання
        aPers.addAction(Actions.forever(Actions.sequence(
            Actions.scaleTo(breathScale, breathScale, timeBreath, Interpolation.sine),
            Actions.scaleTo(1f, 1f, timeBreath, Interpolation.sine)
        )))

        // Дрейф — окрема дія зі своїм періодом, щоб рух не був механічним
        aPers.addAction(Actions.forever(Actions.sequence(
            Actions.moveTo(persX, persY + floatUp, timeFloat, Interpolation.sine),
            Actions.moveTo(persX, persY,           timeFloat, Interpolation.sine)
        )))
    }

    /** Лоадер: безкінечне обертання навколо центру */
    private fun animLoader() {
        aLoader.clearActions()
        aLoader.setOrigin(Align.center)

        // -360 = за годинниковою стрілкою (у scene2d кут росте проти неї)
        aLoader.addAction(Actions.forever(
            Actions.rotateBy(-360f, timeSpin, Interpolation.linear)
        ))
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun showNoWifi() {
        aPanelNoWifi.animShowAndEnable(0.2f)
    }

}