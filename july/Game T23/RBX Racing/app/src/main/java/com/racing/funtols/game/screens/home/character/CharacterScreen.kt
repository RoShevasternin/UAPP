package com.racing.funtols.game.screens.home.character

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.panel.character.APanelCharacter
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.actors.panel.APanelTop
import com.racing.funtols.game.utils.Block
import com.racing.funtols.game.utils.TIME_ANIM_SCREEN
import com.racing.funtols.game.utils.actor.animHide
import com.racing.funtols.game.utils.actor.animShow
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.global.GLOBAL_LIST_CHARACTER_DESCRIPTIONS
import com.racing.funtols.game.utils.global.GLOBAL_LIST_CHARACTER_NAMES
import com.racing.funtols.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class CharacterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontBarlow_Regular, 14f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelCharacter by lazy { APanelCharacter(this) }
    private val aDescLbl        by lazy { AMsdfLabel(GLOBAL_LIST_CHARACTER_DESCRIPTIONS[currentIndex], styleDef) }

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

        rootConstraintLayout.color.a = 0f

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
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 32f)
        add(aPanelTop) { centerX(); topToTop(margin = 16f) }

        aPanelTop.setTitle(GLOBAL_LIST_CHARACTER_NAMES[currentIndex])
    }

    private fun AConstraintLayout.addPanelCharacter() {
        aPanelCharacter.setSize(344f, 144f)
        add(aPanelCharacter) { centerX(); topToBottom(aPanelTop, 24f) }
    }

    private fun AConstraintLayout.addDescLbl() {
        aDescLbl.setSize(344f, 160f)
        add(aDescLbl) { centerX(aPanelCharacter); topToBottom(aPanelCharacter, 24f) }
        aDescLbl.wrap = true
        aDescLbl.setAlignment(Align.top, Align.center)
    }

}