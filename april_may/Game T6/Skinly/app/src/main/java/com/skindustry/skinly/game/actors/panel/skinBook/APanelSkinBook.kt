package com.skindustry.skinly.game.actors.panel.skinBook

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.skindustry.skinly.game.actors.AScrollPane
import com.skindustry.skinly.game.actors.layout.autoLayout.AAutoLayout
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.actors.panel.blokcy.ACardBlokcy
import com.skindustry.skinly.game.actors.panel.blokcy.APanelPoints
import com.skindustry.skinly.game.screens.HomeSelectScreen
import com.skindustry.skinly.game.utils.GLOBAL_listTitleBlokcy
import com.skindustry.skinly.game.utils.GLOBAL_selectedHomeType
import com.skindustry.skinly.game.utils.SelectedHomeType
import com.skindustry.skinly.game.utils.actor.disable
import com.skindustry.skinly.game.utils.actor.setBounds
import com.skindustry.skinly.game.utils.actor.setOnTouchListener
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter
import com.skindustry.skinly.game.utils.gdxGame

class APanelSkinBook(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(20)

    private val labelStyle = FontFactory.create(screen, parameter, screen.fontGenerator_SemiBold, Color.BLACK)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listLbl   = List(3) { Label(GLOBAL_listTitleBlokcy[it], labelStyle) }
    private val listPanel = List(3) { APanelSkinBookHorizontal(screen) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addListLbl()
        addListPanel()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addListLbl() {
        val listBounds = listOf(
            Rectangle(16f, 544f, 70f, 28f),
            Rectangle(16f, 348f, 70f, 28f),
            Rectangle(16f, 152f, 70f, 28f),
        )
        listLbl.forEachIndexed { index, lbl ->
            addActor(lbl)
            lbl.setBounds(listBounds[index])
        }
    }

    private fun addListPanel() {
        val listBounds = listOf(
            Rectangle(0f, 392f, width, 144f),
            Rectangle(0f, 196f, width, 144f),
            Rectangle(0f, 0f, width, 144f),
        )
        val listSB = listOf(
            gdxGame.assetsAll.listSB1,
            gdxGame.assetsAll.listSB2,
            gdxGame.assetsAll.listSB3,
        )
        listPanel.forEachIndexed { index, panel ->
            addActor(panel)
            panel.setBounds(listBounds[index])
            panel.setCards(listSB[index])
        }
    }


}