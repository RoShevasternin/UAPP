package com.mon.sterbx.game.actors.panel.outfit

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.checkbox.base.ACheckBox
import com.mon.sterbx.game.actors.checkbox.base.ACheckBoxGroup
import com.mon.sterbx.game.actors.checkbox.base.ACheckBoxStyles
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.actor.disable
import com.mon.sterbx.game.utils.actor.setFontColor
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
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
        //coroutine?.launch { aBox.checkFlow.collect { isCheck -> aLbl.setFontColor(if (isCheck) GameColor.black_60 else GameColor.white_FFF5E3) } }

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

        width = (24f + aLbl.width + 24f).coerceAtLeast(120f)
    }

    fun setCheckBoxGroup(cbg: ACheckBoxGroup) {
        aBox.checkBoxGroup = cbg
    }

    fun check() {
        aBox.check()
    }

}