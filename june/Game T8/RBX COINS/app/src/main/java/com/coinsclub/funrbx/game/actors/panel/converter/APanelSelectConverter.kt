package com.coinsclub.funrbx.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coinsclub.funrbx.game.screens.home.converter.ConverterScreen
import com.coinsclub.funrbx.game.utils.actor.setOnClickListener
import com.coinsclub.funrbx.game.utils.advanced.AdvancedGroup
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.global.ConverterType
import com.coinsclub.funrbx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

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
        //var nx = 0f
        var ny = 358f
        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(0f, ny, 344f, 81f)

            ny -= 8f + 81f
//            if (index.inc() % 2 == 0) {
//                nx = 0f
//                ny -= 16f + 134f
//            }

            btn.setOnClickListener {
                screen.animHideScreen {
                    GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.entries[index]
                    gdxGame.navigationManager.navigate(ConverterScreen::class.java.name, screen::class.java.name)
                }
            }
        }
    }

}