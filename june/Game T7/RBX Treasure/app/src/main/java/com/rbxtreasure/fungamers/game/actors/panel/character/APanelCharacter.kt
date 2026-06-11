package com.rbxtreasure.fungamers.game.actors.panel.character

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.font.FontFactory
import com.rbxtreasure.fungamers.game.utils.font.FontParameter
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.game.utils.global.GLOBAL_LIST_CHARACTER_NAMES
import com.rbxtreasure.fungamers.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class APanelCharacter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(50)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val currentIndex = GLOBAL_SELECTED_CHARACTER_INDEX

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg = Image(gdxGame.assetsAll.CHAR_BIG_CARD)
    private val aLbl   = Label(GLOBAL_LIST_CHARACTER_NAMES[currentIndex], FontFactory.create(screen, parameter, screen.fontGenerator_AlanSans_Bold, GameColor.yellow_DDA334))
    private val aImg   = Image(gdxGame.assetsAll.listCharacter[currentIndex])

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        addLbl()
        addImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLbl() {
        aLbl.setSize(317f, 43f)
        add(aLbl) { centerX(); bottomToBottom(margin = 22f) }
        aLbl.setAlignment(Align.center)
        aLbl.setEllipsis(true)
    }

    private fun addImg() {
        aImg.setSize(135f, 135f)
        add(aImg) { centerX(); topToTop(margin = 16f) }
    }

}