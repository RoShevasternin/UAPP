package com.diam.ondbit.game.actors.panel.outfit

import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.checkbox.base.ACheckBox
import com.diam.ondbit.game.actors.checkbox.base.ACheckBoxGroup
import com.diam.ondbit.game.actors.checkbox.base.ACheckBoxStyles
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.actor.disable
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle

class AFilterTab(
    override val screen: AdvancedScreen,
    style: MsdfStyle,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBox = ACheckBox(screen, ACheckBoxStyles.FILTER_TAB)
    private val aLbl = AMsdfLabel("", style)

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
        add(aBox) { fillParent() }
        //coroutine?.launch { aBox.checkFlow.collect { isCheck -> aLbl.setTextColor(if (isCheck) Color.WHITE else GameColor.black_D0C8DE) } }

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
        width = (24f + aLbl.width + 24f).coerceAtLeast(70f)
    }

    fun setCheckBoxGroup(cbg: ACheckBoxGroup) {
        aBox.checkBoxGroup = cbg
    }

    fun check() {
        aBox.check()
    }

}