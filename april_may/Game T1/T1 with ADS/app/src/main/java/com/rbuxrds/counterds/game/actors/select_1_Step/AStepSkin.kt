package com.rbuxrds.counterds.game.actors.select_1_Step

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counterds.game.actors.checkbox.base.ACheckBox
import com.rbuxrds.counterds.game.actors.checkbox.base.ACheckBoxGroup
import com.rbuxrds.counterds.game.actors.checkbox.base.ACheckBoxStyles
import com.rbuxrds.counterds.game.utils.actor.addAndFillActor
import com.rbuxrds.counterds.game.utils.actor.animHideAndDisable
import com.rbuxrds.counterds.game.utils.actor.animShowAndEnable
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame
import com.rbuxrds.counterds.game.utils.wizardHelper.WizardStep

class AStepSkin(override val screen: AdvancedScreen): AdvancedGroup(), WizardStep, SelectableBox {

    override val group = this
    override val title = "Select Your Favorite Skin"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.SELECT_SKIN)
    private val listBox     = List(6) { ACheckBox(screen, ACheckBoxStyles.HEX) }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    override var onEnterBlock = {}
    override var onSelectBlock: (isCheck: Boolean) -> Unit = {}

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
        var nx = 4f
        var ny = 410f

        val cbg = ACheckBoxGroup()

        listBox.forEachIndexed { index, box ->
            box.checkBoxGroup = cbg

            addActor(box)
            box.setBounds(nx, ny, 192f, 211f)
            nx += -16f + 192f
            if (index.inc() % 2 == 0) {
                ny -= 0f + 211f
                nx = 4f
            }

            box.setOnCheckListener { onSelectBlock(it) }
        }
    }

}