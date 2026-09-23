package com.skindustry.skinly.game.actors.panel

import com.skindustry.skinly.game.actors.coin.APanelBalance
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.skindustry.skinly.game.actors.button.base.AButtonAnim
import com.skindustry.skinly.game.actors.button.base.AButtonStyles
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.screens.SettingsScreen
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class APanelTopHome(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg     = Image(gdxGame.assetsAll.logo)
    private val aSettingsBtn = AButtonAnim(screen, AButtonStyles.Anim.SETTINGS)
    private val aBalance     = APanelBalance(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addSettingsBtn()
        addBalance()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLogoImg() {
        aLogoImg.setSize(124f, 47f)
        add(aLogoImg) { startToStart(margin = 12f); topToTop(margin = 6f) }
    }

    // Баланс монет — між логотипом і налаштуваннями, по центру кнопки налаштувань
    private fun addBalance() {
        aBalance.setSize(APanelBalance.W.toFloat(), APanelBalance.H.toFloat())
        add(aBalance) {
            endToStart(aSettingsBtn, 12f)
            topToTop(aSettingsBtn); bottomToBottom(aSettingsBtn)
        }
    }

    private fun addSettingsBtn() {
        aSettingsBtn.setSize(44f, 44f)
        add(aSettingsBtn) { endToEnd(margin = 12f); topToTop(margin = 4f) }

        aSettingsBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) }
        }
    }

}