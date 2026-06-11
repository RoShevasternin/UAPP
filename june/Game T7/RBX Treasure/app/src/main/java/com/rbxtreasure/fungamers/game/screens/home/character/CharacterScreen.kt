package com.rbxtreasure.fungamers.game.screens.home.character

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.actors.panel.APanelTop
import com.rbxtreasure.fungamers.game.actors.panel.character.APanelCharacter
import com.rbxtreasure.fungamers.game.utils.Block
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.TIME_ANIM_SCREEN
import com.rbxtreasure.fungamers.game.utils.actor.animDelay
import com.rbxtreasure.fungamers.game.utils.actor.animHide
import com.rbxtreasure.fungamers.game.utils.actor.animShow
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.font.FontFactory
import com.rbxtreasure.fungamers.game.utils.font.FontParameter
import com.rbxtreasure.fungamers.game.utils.gdxGame
import com.rbxtreasure.fungamers.game.utils.global.GLOBAL_LIST_CHARACTER_DESCRIPTIONS
import com.rbxtreasure.fungamers.game.utils.global.GLOBAL_LIST_CHARACTER_NAMES
import com.rbxtreasure.fungamers.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class CharacterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(12)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelCharacter by lazy { APanelCharacter(this) }
    private val aDescLbl        by lazy { Label(GLOBAL_LIST_CHARACTER_DESCRIPTIONS[currentIndex], FontFactory.create(this, parameter, fontGenerator_AlanSans_Medium, GameColor.beige_E2CEAA)) }

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val currentIndex = GLOBAL_SELECTED_CHARACTER_INDEX

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

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
        aPanelTop.height = 56f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle(GLOBAL_LIST_CHARACTER_NAMES[currentIndex])
    }

    private fun AConstraintLayout.addPanelCharacter() {
        aPanelCharacter.setSize(344f, 230f)
        add(aPanelCharacter) { centerX(); topToBottom(aPanelTop, 16f) }
    }

    private fun AConstraintLayout.addDescLbl() {
        aDescLbl.setSize(344f, 154f)
        add(aDescLbl) { centerX(); topToBottom(aPanelCharacter, 16f) }
        aDescLbl.wrap = true
        aDescLbl.setAlignment(Align.center)
    }

}