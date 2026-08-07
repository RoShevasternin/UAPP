package com.racing.funtols.game.actors.panel

import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.button.base.AButtonAnim
import com.racing.funtols.game.actors.button.base.AButtonStyles
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame

class APanelTop(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontTitilliumWeb_BoldItalic, 18f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackBtn  = AButtonAnim(screen, AButtonStyles.Anim.BACK)
    private val aTitleLbl = AMsdfLabel("", styleDef)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBackBtn()
        addTitleLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBackBtn() {
        aBackBtn.setSize(37f, 32f)
        add(aBackBtn) { startToStart(); centerY() }
        aBackBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(116f, 30f)
        add(aTitleLbl) { startToStart(); endToEnd(); centerY(); }
        aTitleLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

}