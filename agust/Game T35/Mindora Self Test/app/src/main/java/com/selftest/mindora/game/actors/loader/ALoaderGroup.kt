package com.selftest.mindora.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.layout.AlignH
import com.selftest.mindora.game.actors.layout.AlignV
import com.selftest.mindora.game.utils.actor.addActorAligned
import com.selftest.mindora.game.utils.actor.animHideAndDisable
import com.selftest.mindora.game.utils.actor.animShowAndEnable
import com.selftest.mindora.game.utils.advanced.AdvancedGroup
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aIcon   = Image(gdxGame.assetsLoader.icon)
    private val aLogo   = Image(gdxGame.assetsLoader.logo)
    private val aLoader = Image(gdxGame.assetsLoader.loader)

    private val aPanelNoWifi = APanelNoWifi(screen)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Базові позиції — з них стартує анімація і до них повертається
    private val logoX   = 60f;  private val logoY   = 540f
    private val iconX   = 60f;  private val iconY   = 268f
    private val loaderX = 152f; private val loaderY = 99f

    // Параметри лупів
    private val logoTilt      = 3.5f   // амплітуда обертів лого, градуси
    private val logoScaleMin  = 0.93f  // до якого масштабу стискається лого
    private val logoTiltTime  = 1.4f   // час чверті циклу обертання
    private val logoPulseTime = 0.95f  // час половини циклу пульсації
    private val loaderSpinTime = 1.1f  // повний оберт лоадера, сек

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onRetry = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addIconImg()
        addLoaderImg()

        addPanelNoWifi()

        animShow()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLogoImg() {
        addActor(aLogo)
        aLogo.setBounds(logoX, logoY, 256f, 67f)
        aLogo.setOrigin(Align.center)
    }

    private fun addIconImg() {
        addActor(aIcon)
        aIcon.setBounds(iconX, iconY, 256f, 256f)
        aIcon.setOrigin(Align.center)
    }

    private fun addLoaderImg() {
        addActor(aLoader)
        aLoader.setBounds(loaderX, loaderY, 71f, 71f)
        aLoader.setOrigin(Align.center)
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

    /**
     * Поява: лого сходить зверху, іконка спливає знизу, лоадер проявляється
     * останнім. Кожен елемент після появи одразу переходить у свій вічний луп.
     */
    private fun animShow() {
        aLogo.clearActions()
        aIcon.clearActions()
        aLoader.clearActions()

        // --- стартовий стан ---
        aLogo.setPosition(logoX, logoY + 40f)
        aLogo.setScale(1f)
        aLogo.rotation = 0f
        aLogo.color.a = 0f

        aIcon.setPosition(iconX, iconY - 60f)
        aIcon.setScale(0.88f)
        aIcon.color.a = 0f

        aLoader.setPosition(loaderX, loaderY)
        aLoader.setScale(0.5f)
        aLoader.rotation = 0f
        aLoader.color.a = 0f

        // --- лого: з'їжджає вниз на місце, далі вічний луп ---
        aLogo.addAction(Actions.sequence(
            Actions.delay(0.10f),
            Actions.parallel(
                Actions.fadeIn(0.45f, Interpolation.fade),
                Actions.moveTo(logoX, logoY, 0.55f, Interpolation.swingOut)
            ),
            animIcon()
        ))

        // --- іконка: спливає знизу з легким доростанням масштабу ---
        aIcon.addAction(Actions.sequence(
            Actions.delay(0.22f),
            Actions.parallel(
                Actions.fadeIn(0.50f, Interpolation.fade),
                Actions.moveTo(iconX, iconY, 0.60f, Interpolation.swingOut),
                Actions.scaleTo(1f, 1f, 0.60f, Interpolation.swingOut)
            )
        ))

        // --- лоадер: проявляється останнім і починає крутитись ---
        aLoader.addAction(Actions.sequence(
            Actions.delay(0.50f),
            Actions.parallel(
                Actions.fadeIn(0.35f, Interpolation.fade),
                Actions.scaleTo(1f, 1f, 0.40f, Interpolation.sineOut)
            ),
            animLoader()
        ))
    }

    /** Лоадер: безкінечне обертання за годинниковою стрілкою, лінійно */
    private fun animLoader(): Action = Actions.forever(
        Actions.rotateBy(-360f, loaderSpinTime)
    )

    /** Іконка: легке «дихання» — за замовчуванням вимкнене */
    private fun animIcon(): Action = Actions.forever(Actions.sequence(
        Actions.scaleTo(1.03f, 1.03f, 1.6f, Interpolation.sine),
        Actions.scaleTo(1f, 1f, 1.6f, Interpolation.sine)
    ))

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun showNoWifi() {
        // лоадер більше не має сенсу — прибираємо його разом із появою панелі
        aLoader.clearActions()
        aLoader.addAction(Actions.parallel(
            Actions.fadeOut(0.2f, Interpolation.fade),
            Actions.scaleTo(0.5f, 0.5f, 0.2f, Interpolation.fade)
        ))

        aPanelNoWifi.animShowAndEnable(0.2f)
    }

}