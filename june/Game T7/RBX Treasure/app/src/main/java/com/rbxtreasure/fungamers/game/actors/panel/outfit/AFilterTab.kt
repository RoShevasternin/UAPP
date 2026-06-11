package com.rbxtreasure.fungamers.game.actors.panel.outfit

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxtreasure.fungamers.game.actors.checkbox.base.ACheckBox
import com.rbxtreasure.fungamers.game.actors.checkbox.base.ACheckBoxGroup
import com.rbxtreasure.fungamers.game.actors.checkbox.base.ACheckBoxStyles
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.actor.disable
import com.rbxtreasure.fungamers.game.utils.actor.setFontColor
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import kotlinx.coroutines.launch

class AFilterTab(
    override val screen: AdvancedScreen,
    labelStyle: Label.LabelStyle,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBox = ACheckBox(screen, ACheckBoxStyles.FILTER_TAB)
    private val aLbl = Label("", labelStyle)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onCheck: (Boolean) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBox()
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBox() {
        add(aBox) { fillParent() } //center(); matchConstraint() }
        coroutine?.launch { aBox.checkFlow.collect { isCheck -> aLbl.setFontColor(if (isCheck) GameColor.black_0E0F0E else GameColor.beige_E2CEAA) } }

        aBox.setOnCheckListener { onCheck(it) }
    }

    private fun addLbl() {
        aLbl.setSize(1f, 14f)
        add(aLbl) { center() }
        aLbl.setAlignment(Align.center)

        aLbl.disable()
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setText(text: String) {
        aLbl.setText(text)
        aLbl.pack()

        width = (16f + aLbl.width + 16f)
    }

    fun setCheckBoxGroup(cbg: ACheckBoxGroup) {
        aBox.checkBoxGroup = cbg
    }

    fun check() {
        aBox.check()
    }

}