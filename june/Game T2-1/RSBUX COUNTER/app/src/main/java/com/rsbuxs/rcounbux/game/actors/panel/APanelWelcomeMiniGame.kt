package com.rsbuxs.rcounbux.game.actors.panel

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.screens.main.BoostModeScreen
import com.rsbuxs.rcounbux.game.utils.actor.setBounds
import com.rsbuxs.rcounbux.game.utils.actor.setOnClickListener
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame
import kotlin.random.Random

class APanelWelcomeMiniGame(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg   = Image(gdxGame.assetsAll.WELCOME_MINI_GAME)
    private val listR         = List(3) { Image(gdxGame.assetsAll.listR[it]) }
    private val aBoostModeBtn = Actor()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addContentImg()
        addListR()
        addBoostModeBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addContentImg() {
        add(aContentImg) { fillParent() }
    }

    private fun addListR() {
        val listBounds = listOf(
            Rectangle(31f, 391f, 69f, 70f),
            Rectangle(233f, 409f, 95f, 96f),
            Rectangle(248f, 376f, 81f, 80f),
        )
        listR.forEachIndexed { index, img ->
            addActor(img)
            img.setBounds(listBounds[index])
            animR(img, index * 0.3f)
        }
    }

    private fun addBoostModeBtn() {
        aBoostModeBtn.setSize(344f, 60f)
        add(aBoostModeBtn) {
            centerX()
            bottomToBottom()
        }

        aBoostModeBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(BoostModeScreen::class.java.name, screen::class.java.name) }
        }
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------
    private fun animR(img: Image, delay: Float) {
        img.apply {
            setOrigin(Align.center)

            val move = 6f + Random.nextFloat() * 4f     // 6–10px
            val duration = 1.2f + Random.nextFloat()   // різна швидкість

            addAction(sequence(
                delay(delay),

                forever(
                    parallel(

                        // float
                        sequence(
                            moveBy(0f, move, duration, Interpolation.sine),
                            moveBy(0f, -move, duration, Interpolation.sine)
                        ),

                        // легкий rotate
                        sequence(
                            rotateBy(8f, duration, Interpolation.sine),
                            rotateBy(-8f, duration, Interpolation.sine)
                        ),

                        sequence(
                            scaleTo(1.05f, 1.05f, duration, Interpolation.sine),
                            scaleTo(0.95f, 0.95f, duration, Interpolation.sine)
                        ),

                    )
                )
            ))
        }
    }

}