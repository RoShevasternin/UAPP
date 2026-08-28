package com.rbxhubpro.rohumex.game.actors.headAndBodyStep

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxhubpro.rohumex.game.actors.checkbox.base.ACheckBoxGroup
import com.rbxhubpro.rohumex.game.utils.actor.addActors
import com.rbxhubpro.rohumex.game.utils.actor.addAndFillActor
import com.rbxhubpro.rohumex.game.utils.actor.animHideAndDisable
import com.rbxhubpro.rohumex.game.utils.actor.animShowAndEnable
import com.rbxhubpro.rohumex.game.utils.actor.setOnClickListener
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedGroup
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.gdxGame
import com.rbxhubpro.rohumex.game.utils.wizardHelper.WizardStep

class AStepSelect(override val screen: AdvancedScreen): AdvancedGroup(), WizardStep {

    override val group = this
    override val title = "Head and body"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.HEAD_AND_BODY_SELECT)
    private val aLock       = Actor()
    private val aShape      = Actor()
    private val aHairs      = Actor()

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    override var onEnterBlock = {}

    var onLock  = {}
    var onShape = {}

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
        addActors(aLock, aShape, aHairs)
        aLock .setBounds(0f, 176f, 344f, 72f)
        aShape.setBounds(0f, 88f, 344f, 72f)
        aHairs.setBounds(0f, 0f, 344f, 72f)

        aLock .setOnClickListener { onLock() }
        aShape.setOnClickListener { onShape() }
        aHairs.setOnClickListener { onShape() }
    }

}