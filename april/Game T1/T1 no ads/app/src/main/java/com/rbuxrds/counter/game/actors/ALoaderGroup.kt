package com.rbuxrds.counter.game.actors

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo
import com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel
import com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat
import com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy
import com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    //private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + textLoading + ".%").setSize(100)
    //private val font      = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aLogoImg  = Image(gdxGame.assetsLoader.logo)
    private val aTitleImg = Image(gdxGame.assetsLoader.title)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        addLogoImg()
        addTitleImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLogoImg() {
        addActor(aLogoImg)
        aLogoImg.setBounds(125f, 365f, 126f, 136f)

        animLogo()
    }

    private fun addTitleImg() {
        addActor(aTitleImg)
        aTitleImg.setBounds(74f, 318f, 228f, 28f)

        animTitle()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animLogo() {
        aLogoImg.apply {
            setOrigin(Align.center)

            color.a = 0f
            setScale(0.85f)

            addAction(sequence(
                // поява
                parallel(
                    fadeIn(0.5f, Interpolation.fade),
                    scaleTo(1f, 1f, 0.5f, Interpolation.smooth)
                ),

                // нескінченний плавний скейл
                forever(
                    sequence(
                        scaleTo(1.05f, 1.05f, 1.2f, Interpolation.sine),
                        scaleTo(0.95f, 0.95f, 1.2f, Interpolation.sine)
                    )
                )
            ))
        }
    }

    private fun animTitle() {
        aTitleImg.apply {
            setOrigin(Align.center)

            val finalY = y
            val startOffset = 40f

            color.a = 0f
            setScale(0.95f)
            setPosition(x, finalY - startOffset)

            addAction(sequence(
                delay(0.2f),

                parallel(
                    moveTo(x, finalY, 0.6f, Interpolation.smooth),
                    fadeIn(0.5f, Interpolation.fade),
                    scaleTo(1f, 1f, 0.5f, Interpolation.smooth)
                )
            ))
        }
    }

}