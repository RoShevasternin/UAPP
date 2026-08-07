package com.diam.ondbit.game.screens.home.converter

import com.diam.ondbit.adsmodule.AdSizeManager
import com.diam.ondbit.game.actors.button.AYellowButton
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.APanelTop
import com.diam.ondbit.game.actors.panel.converter.APanelConverter
import com.diam.ondbit.game.utils.Block
import com.diam.ondbit.game.utils.TIME_ANIM_SCREEN
import com.diam.ondbit.game.utils.actor.animHide
import com.diam.ondbit.game.utils.actor.animShow
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.runGDX
import kotlinx.coroutines.launch

class ConverterScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop       by lazy { APanelTop(this) }
    private val aPanelConverter by lazy { APanelConverter(this) }
    private val aConvertBtn     by lazy { AYellowButton(this, "CONVERT") }

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
        aPanelTop.setSize(344f, 40f)
        add(aPanelTop) { centerX(); topToTop(margin = 16f) }

        aPanelTop.setTitle("DIAMOND CALCULATOR")
    }

    private fun AConstraintLayout.addPanelConverter() {
        aPanelConverter.setSize(344f, 351f)
        add(aPanelConverter) { centerX(); topToBottom(aPanelTop, 24f) }

        aPanelConverter.onInput = { aConvertBtn.enable() }
    }

    private fun AConstraintLayout.addConvertBtn() {
        aConvertBtn.setSize(345f, 62f)
        add(aConvertBtn) { centerX(); bottomToBottom(margin = 40f) }
        aConvertBtn.disable()

        aConvertBtn.setOnClickListener {
            aPanelConverter.calculate()
        }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect {
            runGDX { update(aConvertBtn) { marginBottom = screen.adBottomUI + 40f } }
        } }
    }

}