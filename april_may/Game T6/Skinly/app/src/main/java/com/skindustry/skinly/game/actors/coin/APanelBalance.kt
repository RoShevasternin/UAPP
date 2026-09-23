package com.skindustry.skinly.game.actors.coin

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.skindustry.skinly.businesModule.economy.Wallet
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.NumberFormatter
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter
import com.skindustry.skinly.game.utils.runGDX
import kotlinx.coroutines.launch

// Плашка балансу для шапки: монета + число. Єдине джерело — Wallet.balanceFlow;
// collect віддає значення в потоці підписника, тому в GDX — через runGDX.
class APanelBalance(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val aBgImg    = Image(screen.drawerUtil.getRoundedRegion(W, H, H / 2, GameColor.gray_F2F2F2))
    private val aCoinIcon = ACoinIcon(screen, COIN)
    private val aValueLbl = Label("0", FontFactory.create(
        screen, FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + ".,KMB").setSize(18),
        screen.fontGenerator_Bold, Color.BLACK))

    override fun addActorsOnGroup() {
        addAndFillActor(aBgImg)

        val coinY = (H - COIN) / 2f
        addActor(aCoinIcon)
        aCoinIcon.setBounds(coinY, coinY, COIN.toFloat(), COIN.toFloat())

        addActor(aValueLbl)
        aValueLbl.setBounds(coinY + COIN + 6f, 0f, W - COIN - coinY * 2 - 6f, H.toFloat())
        aValueLbl.setAlignment(Align.center)

        coroutine?.launch {
            Wallet.balanceFlow.collect { balance -> runGDX { aValueLbl.setText(NumberFormatter.format(balance)) } }
        }
    }

    companion object {
        const val W    = 104
        const val H    = 40
        const val COIN = 28
    }
}
