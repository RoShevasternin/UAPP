package com.racing.funtols.game.actors.panel.converter

import android.R
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.racing.funtols.game.actors.checkbox.base.ACheckBox
import com.racing.funtols.game.actors.checkbox.base.ACheckBoxGroup
import com.racing.funtols.game.actors.checkbox.base.ACheckBoxStyles
import com.racing.funtols.game.utils.actor.disable
import com.racing.funtols.game.utils.actor.setOnClickListener
import com.racing.funtols.game.utils.advanced.AdvancedGroup
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.global.ConverterType
import com.racing.funtols.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

class APanelSelectConverter(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listBox     = List(5) { ACheckBox(screen, ACheckBoxStyles.CONVERTER) }
    private val aContentImg = Image(gdxGame.assetsAll.PANEL_CONVERTER_SELECT)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onSelectType: (ConverterType) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addListBtn()
        addAndFillActor(aContentImg)
        aContentImg.disable()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun AdvancedGroup.addListBtn() {
        var nx = 0f
        var ny = 166f

        val cbg = ACheckBoxGroup()

        listBox.forEachIndexed { index, box ->
            addActor(box)
            box.setBounds(nx, ny, 168f, 75f)

            nx += 8f + 168f
            if (index.inc() % 2 == 0) {
                nx = 0f
                ny -= 8f + 75f
            }

            box.checkBoxGroup = cbg

            box.setOnClickListener {
                GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.entries[index]
                onSelectType(GLOBAL_SELECTED_CONVERTER_TYPE)
            }
        }

        listBox.first().check()
    }

}