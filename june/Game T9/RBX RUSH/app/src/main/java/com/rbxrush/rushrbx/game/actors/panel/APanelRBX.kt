package com.rbxrush.rushrbx.game.actors.panel

import com.rbxrush.rushrbx.businesModule.economy.Wallet
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.game.actors.checkbox.base.ACheckBoxGroup
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.NumberFormatter
import com.rbxrush.rushrbx.game.utils.actor.disable
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontFactory
import com.rbxrush.rushrbx.game.utils.font.FontParameter
import com.rbxrush.rushrbx.game.utils.gdxGame
import com.rbxrush.rushrbx.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelRBX(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + ",")
        .setSize(20)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Fredoka_Bold)

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
        aIcon.setSize(22f, 24f)
        add(aIcon) { startToStart(margin = 16f); centerY() }
    }

    private fun addLbl() {
        aLbl.setSize(1f, 22f)
        add(aLbl) { startToEnd(aIcon, 8f); centerY() }

        coroutine?.launch {
            // ⚠️ StateFlow віддає значення в потоці ПІДПИСНИКА — сцену чіпаємо
            // тільки через runGDX.
            Wallet.balanceFlow.collect { balance ->
                runGDX { setText(NumberFormatter.format(balance)) }
            }
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    private fun setText(text: String) {
        aLbl.setText(text)
        aLbl.pack()

        width = (16f + aIcon.width + 8f + aLbl.width + 16f).coerceAtLeast(93f)
    }

}