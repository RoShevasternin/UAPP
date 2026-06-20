package com.treprosure.starbxup.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.treprosure.starbxup.game.actors.button.base.AButtonAnim
import com.treprosure.starbxup.game.actors.button.base.AButtonStyles
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.screens.SettingsScreen
import com.treprosure.starbxup.game.utils.GameColor
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.font.FontFactory
import com.treprosure.starbxup.game.utils.font.FontParameter
import com.treprosure.starbxup.game.utils.gdxGame

class APanelTop(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(18)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackBtn  = AButtonAnim(screen, AButtonStyles.Anim.BACK)
    private val aTitleLbl = Label("", FontFactory.create(screen, parameter, screen.fontGenerator_Anton_Regular, GameColor.beige_C9B797))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addTitleLbl()
        addBackBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBackBtn() {
        aBackBtn.setSize(50f, 50f)
        add(aBackBtn) { startToStart(margin = 3f); centerY() }
        aBackBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(103f, 24f)
        add(aTitleLbl) { center() }
        aTitleLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

}