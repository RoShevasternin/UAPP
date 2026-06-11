package com.rbxtreasure.fungamers.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxtreasure.fungamers.game.screens.home.converter.ConverterScreen
import com.rbxtreasure.fungamers.game.utils.actor.setOnClickListener
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedGroup
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.game.utils.global.ConverterType
import com.rbxtreasure.fungamers.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

class APanelSelectConverter(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.PANEL_CONVERTER_SELECT)
    private val listBtn     = List(5) { Actor() }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aContentImg)
        addListBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun AdvancedGroup.addListBtn() {
        var nx = 0f
        var ny = 284f
        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 168f, 134f)

            nx += 16f + 168f
            if (index.inc() % 2 == 0) {
                nx = 0f
                ny -= 16f + 134f
            }

            btn.setOnClickListener {
                screen.animHideScreen {
                    GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.entries[index]
                    gdxGame.navigationManager.navigate(ConverterScreen::class.java.name, screen::class.java.name)
                }
            }
        }
    }

}