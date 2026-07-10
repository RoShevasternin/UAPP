package com.rbxrush.rushrbx.game.screens.home.converter

import com.rbxrush.rushrbx.adsmodule.AdSizeManager
import com.rbxrush.rushrbx.game.actors.button.AYellowButton
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.actors.panel.APanelTop
import com.rbxrush.rushrbx.game.actors.panel.converter.APanelConverter
import com.rbxrush.rushrbx.game.actors.panel.converter.APanelConverter.Type
import com.rbxrush.rushrbx.game.utils.Block
import com.rbxrush.rushrbx.game.utils.TIME_ANIM_SCREEN
import com.rbxrush.rushrbx.game.utils.actor.animDelay
import com.rbxrush.rushrbx.game.utils.actor.animHide
import com.rbxrush.rushrbx.game.utils.actor.animShow
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE
import com.rbxrush.rushrbx.game.utils.runGDX
import kotlinx.coroutines.launch

class ConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelConverter by lazy { APanelConverter(this) }
    private val aConvertBtn     by lazy { AYellowButton(this, "CONVERT NOW") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
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

        aPanelTop.setTitle(GLOBAL_SELECTED_CONVERTER_TYPE.title)
    }

    private fun AConstraintLayout.addPanelConverter() {
        aPanelConverter.setSize(344f, 289f)
        add(aPanelConverter) { centerX(); topToBottom(aPanelTop, 16f) }

        aPanelConverter.onInput = {
            aConvertBtn.enable()
        }
    }

    private fun AConstraintLayout.addConvertBtn() {
        aConvertBtn.setSize(344f, 56f)
        add(aConvertBtn) { centerX(); bottomToBottom(margin = 30f) }
        aConvertBtn.disable()

        aConvertBtn.setOnClickListener {
            aPanelConverter.state = when(aPanelConverter.state) {
                Type.CONVERT_NOW -> {
                    aPanelConverter.calculate()
                    aConvertBtn.label.setText("CONVERT AGAIN")
                    Type.CONVERT_AGAIN
                }
                Type.CONVERT_AGAIN -> {
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