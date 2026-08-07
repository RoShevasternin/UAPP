package com.racing.funtols.game.actors.panel.outfit

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.checkbox.base.ACheckBox
import com.racing.funtols.game.actors.checkbox.base.ACheckBoxGroup
import com.racing.funtols.game.actors.checkbox.base.ACheckBoxStyles
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.GameColor
import com.racing.funtols.game.utils.actor.disable
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import kotlinx.coroutines.launch

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
        coroutine?.launch { aBox.checkFlow.collect { isCheck -> aLbl.setTextColor(if (isCheck) Color.WHITE else GameColor.black_1A1A1A) } }

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