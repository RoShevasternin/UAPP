package com.rbxtreasure.fungamers.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxtreasure.fungamers.game.actors.button.base.AButtonAnim
import com.rbxtreasure.fungamers.game.actors.button.base.AButtonStyles
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.screens.SettingsScreen
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame

class APanelTopHome(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg = Image(gdxGame.assetsAll.logo)
    private val aSettBtn = AButtonAnim(screen, AButtonStyles.Anim.SETTINGS)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addSettBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLogoImg() {
        aLogoImg.setSize(115f, 48f)
        add(aLogoImg) { startToStart(margin = 16f); centerY() }
    }

    private fun addSettBtn() {
        aSettBtn.setSize(40f, 40f)
        add(aSettBtn) { endToEnd(margin = 16f); centerY() }
        aSettBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) } }
    }

}