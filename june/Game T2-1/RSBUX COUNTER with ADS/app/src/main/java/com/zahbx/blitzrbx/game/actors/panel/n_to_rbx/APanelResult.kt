package com.zahbx.blitzrbx.game.actors.panel.n_to_rbx

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.zahbx.blitzrbx.game.actors.button.AGreenButton
import com.zahbx.blitzrbx.game.actors.label.ALabel
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.utils.GameColor
import com.zahbx.blitzrbx.game.utils.NumberFormatter
import com.zahbx.blitzrbx.game.utils.actor.animShowAndEnable
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.font.FontParameter
import com.zahbx.blitzrbx.game.utils.gdxGame
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class APanelResult(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aResultImg  = Image(gdxGame.assetsAll.result_n_to_rbx)
    private val aResultLbl  = ALabel(screen, "0.00 Dollars", GameColor.green_06, parameter, screen.fontGenerator_Medium)
    private val aGreenBtn   = AGreenButton(screen, "Done")

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addResultImg()
        addResultLbl()
        addGreenBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addResultImg() {
        aResultImg.setSize(344f, 100f)
        add(aResultImg) {
            centerX()
            topToTop()
        }
    }

    private fun addResultLbl() {
        aResultLbl.setSize(110f, 24f)
        add(aResultLbl) {
            startToStart(aResultImg, 117f)
            endToEnd(aResultImg, 117f)
            bottomToBottom(aResultImg, 18f)
        }
    }

    private fun addGreenBtn() {
        aGreenBtn.setSize(344f, 60f)
        add(aGreenBtn) {
            centerX()
            topToBottom(aResultImg, 16f)
        }

        aGreenBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(screen::class.java.name) }
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun calculateAndShow(value: Int) {
        val result = value * 15.0
        aResultLbl.setText("${NumberFormatter.formatDollars(result)} Dollars")

        animShowAndEnable(0.15f)
    }

}