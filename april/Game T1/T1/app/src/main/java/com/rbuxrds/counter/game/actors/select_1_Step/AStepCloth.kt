package com.rbuxrds.counter.game.actors.select_1_Step

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counter.game.actors.checkbox.base.ACheckBox
import com.rbuxrds.counter.game.actors.checkbox.base.ACheckBoxGroup
import com.rbuxrds.counter.game.actors.checkbox.base.ACheckBoxStyles
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.actor.animHideAndDisable
import com.rbuxrds.counter.game.utils.actor.animShowAndEnable
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.game.utils.wizardHelper.WizardStep

class AStepCloth(override val screen: AdvancedScreen): AdvancedGroup(), WizardStep {

    override val group = this
    override val title = "Select Cloth"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.SELECT_CLOTH)
    private val listBox     = List(6) { ACheckBox(screen, ACheckBoxStyles.HEX) }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    override var onEnterBlock = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aContentImg)
        addListBox()
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
    private fun addListBox() {
        var nx = -12f
        var ny = 193f

        val cbg = ACheckBoxGroup()

        listBox.forEachIndexed { index, box ->
            box.checkBoxGroup = cbg

            addActor(box)
            box.setBounds(nx, ny, 133f, 145f)
            nx += -15f + 133f
            if (index.inc() % 3 == 0) {
                ny -= 30f + 145f
                nx = -12f
            }

            box.setOnCheckListener {  }
        }
    }

}