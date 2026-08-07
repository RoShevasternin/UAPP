package com.diam.ondbit.game.actors.panel

import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.button.base.AButtonAnim
import com.diam.ondbit.game.actors.button.base.AButtonStyles
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.GameColor
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame

class APanelTop(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontOrbitron_Black, 18f, GameColor.black_D0C8DE)

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
        aBackBtn.setSize(40f, 40f)
        add(aBackBtn) { startToStart(); centerY() }
        aBackBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(238f, 18f)
        add(aTitleLbl) { center(); horizontalBias = 0.7f }
        aTitleLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

}