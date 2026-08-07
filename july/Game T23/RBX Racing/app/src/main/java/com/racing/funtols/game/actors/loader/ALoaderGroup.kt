package com.racing.funtols.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.layout.AlignH
import com.racing.funtols.game.actors.layout.AlignV
import com.racing.funtols.game.utils.actor.addActorAligned
import com.racing.funtols.game.utils.actor.animHideAndDisable
import com.racing.funtols.game.utils.actor.animShowAndEnable
import com.racing.funtols.game.utils.advanced.AdvancedGroup
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef = MsdfStyle(msdf, msdf.fontBarlow_Bold, 22f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLoaderImg  = Image(gdxGame.assetsLoader.loader)
    private val aPercentLbl = AMsdfLabel("0%", styleDef)

    private val aPanelNoWifi = APanelNoWifi(screen)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onRetry = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLoaderImg()

        addPanelNoWifi()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLoaderImg() {
        addActor(aLoaderImg)
        aLoaderImg.setBounds(141f, 117f, 94f, 94f)
        animLoader()

        addActor(aPercentLbl)
        aPercentLbl.setBounds(164f, 152f, 53f, 27f)
        aPercentLbl.setAlignment(Align.center)
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
    private fun animLoader() {
        aLoaderImg.setOrigin(Align.center)
        aLoaderImg.addAction(
            Actions.forever(
                Actions.sequence(
                    // активний розгін — даємо енергію, швидко набирає
                    Actions.rotateBy(-360f, 0.7f, Interpolation.pow2In),

                    // бжжж — крутиться на піку
                    Actions.rotateBy(-720f, 0.5f, Interpolation.linear),

                    // довгий інерційний вибіг — гальмує повільно через "тертя"
                    Actions.rotateBy(-540f, 2.2f, Interpolation.pow3Out),

                    // майже зупинилось — коротка пауза перед новим поштовхом
                    Actions.delay(0.15f),
                )
            )
        )
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun showNoWifi() {
        aPanelNoWifi.animShowAndEnable(0.2f)
    }

    fun setPercent(percent: Int) {
        aPercentLbl.setText("$percent%")
    }

}