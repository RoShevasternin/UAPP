package com.rbxtreasure.fungamers.game.actors.panel.home

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxtreasure.fungamers.game.actors.layout.autoLayout.AAutoLayout
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.utils.actor.setOnClickListener
import com.rbxtreasure.fungamers.game.utils.actor.setOnTouchListener
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame

class APanelTrio(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[3])
    private val listBtn     = List(3) { Actor() }

    private val aHorizontal = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        gapMain   = 8f,
        alignMain = AAutoLayout.AlignMain.SPACE_BETWEEN
    )

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onScratch = {}
    var onWheel   = {}
    var onFinds   = {}

    private val listBlock = listOf(
        ::onScratch,
        ::onWheel,
        ::onFinds,
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addContentImg()
        addHorizontal()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addContentImg() {
        add(aContentImg) { fillParent() }
    }

    private fun addHorizontal() {
        add(aHorizontal) { fillParent() }

        listBtn.forEachIndexed { index, btn ->
            btn.width = 110f
            aHorizontal.add(btn) { alignSelf = AAutoLayout.AlignSelf.STRETCH }

            btn.setOnTouchListener { listBlock[index].get().invoke() }
        }
    }

}