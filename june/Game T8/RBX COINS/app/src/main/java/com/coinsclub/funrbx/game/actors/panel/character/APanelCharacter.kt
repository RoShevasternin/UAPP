package com.coinsclub.funrbx.game.actors.panel.character

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.global.GLOBAL_LIST_CHARACTER_NAMES
import com.coinsclub.funrbx.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class APanelCharacter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(32)
        .setBorderAndShadow()

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_LuckiestGuy_Regular, GameColor.yellow_DFA008)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val currentIndex = GLOBAL_SELECTED_CHARACTER_INDEX

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg = Image(gdxGame.assetsAll.CHAR_BIG_CARD)
    private val aLbl   = Label(GLOBAL_LIST_CHARACTER_NAMES[currentIndex], lsDef)
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
        aLbl.setSize(344f, 24f)
        add(aLbl) { centerX(); topToBottom(margin = 10f) }
        aLbl.setAlignment(Align.center)
        aLbl.setEllipsis(true)
    }

    private fun addImg() {
        aImg.setSize(180f, 180f)
        add(aImg) { center() }
    }

}