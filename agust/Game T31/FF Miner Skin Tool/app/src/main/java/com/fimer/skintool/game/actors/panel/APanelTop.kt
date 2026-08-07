package com.fimer.skintool.game.actors.panel

import com.badlogic.gdx.utils.Align
import com.fimer.skintool.game.actors.button.base.AButtonAnim
import com.fimer.skintool.game.actors.button.base.AButtonStyles
import com.fimer.skintool.game.actors.label.AMsdfLabel
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.utils.GameColor
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.font.msdf.MsdfStyle
import com.fimer.skintool.game.utils.gdxGame

class APanelTop(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontBowlbyOneSC_Regular, 18f)

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
        aTitleLbl.setSize(244f, 20f)
        add(aTitleLbl) { center(); /*horizontalBias = 0.7f*/ }
        aTitleLbl.setAlignment(Align.center)
        aTitleLbl.setEllipsis(true)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

}