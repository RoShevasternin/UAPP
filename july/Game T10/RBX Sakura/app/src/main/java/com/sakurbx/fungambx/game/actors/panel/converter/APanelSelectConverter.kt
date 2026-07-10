package com.sakurbx.fungambx.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.sakurbx.fungambx.game.screens.home.converter.ConverterScreen
import com.sakurbx.fungambx.game.utils.actor.setOnClickListener
import com.sakurbx.fungambx.game.utils.advanced.AdvancedGroup
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.global.ConverterType
import com.sakurbx.fungambx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE

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
        var ny = 272f
        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 344f, 60f)

            ny -= 8f + 60f
//            if (index.inc() % 2 == 0) {
//                nx = 0f
//                ny -= 8f + 119f
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