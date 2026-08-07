package com.diam.ondbit.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.GameColor
import com.diam.ondbit.game.utils.NumberFormatter
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelRBX(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontSpaceGrotesk_Medium, 16f)

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

        aLbl.setSize(1f, 16f)
        add(aLbl) { endToEnd(margin = 24f); centerY() }

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
        width = (58f + aLbl.width + 24f).coerceAtLeast(118f)
    }

}