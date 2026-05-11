package com.rbuxrds.counter.game.actors.clothingStep

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counter.game.actors.checkbox.base.ACheckBoxGroup
import com.rbuxrds.counter.game.utils.actor.addActors
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.actor.animHideAndDisable
import com.rbuxrds.counter.game.utils.actor.animShowAndEnable
import com.rbuxrds.counter.game.utils.actor.setOnClickListener
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.game.utils.wizardHelper.WizardStep

class AStepSelect(override val screen: AdvancedScreen): AdvancedGroup(), WizardStep {

    override val group = this
    override val title = "Clothing"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.CLOTHING_SELECT)
    private val aShoes      = Actor()
    private val aPants      = Actor()
    private val aT_Shiets   = Actor()
    private val aShirts     = Actor()

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    override var onEnterBlock = {}

    var onShoes    = {}
    var onPants    = {}
    var onT_Shiets = {}
    var onShirts   = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aContentImg)
        addBtns()
    }

    override fun onEnter() {
        onEnterBlock()
        animShowAndEnable(0.25f)
    }

    override fun onExit() {
        animHideAndDisable(0.25f)
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBtns() {
        addActors(aShoes, aPants, aT_Shiets, aShirts,)
        aShoes   .setBounds(0f, 264f, 344f, 72f)
        aPants   .setBounds(0f, 176f, 344f, 72f)
        aT_Shiets.setBounds(0f, 88f, 344f, 72f)
        aShirts  .setBounds(0f, 0f, 344f, 72f)

        aShoes   .setOnClickListener { onShoes() }
        aPants   .setOnClickListener { onPants() }
        aT_Shiets.setOnClickListener { onT_Shiets() }
        aShirts  .setOnClickListener { onShirts() }
    }

}