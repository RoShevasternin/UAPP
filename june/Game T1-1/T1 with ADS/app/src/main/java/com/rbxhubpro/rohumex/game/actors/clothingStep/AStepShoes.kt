package com.rbxhubpro.rohumex.game.actors.clothingStep

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxhubpro.rohumex.game.actors.AScrollPane
import com.rbxhubpro.rohumex.game.actors.layout.linear.AVerticalGroup
import com.rbxhubpro.rohumex.game.utils.actor.addAndFillActor
import com.rbxhubpro.rohumex.game.utils.actor.animHideAndDisable
import com.rbxhubpro.rohumex.game.utils.actor.animShowAndEnable
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedGroup
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.gdxGame
import com.rbxhubpro.rohumex.game.utils.wizardHelper.WizardStep

class AStepShoes(override val screen: AdvancedScreen): AdvancedGroup(), WizardStep {

    override val group = this
    override val title = "SHOES Collection"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, wrap = true)
    private val aContentImg    = Image(gdxGame.assetsAll.COLLECTION_SHOES)
    private val aScrollPane    = AScrollPane(aVerticalGroup)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    override var onEnterBlock = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aScrollPane)
        aVerticalGroup.wrapMinHeight = height

        aContentImg.setSize(376f, 1233f)
        aVerticalGroup.addActor(aContentImg)
    }

    override fun onEnter() {
        onEnterBlock()
        animShowAndEnable(0.25f)
    }

    override fun onExit() {
        animHideAndDisable(0.25f)
    }

}