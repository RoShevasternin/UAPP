package com.bossrbx.rbxcalculator.game.screens.main.converter

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.game.actors.button.ABlueButton
import com.bossrbx.rbxcalculator.game.actors.button.base.AButtonStyles
import com.bossrbx.rbxcalculator.game.actors.button.base.AButtonTexture
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.actors.panel.APanelTop
import com.bossrbx.rbxcalculator.game.actors.panel.converter.AInput
import com.bossrbx.rbxcalculator.game.utils.Block
import com.bossrbx.rbxcalculator.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.bossrbx.rbxcalculator.game.utils.TIME_ANIM_SCREEN
import com.bossrbx.rbxcalculator.game.utils.actor.animDelay
import com.bossrbx.rbxcalculator.game.utils.actor.animHide
import com.bossrbx.rbxcalculator.game.utils.actor.animHideAndDisable
import com.bossrbx.rbxcalculator.game.utils.actor.animShow
import com.bossrbx.rbxcalculator.game.utils.actor.animShowAndEnable
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.game.actors.panel.converter.APanelResult

class ConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop     by lazy { APanelTop(this) }
    private val aInput        by lazy { AInput(this) }
    private val aCountNowBtn  by lazy { ABlueButton(this, "Count now") }
    private val aPanelResult  by lazy { APanelResult(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var inputValue = 0

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
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
        addInput()
        addCountNowBtn()
        addPanelResult()
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
        aPanelTop.setSize(WIDTH, 64f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle(GLOBAL_SELECTED_CONVERTER_TYPE.title)
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addInput() {
        aInput.setSize(344f, 64f)
        add(aInput) { centerX(); topToBottom(aPanelTop, 12f) }
        aInput.onInput = { value ->
            inputValue = value
            aCountNowBtn.enable()
        }
    }

    private fun AConstraintLayout.addCountNowBtn() {
        aCountNowBtn.setSize(344f, 64f)
        add(aCountNowBtn) { centerX(); topToBottom(aInput, 12f) }

        aCountNowBtn.disable()

        aCountNowBtn.setOnClickListener {
            aCountNowBtn.disable()
            aPanelResult.calculate(inputValue)

            gdxGame.soundUtil.apply { play(WIN) }
            aPanelResult.animShowAndEnable(0.25f)
        }
    }

    private fun AConstraintLayout.addPanelResult() {
        aPanelResult.animHideAndDisable()
        aPanelResult.setSize(344f, 112f)
        add(aPanelResult) { centerX(); topToBottom(aCountNowBtn, 12f) }
    }

}