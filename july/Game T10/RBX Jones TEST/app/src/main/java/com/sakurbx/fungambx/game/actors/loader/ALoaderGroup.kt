package com.sakurbx.fungambx.game.actors.loader

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.layout.AlignH
import com.sakurbx.fungambx.game.actors.layout.AlignV
import com.sakurbx.fungambx.game.utils.actor.addActorAligned
import com.sakurbx.fungambx.game.utils.actor.animHideAndDisable
import com.sakurbx.fungambx.game.utils.actor.animShowAndEnable
import com.sakurbx.fungambx.game.utils.advanced.AdvancedGroup
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame

class ALoaderGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aStarImg     = Image(gdxGame.assetsLoader.STAR)
    private val aLogoImg     = Image(gdxGame.assetsLoader.logo)
    private val aLeaf        = ALeaf(screen)
    private val aLoaderImg   = Image(gdxGame.assetsLoader.loader)

    private val aPanelNoWifi = APanelNoWifi(screen)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onRetry = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addStarImg()
        addLogoImg()
        addLeaf()
        addLoaderImg()

        addPanelNoWifi()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addStarImg() {
        addActor(aStarImg)
        aStarImg.setBounds(-56f, 163f, 489f, 489f)
        aStarImg.setOrigin(Align.center)

        animStar()
    }

    private fun addLogoImg() {
        addActor(aLogoImg)
        aLogoImg.setBounds(61f, 293f, 279f, 242f)

        animLogo()
    }

    private fun addLeaf() {
        addActor(aLeaf)
        aLeaf.setBounds(61f, 293f, 279f, 242f)
    }

    private fun addLoaderImg() {
        addActor(aLoaderImg)
        aLoaderImg.setBounds(153f, 134f, 71f, 71f)

        animLoader()
    }

    private fun addPanelNoWifi() {
        aPanelNoWifi.animHideAndDisable()

        aPanelNoWifi.setSize(316f, 316f)
        addActorAligned(aPanelNoWifi, AlignH.CENTER, AlignV.CENTER)

        aPanelNoWifi.onRetry = { onRetry() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun showNoWifi() {
        aPanelNoWifi.animShowAndEnable(0.2f)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animStar() {
        aStarImg.apply {
            setOrigin(Align.center)

            // повільне безперервне обертання
            addAction(
                Actions.forever(
                    Actions.rotateBy(360f, 24f, Interpolation.linear)
                )
            )

            // пульсація (сяйво) — окремо, паралельно з обертанням
            addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.scaleTo(1.12f, 1.12f, 2f, Interpolation.sine),
                        Actions.scaleTo(0.95f, 0.95f, 2f, Interpolation.sine),
                    )
                )
            )
        }
    }

    private fun animLogo() {
        aLogoImg.apply {
            setOrigin(Align.center)

            color.a  = 0f
            rotation = -6f
            setScale(0.6f)

            addAction(
                Actions.sequence(

                    // ── плавна поява: м'яко випливає, підростає, докручується ──
                    Actions.parallel(

                        Actions.fadeIn(0.6f, Interpolation.pow2Out),

                        Actions.scaleTo(1.06f, 1.06f, 0.7f, Interpolation.pow3Out), // плавно підростає з легким перельотом
                        Actions.rotateTo(0f, 0.7f, Interpolation.pow2Out),
                    ),

                    // м'яке осідання до 1.0
                    Actions.scaleTo(1f, 1f, 0.35f, Interpolation.sine),

                    // ── дихання ──
                    Actions.forever(
                        Actions.sequence(
                            Actions.scaleTo(1.03f, 1.03f, 1.6f, Interpolation.sine),
                            Actions.scaleTo(0.98f, 0.98f, 1.6f, Interpolation.sine),
                        )
                    )
                )
            )
        }
    }

    private fun animLoader() {
        aLoaderImg.apply {
            setOrigin(Align.center)
            addAction(Actions.forever(Actions.rotateBy(-360f, 1.35f, Interpolation.linear)))
        }
    }

}