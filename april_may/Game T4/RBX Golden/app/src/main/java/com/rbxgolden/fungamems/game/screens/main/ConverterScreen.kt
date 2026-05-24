package com.rbxgolden.fungamems.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.rbxgolden.fungamems.game.actors.button.AGoldenGrayButton
import com.rbxgolden.fungamems.game.actors.button.base.AButtonStyles
import com.rbxgolden.fungamems.game.actors.button.base.AButtonTexture
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.panel.APanelTop
import com.rbxgolden.fungamems.game.actors.panel.converter.AInput
import com.rbxgolden.fungamems.game.actors.panel.converter.APanelResult
import com.rbxgolden.fungamems.game.utils.Block
import com.rbxgolden.fungamems.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.rbxgolden.fungamems.game.utils.TIME_ANIM_SCREEN
import com.rbxgolden.fungamems.game.utils.actor.animDelay
import com.rbxgolden.fungamems.game.utils.actor.animHide
import com.rbxgolden.fungamems.game.utils.actor.animHideAndDisable
import com.rbxgolden.fungamems.game.utils.actor.animShow
import com.rbxgolden.fungamems.game.utils.actor.animShowAndEnable
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame

class ConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop     by lazy { APanelTop(this) }
    private val aInput        by lazy { AInput(this) }
    private val aCountNowBtn  by lazy { AGoldenGrayButton(this, "Count now") }
    private val aPanelResult  by lazy { APanelResult(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var inputValue = 0

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
        aPanelTop.setSize(WIDTH, 56f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle(GLOBAL_SELECTED_CONVERTER_TYPE.title)
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addInput() {
        aInput.setSize(344f, 64f)
        add(aInput) { centerX(); topToBottom(aPanelTop, 16f) }
        aInput.onInput = { value ->
            inputValue = value
            aCountNowBtn.enable()
        }
    }

    private fun AConstraintLayout.addCountNowBtn() {
        aCountNowBtn.setSize(344f, 56f)
        add(aCountNowBtn) { centerX(); topToBottom(aInput, 24f) }

        aCountNowBtn.disable()

        aCountNowBtn.setOnClickListener {
            aCountNowBtn.disable()
            aPanelResult.calculate(inputValue)

            aPanelResult.animShowAndEnable(0.25f)
        }
    }

    private fun AConstraintLayout.addPanelResult() {
        aPanelResult.animHideAndDisable()
        aPanelResult.setSize(344f, 134f)
        add(aPanelResult) { centerX(); topToBottom(aCountNowBtn, 24f) }
    }

}