package com.coinsclub.funrbx.game.actors.panel.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.NumberFormatter
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelRBX(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterBalance = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(48)
        .setBorderAndShadow(border = 5f, shadowX = 7, shadowY = 4)

    private val lsBalance = FontFactory.create(screen, parameterBalance, screen.fontGenerator_LuckiestGuy_Regular, GameColor.yellow_DFA008)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[0])
    private val aBalanceLbl = Label("0", lsBalance)
    private val aRBXImg     = Image(gdxGame.assetsAll.rbx)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addContentImg()
        addBalanceLbl()
        addRBXImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addContentImg() {
        add(aContentImg) { fillParent() }
    }

    private fun addBalanceLbl() {
        aBalanceLbl.height = 48f
        add(aBalanceLbl) { startToStart(margin = 18f); bottomToBottom(margin = 4f) }

        collectRBX()
    }

    private fun addRBXImg() {
        aRBXImg.setSize(39f, 20f)
        add(aRBXImg) { startToEnd(aBalanceLbl, 6f); bottomToBottom(margin = 15f) }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun collectRBX() {
        coroutine?.launch {
            gdxGame.modelPlayer.rbxFlow.collect { rbx ->
                runGDX {
                    aBalanceLbl.setText(NumberFormatter.format(rbx))
                    aBalanceLbl.pack()
                }
            }
        }
    }

}