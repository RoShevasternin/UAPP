package com.selftest.mindora.game.actors.panel

import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.base.AButtonAnim
import com.selftest.mindora.game.actors.button.base.AButtonStyles
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

class APanelTop(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * Перевизначення «назад». null (дефолт) — вихід з екрана, як і було.
     * TestScreen ставить сюди крок по питаннях: з середини теста назад —
     * це попереднє питання, а не вихід.
     */
    var onBack: Block? = null

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 20f)

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
        aBackBtn.setSize(48f, 48f)
        add(aBackBtn) { startToStart(); centerY() }
        aBackBtn.setOnClickListener {
            onBack?.invoke() ?: screen.animHideScreen { gdxGame.navigationManager.back() }
        }
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(210f, 24f)
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