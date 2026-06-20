package com.coinsclub.funrbx.game.actors.panel.home

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coinsclub.funrbx.game.actors.layout.autoLayout.AAutoLayout
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.screens.home.WheelScreen
import com.coinsclub.funrbx.game.utils.actor.setBounds
import com.coinsclub.funrbx.game.utils.actor.setOnTouchListener
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame

class APanel5(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[2])
    private val listBtn     = List(5) { Actor() }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onWheel   = {}
    var onScratch = {}
    var onQuiz    = {}
    var onGuess   = {}
    var onFree    = {}

    private val listBlock = listOf(
        ::onWheel,
        ::onScratch,
        ::onQuiz,
        ::onGuess,
        ::onFree,
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addContentImg()
        addBtns()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addContentImg() {
        add(aContentImg) { fillParent() }
    }

    private fun addBtns() {
        val listBounds = listOf(
            Rectangle(19f, 303f, 149f, 149f),
            Rectangle(176f, 303f, 149f, 149f),
            Rectangle(19f, 146f, 149f, 149f),
            Rectangle(176f, 146f, 149f, 149f),
            Rectangle(19f, 21f, 306f, 117f),
        )

//        val listScreenName = listOf(
//            WheelScreen::class.java.name,
//            WheelScreen::class.java.name,
//            WheelScreen::class.java.name,
//            WheelScreen::class.java.name,
//            WheelScreen::class.java.name,
//        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])

            btn.setOnTouchListener { listBlock[index].get().invoke() }
        }
    }

}