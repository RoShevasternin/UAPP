package com.sakurbx.fungambx.game.actors.panel.character

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.global.GLOBAL_LIST_CHARACTER_NAMES
import com.sakurbx.fungambx.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class APanelCharacter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(24)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Laila_Bold)

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
        aBgImg.setSize(344f, 163f)
        add(aBgImg) { centerX(); topToTop() }

        addLbl()
        addImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLbl() {
        aLbl.setSize(344f, 26f)
        add(aLbl) { centerX(); bottomToBottom() }
        aLbl.setAlignment(Align.center)
        aLbl.setEllipsis(true)
    }

    private fun addImg() {
        aImg.setSize(182f, 182f)
        add(aImg) { centerX(); topToTop(margin = -9f) }
    }

}