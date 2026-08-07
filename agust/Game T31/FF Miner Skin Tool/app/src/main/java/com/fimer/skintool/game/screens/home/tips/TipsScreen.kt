package com.fimer.skintool.game.screens.home.tips

import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.actors.panel.APanelTop
import com.fimer.skintool.game.actors.panel.tips.APanelTips
import com.fimer.skintool.game.data.TipsData
import com.fimer.skintool.game.utils.Block
import com.fimer.skintool.game.utils.TIME_ANIM_SCREEN
import com.fimer.skintool.game.utils.actor.animHide
import com.fimer.skintool.game.utils.actor.animShow
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.global.GLOBAL_TIPS_INDEX

class TipsScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTop    by lazy { APanelTop(this) }
    private val aSelect by lazy { APanelTips(this) }

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
        addTop()
        addSelect()
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

    private fun AConstraintLayout.addTop() {
        aTop.setSize(344f, 32f)
        add(aTop) { centerX(); topToTop(margin = 16f) }

        aTop.setTitle(TipsData.items()[GLOBAL_TIPS_INDEX].name)
    }

    private fun AConstraintLayout.addSelect() {
        aSelect.setSize(344f, 1f)
        add(aSelect) { centerX(); topToBottom(aTop, 16f); }
    }

}