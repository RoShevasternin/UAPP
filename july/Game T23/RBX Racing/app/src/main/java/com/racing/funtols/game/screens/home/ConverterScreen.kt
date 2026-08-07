package com.racing.funtols.game.screens.home

import com.racing.funtols.adsmodule.AdSizeManager
import com.racing.funtols.game.actors.button.ARedButton
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.actors.panel.APanelTop
import com.racing.funtols.game.actors.panel.converter.APanelConverter
import com.racing.funtols.game.utils.Block
import com.racing.funtols.game.utils.TIME_ANIM_SCREEN
import com.racing.funtols.game.utils.actor.animHide
import com.racing.funtols.game.utils.actor.animShow
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.runGDX
import kotlinx.coroutines.launch

class ConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelConverter by lazy { APanelConverter(this) }
    private val aConvertBtn     by lazy { ARedButton(this, "CONVERT") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelConverter()
        addConvertBtn()
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

        aPanelTop.setTitle("RBX CONVERTER")
    }

    private fun AConstraintLayout.addPanelConverter() {
        aPanelConverter.setSize(344f, 527f)
        add(aPanelConverter) { centerX(); topToBottom(aPanelTop, 24f) }

        aPanelConverter.onInput = { aConvertBtn.enable() }
    }

    private fun AConstraintLayout.addConvertBtn() {
        aConvertBtn.setSize(344f, 52f)
        add(aConvertBtn) { centerX(); bottomToBottom(margin = 42f) }
        aConvertBtn.disable()

        aConvertBtn.setOnClickListener {
            aPanelConverter.calculate()
        }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aConvertBtn) { marginBottom = screen.adBottomUI + 42f } } } }
    }

}