package com.rbuxrds.counterds.game.actors.select_1_Step

import com.badlogic.gdx.graphics.Color
import com.rbuxrds.counterds.game.actors.checkbox.base.ACheckBox
import com.rbuxrds.counterds.game.actors.checkbox.base.ACheckBoxStyles
import com.rbuxrds.counterds.game.actors.label.ALabel
import com.rbuxrds.counterds.game.utils.actor.animHideAndDisable
import com.rbuxrds.counterds.game.utils.actor.animShowAndEnable
import com.rbuxrds.counterds.game.utils.actor.disable
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.font.FontParameter
import com.rbuxrds.counterds.game.utils.wizardHelper.WizardStep

class AStepAnimationPack(override val screen: AdvancedScreen): AdvancedGroup(), WizardStep, SelectableBox {

    override val group = this
    override val title = "Select Animation Pack"

    private val listTitles = listOf(
        "Ninja",
        "Werewolf",
        "Knight",
        "Super Hero",
        "Villain",
        "Hard Cour",
        "Elder",
        "Vampire"
    )

    private val sizeItems = listTitles.size

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listBox = List(sizeItems) { ACheckBox(screen, ACheckBoxStyles.LONG) }
    private val listLbl = List(sizeItems) { ALabel(screen, listTitles[it], Color.WHITE, parameter, screen.fontGenerator_InterTight_Medium) }

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    override var onEnterBlock = {}
    override var onSelectBlock: (isCheck: Boolean) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addListBox()
        addListLbl()
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
        var ny = 448f

        //val cbg = ACheckBoxGroup()

        listBox.forEach { box ->
            //box.checkBoxGroup = cbg

            addActor(box)
            box.setBounds(0f, ny, 368f, 72f)
            ny -= -8f + 72f

            box.setOnCheckListener { onSelectBlock(it) }
        }
    }

    private fun addListLbl() {
        var ny = 473f

        listLbl.forEach { lbl ->
            addActor(lbl)
            lbl.setBounds(28f, ny, 44f, 22f)
            ny -= 42f + 22f

            lbl.disable()
        }
    }

}