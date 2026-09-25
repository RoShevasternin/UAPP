package com.zahbx.blitzrbx.game.screens

import com.zahbx.blitzrbx.businesModule.backend.Bt
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.actors.layout.linear.AVerticalGroup
import com.zahbx.blitzrbx.game.actors.panel.APanelMain
import com.zahbx.blitzrbx.game.actors.panel.APanelRBX
import com.zahbx.blitzrbx.game.actors.panel.APanelTopLogo
import com.zahbx.blitzrbx.game.utils.Block
import com.zahbx.blitzrbx.game.utils.TIME_ANIM_SCREEN
import com.zahbx.blitzrbx.game.utils.actor.animDelay
import com.zahbx.blitzrbx.game.utils.actor.animHide
import com.zahbx.blitzrbx.game.utils.actor.animShow
import com.zahbx.blitzrbx.game.utils.actor.setOnClickListener
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.gdxGame
import com.zahbx.blitzrbx.services.analytics.AnalyticsManager

class MainScreen: AdvancedScreen() {

    override val analyticsBt    = Bt.HUB
    override val analyticsBlock = "main_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTopLogo = APanelTopLogo(this)
    private val aPanelMain    = APanelMain(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        // Нативки в меню немає навмисно: вона перекриває плитки механік
        // (рішення користувача). Банер лишається.
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen { AnalyticsManager.openHomeScreen() }
    }


    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTopLogo()
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

    private fun AConstraintLayout.addPanelTopLogo() {
        aPanelTopLogo.setSize(376f, 80f)
        add(aPanelTopLogo) {
            centerX()
            topToTop()
        }
    }

    private fun AConstraintLayout.addPanelMain() {
        aPanelMain.width = 376f
        add(aPanelMain) {
            centerX()
            topToBottom(aPanelTopLogo)
            bottomToBottom()

            matchHeight()
        }
    }

}