package com.rbuxdrop.cougame.game.screens

import com.rbuxdrop.cougame.businesModule.backend.Bt
import com.rbuxdrop.cougame.game.utils.Onboarding
import com.rbuxdrop.cougame.game.actors.ATmpGroup
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.actors.panel.APanelMain
import com.rbuxdrop.cougame.game.actors.panel.APanelTopMain
import com.rbuxdrop.cougame.game.utils.Block
import com.rbuxdrop.cougame.game.utils.TIME_ANIM_SCREEN
import com.rbuxdrop.cougame.game.utils.actor.animDelay
import com.rbuxdrop.cougame.game.utils.actor.animHide
import com.rbuxdrop.cougame.game.utils.actor.animShow
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.services.analytics.AnalyticsManager

class MainScreen: AdvancedScreen() {

    override val analyticsBt    = Bt.HUB
    override val analyticsBlock = "main_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  = APanelTopMain(this)
    private val aPanelMain = APanelMain(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    // Нативки в меню немає навмисно: вона перекриває плитки механік
    // (рішення користувача). Банер лишається.
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        Onboarding.markDone()   // дійшов до меню = онбординг пройдено
        animShowScreen { AnalyticsManager.openHomeScreen() }
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelMain()
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
        aPanelTop.setSize(WIDTH, 80f)
        add(aPanelTop) {
            centerX()
            topToTop()
        }
    }

    private fun AConstraintLayout.addPanelMain() {
        aPanelMain.width = WIDTH
        add(aPanelMain) {
            centerX()
            topToBottom(aPanelTop); bottomToBottom()

            matchHeight()
        }
    }

}