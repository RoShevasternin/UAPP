package com.diam.ondbit.game.screens.home.converter

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.diam.ondbit.adsmodule.AdSizeManager
import com.diam.ondbit.game.actors.button.AYellowButton
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.APanelTop
import com.diam.ondbit.game.utils.Block
import com.diam.ondbit.game.utils.TIME_ANIM_SCREEN
import com.diam.ondbit.game.utils.actor.animHide
import com.diam.ondbit.game.utils.actor.animShow
import com.diam.ondbit.game.utils.actor.setOnClickListener
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.global.ConverterType
import com.diam.ondbit.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE
import com.diam.ondbit.game.utils.runGDX
import kotlinx.coroutines.launch

class SelectConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop    by lazy { APanelTop(this) }
    private val aPanelSelect by lazy { Image(gdxGame.assetsAll.PANEL_CONVERTER_SELECT) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelSelect()
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
        aPanelTop.setSize(344f, 40f)
        add(aPanelTop) { centerX(); topToTop(margin = 16f) }

        aPanelTop.setTitle("DIAMOND CALCULATOR")
    }

    private fun AConstraintLayout.addPanelSelect() {
        aPanelSelect.setSize(344f, 288f)
        add(aPanelSelect) { centerX(); topToBottom(aPanelTop, 24f) }

        var ny = 0f
        List(3) { Actor() }.forEachIndexed { index, actor ->
            actor.setSize(344f, 92f)
            add(actor) { centerX(aPanelSelect); topToTop(aPanelSelect, ny) }
            ny += 6f + 92f

            actor.setOnClickListener {
                GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.entries[index]
                animHideScreen { gdxGame.navigationManager.navigate(ConverterScreen::class.java.name, SelectConverterScreen::class.java.name) }
            }
        }
    }

}