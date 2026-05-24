package com.rbuxrds.counterds.game.actors.animationsStep

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.rbuxrds.counterds.game.actors.AScrollPane
import com.rbuxrds.counterds.game.actors.ATmpGroup
import com.rbuxrds.counterds.game.actors.layout.AlignV
import com.rbuxrds.counterds.game.actors.layout.linear.AVerticalGroup
import com.rbuxrds.counterds.game.utils.actor.addAndFillActor
import com.rbuxrds.counterds.game.utils.actor.animHideAndDisable
import com.rbuxrds.counterds.game.utils.actor.animShowAndEnable
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame
import com.rbuxrds.counterds.game.utils.wizardHelper.WizardStep
import com.rbuxrds.counterds.util.log

class AStepBundles(override val screen: AdvancedScreen): AdvancedGroup(), WizardStep {

    override val group = this
    override val title = "BUNDLES Animation"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, wrap = true)
    private val aContentImg    = Image(gdxGame.assetsAll.ANIMATIONS_BUNDLES)
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

        aContentImg.setSize(344f, 676f)
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