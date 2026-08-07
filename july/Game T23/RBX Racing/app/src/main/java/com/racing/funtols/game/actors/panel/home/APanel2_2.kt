package com.racing.funtols.game.actors.panel.home

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.actor.setBounds
import com.racing.funtols.game.utils.actor.setOnTouchListener
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame

class APanel2_2(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[2])
    private val listBtn     = List(2) { Actor() }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onFuelPick  = {}
    var onFuelBoost = {}

    private val listBlock = listOf(
        ::onFuelPick,
        ::onFuelBoost,
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
            Rectangle(0f, 0f, 196f, 128f),
            Rectangle(204f, 0f, 140f, 128f),
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])

            btn.setOnTouchListener { listBlock[index].get().invoke() }
        }
    }

}