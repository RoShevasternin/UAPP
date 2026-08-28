package com.selftest.mindora.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.NumberFormatter
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelLumens(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 14f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg = Image(gdxGame.assetsAll.panel_balance)
    private val aLbl   = AMsdfLabel("", styleDef)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLbl() {
        aLbl.autoSize = true

        aLbl.setSize(1f, 17f)
        add(aLbl) { startToStart(margin = 62f); centerY() }

        coroutine?.launch {
            gdxGame.modelPlayer.lumensFlow.collect { rbx ->
                runGDX { setText(NumberFormatter.format(rbx) + " lumens") }
            }
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    private fun setText(text: String) {
        aLbl.setText(text)
        width = (62f + aLbl.width + 54f).coerceAtLeast(185f)
    }

}