package com.treprosure.starbxup.game.actors.panel.home

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.treprosure.starbxup.game.actors.layout.autoLayout.AAutoLayout
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.utils.actor.setOnClickListener
import com.treprosure.starbxup.game.utils.actor.setOnTouchListener
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.gdxGame

class APanelDuo(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[4])
    private val listBtn     = List(2) { Actor() }

    private val aHorizontal = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        gapMain   = 8f,
        alignMain = AAutoLayout.AlignMain.SPACE_BETWEEN
    )

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onQuiz = {}
    var onGift = {}

    private val listBlock = listOf(
        ::onQuiz,
        ::onGift,
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
            btn.width = 165f
            aHorizontal.add(btn) { alignSelf = AAutoLayout.AlignSelf.STRETCH }

            btn.setOnTouchListener { listBlock[index].get().invoke() }
        }
    }

}