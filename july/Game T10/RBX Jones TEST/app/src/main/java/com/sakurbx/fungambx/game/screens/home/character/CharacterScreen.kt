package com.sakurbx.fungambx.game.screens.home.character

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.APanelTop
import com.sakurbx.fungambx.game.actors.panel.character.APanelCharacter
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.actor.animDelay
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.global.GLOBAL_LIST_CHARACTER_DESCRIPTIONS
import com.sakurbx.fungambx.game.utils.global.GLOBAL_LIST_CHARACTER_NAMES
import com.sakurbx.fungambx.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class CharacterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val lsDef by lazy { FontFactory.create(this, parameterDef, fontGenerator_Laila_Bold) }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelCharacter by lazy { APanelCharacter(this) }
    private val aDescLbl        by lazy { Label(GLOBAL_LIST_CHARACTER_DESCRIPTIONS[currentIndex], lsDef) }

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val currentIndex = GLOBAL_SELECTED_CHARACTER_INDEX

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBottomUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelCharacter()
        addDescLbl()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.height = 72f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle(GLOBAL_LIST_CHARACTER_NAMES[currentIndex])
    }

    private fun AConstraintLayout.addPanelCharacter() {
        aPanelCharacter.setSize(344f, 197f)
        add(aPanelCharacter) { centerX(); topToBottom(aPanelTop, 16f) }
    }

    private fun AConstraintLayout.addDescLbl() {
        aDescLbl.setSize(344f, 168f)
        add(aDescLbl) { centerX(); topToBottom(aPanelCharacter, 16f) }
        aDescLbl.wrap = true
        aDescLbl.setAlignment(Align.top, Align.center)
    }

}