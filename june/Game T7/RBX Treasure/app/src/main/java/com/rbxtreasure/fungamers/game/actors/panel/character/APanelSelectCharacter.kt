package com.rbxtreasure.fungamers.game.actors.panel.character

import com.badlogic.gdx.math.Vector2
import com.rbxtreasure.fungamers.game.actors.ACard
import com.rbxtreasure.fungamers.game.actors.layout.AScrollLayout
import com.rbxtreasure.fungamers.game.actors.layout.autoLayout.AAutoLayout
import com.rbxtreasure.fungamers.game.screens.home.character.CharacterScreen
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.actor.setOnClickListener
import com.rbxtreasure.fungamers.game.utils.actor.setOnTouchListener
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.font.FontFactory
import com.rbxtreasure.fungamers.game.utils.font.FontParameter
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.game.utils.global.GLOBAL_LIST_CHARACTER_NAMES
import com.rbxtreasure.fungamers.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class APanelSelectCharacter(override val screen: AdvancedScreen): AScrollLayout(screen, 8f, 20f) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(20)

    private val labelStyle = FontFactory.create(screen, parameter, screen.fontGenerator_Anton_Regular, GameColor.beige_E2CEAA)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val textureSize = Vector2(135f, 135f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listCards = List(7) { ACard(screen, GLOBAL_LIST_CHARACTER_NAMES[it], labelStyle, gdxGame.assetsAll.listCharacter[it], textureSize) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun AAutoLayout.addContent() {
        addListCards()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun AAutoLayout.addListCards() {
        listCards.forEachIndexed { index, card ->
            card.setSize(344f, 94f)
            add(card)

            card.setOnTouchListener {
                screen.animHideScreen {
                    GLOBAL_SELECTED_CHARACTER_INDEX = index
                    gdxGame.navigationManager.navigate(CharacterScreen::class.java.name, screen::class.java.name)
                }
            }
        }
    }

}