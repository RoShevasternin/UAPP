package com.mon.sterbx.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.mon.sterbx.game.screens.home.converter.ConverterScreen
import com.mon.sterbx.game.utils.actor.setOnClickListener
import com.mon.sterbx.game.utils.advanced.AdvancedGroup
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.global.ConverterType
import com.mon.sterbx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

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
        var ny = 396f
        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 168f, 190f)

            nx += 8f + 168f
            if (index.inc() % 2 == 0) {
                nx = 0f
                ny -= 8f + 190f
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