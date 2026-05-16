package com.rbuxdrop.cougame.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxdrop.cougame.game.actors.button.base.AButtonStyles
import com.rbuxdrop.cougame.game.actors.button.base.AButtonTexture
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.actors.panel.APanelTop
import com.rbuxdrop.cougame.game.actors.panel.converter.AInput
import com.rbuxdrop.cougame.game.actors.panel.converter.APanelResult
import com.rbuxdrop.cougame.game.utils.Block
import com.rbuxdrop.cougame.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.rbuxdrop.cougame.game.utils.TIME_ANIM_SCREEN
import com.rbuxdrop.cougame.game.utils.actor.animDelay
import com.rbuxdrop.cougame.game.utils.actor.animHide
import com.rbuxdrop.cougame.game.utils.actor.animHideAndDisable
import com.rbuxdrop.cougame.game.utils.actor.animShow
import com.rbuxdrop.cougame.game.utils.actor.animShowAndEnable
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame

class ConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop     = APanelTop(this)
    private val aInput        = AInput(this)
    private val aCountNowBtn  = AButtonTexture(this, AButtonStyles.Texture.COUNT_NOW)
    private val aSeparatorImg = Image(gdxGame.assetsAll.SEPARATOR)
    private val aPanelResult  = APanelResult(this)

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
        addSeparatorImg()
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
        add(aInput) { centerX(); topToBottom(aPanelTop, 12f) }
        aInput.onInput = { value ->
            inputValue = value
            aCountNowBtn.enable()
        }
    }

    private fun AConstraintLayout.addCountNowBtn() {
        aCountNowBtn.setSize(344f, 60f)
        add(aCountNowBtn) { centerX(); topToBottom(aInput, 12f) }

        aCountNowBtn.disable()

        aCountNowBtn.setOnClickListener {
            aCountNowBtn.disable()
            aPanelResult.calculate(inputValue)

            aSeparatorImg.animShowAndEnable(0.25f)
            aPanelResult.animShowAndEnable(0.25f)
        }
    }

    private fun AConstraintLayout.addSeparatorImg() {
        aSeparatorImg.animHideAndDisable()
        aSeparatorImg.setSize(344f, 24f)
        add(aSeparatorImg) { centerX(); topToBottom(aCountNowBtn, 12f) }
    }

    private fun AConstraintLayout.addPanelResult() {
        aPanelResult.animHideAndDisable()
        aPanelResult.setSize(344f, 98f)
        add(aPanelResult) { centerX(); topToBottom(aSeparatorImg, 12f) }
    }

}