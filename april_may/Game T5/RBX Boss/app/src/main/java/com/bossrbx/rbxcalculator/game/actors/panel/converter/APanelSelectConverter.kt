package com.bossrbx.rbxcalculator.game.actors.panel.converter

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.game.actors.ATmpGroup
import com.bossrbx.rbxcalculator.game.actors.layout.AScrollLayout
import com.bossrbx.rbxcalculator.game.actors.layout.linear.AVerticalGroup
import com.bossrbx.rbxcalculator.game.utils.ConverterType
import com.bossrbx.rbxcalculator.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.bossrbx.rbxcalculator.game.utils.actor.setOnClickListener
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedGroup
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.game.screens.main.converter.ConverterScreen

class APanelSelectConverter(screen: AdvancedScreen): AScrollLayout(screen) {

    override val contentHeight = 572f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentGroup  = ATmpGroup(screen)
    private val aContentImg    = Image(gdxGame.assetsAll.PANEL_SELECT_CONVERTER)
    private val listBtn        = List(7) { Actor() }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun AVerticalGroup.addContent() {
        addContentGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    // Content Group start ------------------------------------------------------------------------

    private fun AVerticalGroup.addContentGroup() {
        aContentGroup.setSize(344f, contentHeight)
        addActor(aContentGroup)

        aContentGroup.also {
            it.addAndFillActor(aContentImg)
            it.addListBtn()
        }
    }

    private fun AdvancedGroup.addListBtn() {
        var ny = 504f
        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(0f, ny, 344f, 68f)

            ny -= 16f + 68f

            btn.setOnClickListener {
                screen.animHideScreen {
                    GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.entries[index]
                    gdxGame.navigationManager.navigate(ConverterScreen::class.java.name, screen::class.java.name)
                }
            }
        }
    }

}