package com.sakurbx.fungambx.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.NumberFormatter
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.font.setDoubleShadow
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelRBX(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + ",")
        .setSize(18)
        .setDoubleShadow()

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Laila_Bold)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg = Image(gdxGame.assetsAll.panel_balance)
    private val aIcon  = Image(gdxGame.assetsAll.rbx)
    private val aLbl   = Label("", lsDef)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addIcon()
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addIcon() {
        aIcon.setSize(24f, 24f)
        add(aIcon) { startToStart(margin = 7f); centerY() }
    }

    private fun addLbl() {
        aLbl.setSize(1f, 17f)
        add(aLbl) { startToEnd(aIcon, 3f); centerY() }

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
        aLbl.pack()

        width = (7f + aIcon.width + 3f + aLbl.width + 21f).coerceAtLeast(100f)
    }

}