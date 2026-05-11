package com.rbuxrds.counterds.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counterds.game.utils.actor.animHideAndDisable
import com.rbuxrds.counterds.game.utils.actor.animShowAndEnable
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    //private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + textLoading + ".%").setSize(100)
    //private val font      = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aLogoImg  = Image(gdxGame.assetsLoader.logo)
    private val aTitleImg = Image(gdxGame.assetsLoader.title)

    private val aPanelNoWifi = APanelNoWifi(screen)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onRetry = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addTitleImg()

        addPanelNoWifi()
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

    private fun addPanelNoWifi() {
        aPanelNoWifi.animHideAndDisable()
        addActor(aPanelNoWifi)
        aPanelNoWifi.setBounds(30f, 249f, 316f, 316f)

        aPanelNoWifi.onRetry = { onRetry() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun showNoWifi() {
        aPanelNoWifi.animShowAndEnable(0.25f)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animLogo() {
        aLogoImg.apply {
            setOrigin(Align.center)

            color.a = 0f
            setScale(0.85f)

            addAction(
                Actions.sequence(
                    // поява
                    Actions.parallel(
                        Actions.fadeIn(0.5f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.5f, Interpolation.smooth)
                    ),

                    // нескінченний плавний скейл
                    Actions.forever(
                        Actions.sequence(
                            Actions.scaleTo(1.05f, 1.05f, 1.2f, Interpolation.sine),
                            Actions.scaleTo(0.95f, 0.95f, 1.2f, Interpolation.sine)
                        )
                    )
                )
            )
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

            addAction(
                Actions.sequence(
                    Actions.delay(0.2f),

                    Actions.parallel(
                        Actions.moveTo(x, finalY, 0.6f, Interpolation.smooth),
                        Actions.fadeIn(0.5f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.5f, Interpolation.smooth)
                    )
                )
            )
        }
    }

}