package com.rbuxrds.counterds.game.actors.animationsStep

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counterds.game.actors.checkbox.base.ACheckBoxGroup
import com.rbuxrds.counterds.game.utils.actor.addActors
import com.rbuxrds.counterds.game.utils.actor.addAndFillActor
import com.rbuxrds.counterds.game.utils.actor.animHideAndDisable
import com.rbuxrds.counterds.game.utils.actor.animShowAndEnable
import com.rbuxrds.counterds.game.utils.actor.setOnClickListener
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame
import com.rbuxrds.counterds.game.utils.wizardHelper.WizardStep

class AStepSelect(override val screen: AdvancedScreen): AdvancedGroup(), WizardStep {

    override val group = this
    override val title = "Animations"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.ANIMATIONS_SELECT)
    private val aEmotes     = Actor()
    private val aBundles    = Actor()

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    override var onEnterBlock = {}

    var onEmotes  = {}
    var onBundles = {}

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
        addActors(aEmotes, aBundles)
        aEmotes.setBounds(0f, 88f, 344f, 72f)
        aBundles.setBounds(0f, 0f, 344f, 72f)

        aEmotes.setOnClickListener { onEmotes() }
        aBundles.setOnClickListener { onBundles() }
    }

}