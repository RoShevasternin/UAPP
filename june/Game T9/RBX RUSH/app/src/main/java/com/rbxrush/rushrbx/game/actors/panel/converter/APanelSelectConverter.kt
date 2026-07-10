package com.rbxrush.rushrbx.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxrush.rushrbx.game.screens.home.converter.ConverterScreen
import com.rbxrush.rushrbx.game.utils.actor.setOnClickListener
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedGroup
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame
import com.rbxrush.rushrbx.game.utils.global.ConverterType
import com.rbxrush.rushrbx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

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
        var ny = 254f
        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 168f, 119f)

            nx += 8f + 168f
            if (index.inc() % 2 == 0) {
                nx = 0f
                ny -= 8f + 119f
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