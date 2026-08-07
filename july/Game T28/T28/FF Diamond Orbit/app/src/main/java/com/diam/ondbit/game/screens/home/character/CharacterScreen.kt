package com.diam.ondbit.game.screens.home.character

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.APanelTop
import com.diam.ondbit.game.utils.Block
import com.diam.ondbit.game.utils.TIME_ANIM_SCREEN
import com.diam.ondbit.game.utils.actor.animHide
import com.diam.ondbit.game.utils.actor.animShow
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.global.GLOBAL_LIST_CHARACTER_DESCRIPTIONS
import com.diam.ondbit.game.utils.global.GLOBAL_LIST_CHARACTER_NAMES
import com.diam.ondbit.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class CharacterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontSpaceGrotesk_Medium, 16f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelCharacter by lazy { Image(gdxGame.assetsAll.listBigCharacters[currentIndex]) }
    private val aDescLbl        by lazy { AMsdfLabel(GLOBAL_LIST_CHARACTER_DESCRIPTIONS[currentIndex], styleDef) }

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val currentIndex = GLOBAL_SELECTED_CHARACTER_INDEX

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        //val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBottomUI))
        //gdxGame.activity.showNativeAt(coords.y)

        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

//    override fun hide() {
//        super.hide()
//        gdxGame.activity.hideNative()
//    }

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
        aPanelCharacter.setSize(344f, 214f)
        add(aPanelCharacter) { centerX(); topToBottom(aPanelTop, 24f) }
    }

    private fun AConstraintLayout.addDescLbl() {
        aDescLbl.setSize(344f, 272f)
        add(aDescLbl) { centerX(aPanelCharacter); topToBottom(aPanelCharacter, 16f) }
        aDescLbl.wrap = true
        aDescLbl.setAlignment(Align.top, Align.center)
    }

}