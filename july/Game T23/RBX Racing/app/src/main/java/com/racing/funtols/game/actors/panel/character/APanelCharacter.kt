package com.racing.funtols.game.actors.panel.character

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class APanelCharacter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val currentIndex = GLOBAL_SELECTED_CHARACTER_INDEX

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg = Image(gdxGame.assetsAll.BIG_CHAR)
    private val aImg   = Image(gdxGame.assetsAll.listCharacter[currentIndex])

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        aImg.setSize(100f, 105f)
        add(aImg) { center() }
    }

}