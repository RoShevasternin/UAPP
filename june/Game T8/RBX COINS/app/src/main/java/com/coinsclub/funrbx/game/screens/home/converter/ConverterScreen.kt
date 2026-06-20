package com.coinsclub.funrbx.game.screens.home.converter

import com.coinsclub.funrbx.adsmodule.AdSizeManager
import com.coinsclub.funrbx.game.actors.button.AYellowButton
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.actors.panel.APanelTop
import com.coinsclub.funrbx.game.actors.panel.converter.APanelConverter
import com.coinsclub.funrbx.game.actors.panel.converter.APanelConverter.Type
import com.coinsclub.funrbx.game.utils.Block
import com.coinsclub.funrbx.game.utils.TIME_ANIM_SCREEN
import com.coinsclub.funrbx.game.utils.actor.animDelay
import com.coinsclub.funrbx.game.utils.actor.animHide
import com.coinsclub.funrbx.game.utils.actor.animShow
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE
import com.coinsclub.funrbx.game.utils.runGDX
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
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

        //val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBottomUI))
        //gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        //gdxGame.activity.hideNative()
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
        aPanelConverter.setSize(346f, 346f)
        add(aPanelConverter) { centerX(); topToBottom(aPanelTop, 14f) }

        aPanelConverter.onInput = {
            aConvertBtn.enable()
        }
    }

    private fun AConstraintLayout.addConvertBtn() {
        aConvertBtn.setSize(312f, 51f)
        add(aConvertBtn) { centerX(); bottomToBottom(margin = 30f) }
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