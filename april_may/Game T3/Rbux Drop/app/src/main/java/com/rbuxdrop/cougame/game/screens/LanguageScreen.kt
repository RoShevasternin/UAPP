package com.rbuxdrop.cougame.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.ATmpGroup
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.actors.panel.APanelLanguage
import com.rbuxdrop.cougame.game.utils.Block
import com.rbuxdrop.cougame.game.utils.TIME_ANIM_SCREEN
import com.rbuxdrop.cougame.game.utils.actor.animDelay
import com.rbuxdrop.cougame.game.utils.actor.animHide
import com.rbuxdrop.cougame.game.utils.actor.animShow
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter
import com.rbuxdrop.cougame.game.utils.gdxGame

class LanguageScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop      = ALabel(this, "Select Language", Color.WHITE, parameter, fontGenerator_Medium)
    private val aPanelLanguage = APanelLanguage(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, safeBannerUI))
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
        addPanelLanguage()
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
        aPanelTop.setSize(376f, 56f)
        add(aPanelTop) {
            centerX()
            topToTop()
        }

        aPanelTop.setAlignment(Align.center)

        //aPanelTop.setTitle("Select Language")
        //aPanelTop.onBack = { }
    }



    private fun AConstraintLayout.addPanelLanguage() {
        aPanelLanguage.width = 376f

        add(aPanelLanguage) {
            centerX()

            topToBottom(aPanelTop)
            bottomToBottom()

            matchHeight()
        }
    }

}