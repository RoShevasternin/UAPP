package com.mon.sterbx.game.screens.home.converter

import com.mon.sterbx.adsmodule.AdSizeManager
import com.mon.sterbx.game.actors.button.AOrangeButton
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.actors.panel.APanelTop
import com.mon.sterbx.game.actors.panel.converter.APanelConverter
import com.mon.sterbx.game.actors.panel.converter.APanelConverter.Type
import com.mon.sterbx.game.utils.Block
import com.mon.sterbx.game.utils.TIME_ANIM_SCREEN
import com.mon.sterbx.game.utils.actor.animDelay
import com.mon.sterbx.game.utils.actor.animHide
import com.mon.sterbx.game.utils.actor.animShow
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE
import com.mon.sterbx.game.utils.runGDX
import kotlinx.coroutines.launch

class ConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelConverter by lazy { APanelConverter(this) }
    private val aConvertBtn     by lazy { AOrangeButton(this, "CONVERT NOW") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsAll.BACKGROUND_YELLOW)
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
        aPanelTop.setSize(WIDTH, 42f)
        add(aPanelTop) { centerX(); topToTop(margin = 12f) }

        aPanelTop.setTitle(GLOBAL_SELECTED_CONVERTER_TYPE.title)
    }

    private fun AConstraintLayout.addPanelConverter() {
        aPanelConverter.setSize(344f, 97f)
        add(aPanelConverter) { centerX(); topToBottom(aPanelTop, 16f) }

        aPanelConverter.onInput = {
            aConvertBtn.enable()
        }
    }

    private fun AConstraintLayout.addConvertBtn() {
        aConvertBtn.setSize(344f, 64f)
        add(aConvertBtn) { centerX(); bottomToBottom(margin = 36f) }
        aConvertBtn.disable()

        aConvertBtn.setOnClickListener {
            aPanelConverter.state = when(aPanelConverter.state) {
                Type.CONVERT_NOW -> {
                    aPanelConverter.calculate()
                    aConvertBtn.label.setText("CONVERT AGAIN")
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

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aConvertBtn) { marginBottom = screen.adBottomUI + 36f } } } }
    }

}