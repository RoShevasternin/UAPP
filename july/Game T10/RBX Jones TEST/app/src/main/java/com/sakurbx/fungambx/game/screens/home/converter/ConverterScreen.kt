package com.sakurbx.fungambx.game.screens.home.converter

import com.sakurbx.fungambx.adsmodule.AdSizeManager
import com.sakurbx.fungambx.game.actors.button.APinkButton
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.APanelTop
import com.sakurbx.fungambx.game.actors.panel.converter.APanelConverter
import com.sakurbx.fungambx.game.actors.panel.converter.APanelConverter.Type
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.actor.animDelay
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE
import com.sakurbx.fungambx.game.utils.runGDX
import kotlinx.coroutines.launch

class ConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelConverter by lazy { APanelConverter(this) }
    private val aConvertBtn     by lazy { APinkButton(this, "CONVERT NOW") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsAll.BACKGROUND_PUPRLE)
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
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN)
        rootConstraintLayout.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN)
        rootConstraintLayout.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 56f)
        add(aPanelTop) { centerX(); topToTop(margin = 8f) }

        aPanelTop.setTitle(GLOBAL_SELECTED_CONVERTER_TYPE.title)
    }

    private fun AConstraintLayout.addPanelConverter() {
        aPanelConverter.setSize(344f, 318f)
        add(aPanelConverter) { centerX(); topToBottom(aPanelTop, 16f) }

        aPanelConverter.onInput = { aConvertBtn.enable() }
    }

    private fun AConstraintLayout.addConvertBtn() {
        aConvertBtn.setSize(344f, 57f)
        add(aConvertBtn) { centerX(); bottomToBottom(margin = 32f) }
        aConvertBtn.disable()

        aConvertBtn.setOnClickListener {
            aPanelConverter.state = when(aPanelConverter.state) {
                Type.CONVERT_NOW -> {
                    aPanelConverter.calculate()
                    aConvertBtn.label.setText("CLEAR")
                    Type.CLEAR
                }
                Type.CLEAR -> {
                    aPanelConverter.clearInput()
                    aConvertBtn.disable()
                    aConvertBtn.label.setText("CONVERT NOW")
                    Type.CONVERT_NOW
                }
            }
        }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aConvertBtn) { marginBottom += screen.adBottomUI } } } }
    }

}