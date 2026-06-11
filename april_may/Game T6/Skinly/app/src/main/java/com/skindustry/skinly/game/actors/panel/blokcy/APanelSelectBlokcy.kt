package com.skindustry.skinly.game.actors.panel.blokcy

import com.badlogic.gdx.graphics.Color
import com.skindustry.skinly.game.actors.AScrollPane
import com.skindustry.skinly.game.actors.layout.autoLayout.AAutoLayout
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.screens.HomeSelectScreen
import com.skindustry.skinly.game.utils.GLOBAL_listTitleBlokcy
import com.skindustry.skinly.game.utils.GLOBAL_selectedHomeType
import com.skindustry.skinly.game.utils.SelectedHomeType
import com.skindustry.skinly.game.utils.actor.disable
import com.skindustry.skinly.game.utils.actor.setOnTouchListener
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter
import com.skindustry.skinly.game.utils.gdxGame

class APanelSelectBlokcy(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(36)

    private val labelStyle = FontFactory.create(screen, parameter, screen.fontGenerator_Black, Color.BLACK)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listCard = List(3) { ACardBlokcy(screen, GLOBAL_listTitleBlokcy[it], labelStyle, gdxGame.assetsAll.listBlokcy[it]) }
    private val aHorizontal = AAutoLayout(
        screen       = screen,
        direction    = AAutoLayout.Direction.HORIZONTAL,
        sizingW      = AAutoLayout.Sizing.HUG,
        paddingStart = 16f,
        paddingEnd   = 16f,
        gapMain      = 8f,
    )
    private val aScrollPane = AScrollPane(aHorizontal, scrollX = true, scrollY = false)

    private val aPanelPoints = APanelPoints(screen)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var lastCardIndex = -1

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addScrollPane()
        setUpHorizontal()
        addPanelPoints()
    }

    override fun act(delta: Float) {
        super.act(delta)
        updatePoints()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addScrollPane() {
        aScrollPane.setSize(width, 466f)
        add(aScrollPane) { centerX(); topToTop() }
    }

    private fun setUpHorizontal() {
        aHorizontal.setSize(width, aScrollPane.height)

        listCard.forEachIndexed { index, card ->
            card.setSize(328f, 466f)
            aHorizontal.add(card)

            card.setOnTouchListener {
                GLOBAL_selectedHomeType = SelectedHomeType.entries[index]
                screen.animHideScreen { gdxGame.navigationManager.navigate(HomeSelectScreen::class.java.name, screen::class.java.name) }
            }
        }
    }

    private fun addPanelPoints() {
        aPanelPoints.setSize(68f, 12f)
        add(aPanelPoints) { centerX(); bottomToBottom(margin = 8f) }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun updatePoints() {
        val cardWidth = 328f + 8f // ширина карти + gap
        val scrollX   = aScrollPane.scrollX
        val index     = (scrollX / cardWidth + 0.5f).toInt().coerceIn(0, listCard.size - 1)

        if (index != lastCardIndex) {
            lastCardIndex = index
            aPanelPoints.setActive(index)
        }
    }

}