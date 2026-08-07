package com.diam.ondbit.game.actors.panel.home

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.actor.setBounds
import com.diam.ondbit.game.utils.actor.setOnTouchListener
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame

class APanel4(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[1])
    private val listBtn     = List(4) { Actor() }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onMaps     = {}
    var onQuiz     = {}
    var onScratch  = {}
    var onUnlock   = {}

    private val listBlock = listOf(
        ::onMaps,
        ::onQuiz,
        ::onScratch,
        ::onUnlock,
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
            Rectangle(0f, 177f, 168f, 168f),
            Rectangle(176f, 177f, 168f, 168f),
            Rectangle(0f, 0f, 168f, 168f),
            Rectangle(177f, 0f, 168f, 168f),
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])

            btn.setOnTouchListener { listBlock[index].get().invoke() }
        }
    }

}