package com.selftest.mindora.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

class APanelDailyStreak(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontKarla_Bold, 50f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aFireImg   = Image(gdxGame.assetsAll.fire)
    private val aStreakLbl = AMsdfLabel("0 DAY", styleDef)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(Image(gdxGame.assetsAll.PANEL_STREAK)) { fillParent() }

        addDayLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addDayLbl() {
        aStreakLbl.setSize(137f, 50f)
        add(aStreakLbl) { centerX(); topToTop(margin = 58f); horizontalBias = 0.53f }

        aFireImg.setSize(47f, 50f)
        add(aFireImg) { endToStart(aStreakLbl, 10f); centerY(aStreakLbl) }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /**
     * @param streak скільки днів підряд юзер заходить. Це НЕ день циклу:
     *               на 8-й день підряд тут буде 8, а в сітці підсвітиться Day 1.
     */
    fun setStreak(streak: Int) {
        aStreakLbl.setText("$streak ${if (streak == 1) "DAY" else "DAYS"}")
    }
}