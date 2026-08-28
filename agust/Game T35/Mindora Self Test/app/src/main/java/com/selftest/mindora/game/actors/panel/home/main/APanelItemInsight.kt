package com.selftest.mindora.game.actors.panel.home.main

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.content.Insights
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

class APanelItemInsight(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef by lazy {
        MsdfStyle(msdf, msdf.fontMontserrat_Regular, 12f, GameColor.white_80)
    }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Скільки інсайт висить на екрані до заміни
    private val timeHold = 10f

    // Перехід між інсайтами (fadeOut + fadeIn)
    private val timeFade = 0.35f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg   = Image(gdxGame.assetsAll.ITEM_INSIGHT)
    private val aTextLbl = AMsdfLabel("", styleDef)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBgImg()
        addProgressLbl()

        startInsightLoop()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBgImg() {
        add(aBgImg) { fillParent() }
    }

    private fun addProgressLbl() {
        aTextLbl.setSize(248f, 28f)
        add(aTextLbl) { startToStart(margin = 79f); topToTop(margin = 43f) }

        aTextLbl.setAlignment(Align.topLeft)
        aTextLbl.wrap = true
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------

    /**
     * Вічний цикл: показати → тримати timeHold → згаснути → підмінити текст
     * під нульовою альфою → проявитись. Заміна саме в невидимій фазі, щоб
     * не було видно стрибка перенесення рядків.
     */
    private fun startInsightLoop() {
        aTextLbl.clearActions()
        aTextLbl.setText("\"${Insights.next()}\"")
        aTextLbl.color.a = 1f

        aTextLbl.addAction(Actions.forever(Actions.sequence(
            Actions.delay(timeHold),
            Actions.fadeOut(timeFade, Interpolation.fade),
            Actions.run { aTextLbl.setText("\"${Insights.next()}\"") },
            Actions.fadeIn(timeFade, Interpolation.fade)
        )))
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** Перезапустити з новим інсайтом — наприклад при поверненні на хаб. */
    fun refreshInsight() {
        startInsightLoop()
    }

}