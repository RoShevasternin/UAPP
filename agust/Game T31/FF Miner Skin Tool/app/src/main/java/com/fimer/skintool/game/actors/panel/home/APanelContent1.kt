package com.fimer.skintool.game.actors.panel.home

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.utils.actor.setBounds
import com.fimer.skintool.game.utils.actor.setOnTouchListener
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame

class APanelContent1(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[0])
    private val listBtn     = List(7) { Actor() }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onEmotes    = {}
    var onWeapon    = {}
    var onVehicles  = {}
    var onParachute = {}
    var onBundles   = {}
    var onPets      = {}
    var onCharacter = {}

    private val listBlock = listOf(
        ::onEmotes,
        ::onWeapon,
        ::onVehicles,
        ::onParachute,
        ::onBundles,
        ::onPets,
        ::onCharacter,
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
            Rectangle(24f, 454f, 145f, 138f),
            Rectangle(177f, 454f, 145f, 138f),
            Rectangle(24f, 308f, 145f, 138f),
            Rectangle(177f, 308f, 145f, 138f),
            Rectangle(24f, 162f, 145f, 138f),
            Rectangle(177f, 162f, 145f, 138f),
            Rectangle(24f, 16f, 298f, 138f),
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(listBounds[index])

            btn.setOnTouchListener { listBlock[index].get().invoke() }
        }
    }

}