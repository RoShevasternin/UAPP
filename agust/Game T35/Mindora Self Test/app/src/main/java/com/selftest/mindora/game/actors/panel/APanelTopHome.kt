package com.selftest.mindora.game.actors.panel

import com.selftest.mindora.game.actors.button.base.AButtonAnim
import com.selftest.mindora.game.actors.button.base.AButtonStyles
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen

class APanelTopHome(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelRBX = APanelLumens(screen)
    private val aSettBtn  = AButtonAnim(screen, AButtonStyles.Anim.SETTINGS)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onPanelRBX = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelRBX()
        addSettBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelRBX() {
        aPanelRBX.setSize(185f, 48f)
        add(aPanelRBX) { startToStart(); centerY() }

        aPanelRBX.setOnClickListener { onPanelRBX() }
    }

    private fun addSettBtn() {
        aSettBtn.setSize(48f, 48f)
        add(aSettBtn) { endToEnd(); centerY() }
        //aSettBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) } }
    }

}