package com.fimer.skintool.game.actors.panel.home

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.utils.actor.setBounds
import com.fimer.skintool.game.utils.actor.setOnTouchListener
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame

class APanelContent2(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[1])
    private val listBtn     = List(3) { Actor() }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onCalculator = {}
    var onTips       = {}
    var onFree       = {}

    private val listBlock = listOf(
        ::onCalculator,
        ::onTips,
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
            Rectangle(24f, 316f, 298f, 138f),
            Rectangle(24f, 170f, 298f, 138f),
            Rectangle(24f, 24f, 298f, 138f),
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])

            btn.setOnTouchListener { listBlock[index].get().invoke() }
        }
    }

}