package com.skindustry.skinly.game.actors.panel.selector

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBox
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBoxStyles
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.NumberFormatter
import com.skindustry.skinly.game.utils.actor.disable
import com.skindustry.skinly.game.utils.actor.setFontColor
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelSelectorItem(
    override val screen: AdvancedScreen,
    labelStyle: Label.LabelStyle,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBox = ACheckBox(screen, ACheckBoxStyles.SELECTOR_ITEM)
    private val aLbl = Label("", labelStyle)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    val isChecked get() = aBox.isChecked
    val checkFlow get() = aBox.checkFlow

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
        add(aBox) { center(); matchConstraint() }
        coroutine?.launch { aBox.checkFlow.collect { isCheck -> aLbl.setFontColor(if (isCheck) Color.WHITE else Color.BLACK) } }
        aBox.setOnCheckListener {}
    }

    private fun addLbl() {
        aLbl.setSize(1f, 56f)
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

        width = (24f + aLbl.width + 24f)
    }

    // ------------------------------------------------------------------------
    // prefWidth
    // ------------------------------------------------------------------------
//    override fun getPrefWidth(): Float {
//        return 24f + aLbl.width + 24f
//    }

}