package com.fimer.skintool.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.label.AMsdfLabel
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.utils.NumberFormatter
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.font.msdf.MsdfStyle
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelRBX(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontNunitoSans_Bold, 14f)

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

        aLbl.setSize(1f, 14f)
        add(aLbl) { endToEnd(margin = 20f); centerY() }

        coroutine?.launch {
            gdxGame.modelPlayer.rbxFlow.collect { rbx ->
                runGDX { setText(NumberFormatter.format(rbx)) }
            }
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    private fun setText(text: String) {
        aLbl.setText(text)
        width = (58f + aLbl.width + 20f).coerceAtLeast(105f)
    }

}