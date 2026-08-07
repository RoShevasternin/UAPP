package com.racing.funtols.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.GameColor
import com.racing.funtols.game.utils.NumberFormatter
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelRBX(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontBarlow_Bold, 16f, GameColor.black_101010)

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

        aLbl.setSize(1f, 32f)
        add(aLbl) { endToEnd(margin = 24f); bottomToBottom(margin = 6f) }

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
        width = (50f + aLbl.width + 24f).coerceAtLeast(108f)
    }

}