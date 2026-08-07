package com.diam.ondbit.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.layout.AlignH
import com.diam.ondbit.game.actors.layout.AlignV
import com.diam.ondbit.game.actors.progress.AProgressLoader
import com.diam.ondbit.game.utils.actor.addActorAligned
import com.diam.ondbit.game.utils.actor.animHideAndDisable
import com.diam.ondbit.game.utils.actor.animShowAndEnable
import com.diam.ondbit.game.utils.advanced.AdvancedGroup
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogo      = Image(gdxGame.assetsLoader.logo)
    private val aPers      = Image(gdxGame.assetsLoader.pers)
    private val aProgress  = AProgressLoader(screen)

    private val aPanelNoWifi = APanelNoWifi(screen)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Базові позиції — з них стартує левітація, до них повертається поява
    private val logoY = 588f
    private val persY = 174f

    // Поява
    private val timeShowLogo  = 0.5f
    private val timeShowPers  = 0.6f
    private val delayShowPers = 0.15f  // персонаж виїжджає трохи після лого
    private val offsetLogo    = 30f    // лого «сідає» згори
    private val offsetPers    = 45f    // персонаж спливає знизу

    // Левітація
    private val floatDown = 22f   // наскільки провисає вниз
    private val timeFloat = 1.8f  // період дрейфу
    private val timeSway  = 2.6f  // період нахилу — навмисно НЕ кратний timeFloat
    private val swayAngle = 2.5f

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onRetry = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addPersImg()
        addLoaderImg()

        addPanelNoWifi()

        animShow()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLogoImg() {
        addActor(aLogo)
        aLogo.setBounds(20f, logoY, 334f, 72f)
    }

    private fun addPersImg() {
        addActor(aPers)
        aPers.setBounds(0f, persY, 376f, 457f)
    }

    private fun addLoaderImg() {
        addActor(aProgress)
        aProgress.setBounds(16f, 93f, 344f, 20f)
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

    /** Поява: лого сідає згори, персонаж спливає знизу і починає левітувати */
    private fun animShow() {
        aLogo.clearActions()
        aPers.clearActions()

        // Стартові стани — прозорі й зміщені
        aLogo.color.a = 0f
        aLogo.setPosition(20f, logoY + offsetLogo)

        aPers.color.a = 0f
        aPers.setPosition(0f, persY - offsetPers)
        aPers.setOrigin(Align.center) // без цього нахил крутив би навколо кута

        aLogo.addAction(Actions.parallel(
            Actions.alpha(1f, timeShowLogo),
            Actions.moveTo(20f, logoY, timeShowLogo, Interpolation.pow2Out)
        ))

        aPers.addAction(Actions.sequence(
            Actions.delay(delayShowPers),
            Actions.parallel(
                Actions.alpha(1f, timeShowPers),
                Actions.moveTo(0f, persY, timeShowPers, Interpolation.pow2Out)
            ),
            // доїхав на місце — віддаємо його левітації
            Actions.run { animPers() }
        ))
    }

    /** Космічна левітація: дрейф вниз-вгору + легке погойдування */
    private fun animPers() {
        aPers.clearActions()
        aPers.setOrigin(Align.center)

        // Вертикальний дрейф
        aPers.addAction(Actions.forever(Actions.sequence(
            Actions.moveTo(0f, persY - floatDown, timeFloat, Interpolation.sine),
            Actions.moveTo(0f, persY,             timeFloat, Interpolation.sine)
        )))

        // Погойдування — окрема дія зі своїм періодом, щоб рух не був механічним
        aPers.addAction(Actions.forever(Actions.sequence(
            Actions.rotateTo(-swayAngle, timeSway, Interpolation.sine),
            Actions.rotateTo( swayAngle, timeSway, Interpolation.sine)
        )))
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun showNoWifi() {
        aPanelNoWifi.animShowAndEnable(0.2f)
    }

    fun setPercent(percent: Int) {
        aProgress.progressPercentFlow.value = percent.toFloat()
    }

}