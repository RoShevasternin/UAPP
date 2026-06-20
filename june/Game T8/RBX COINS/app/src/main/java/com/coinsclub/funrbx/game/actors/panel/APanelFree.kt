package com.coinsclub.funrbx.game.actors.panel

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.button.AImageYellowButton
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow
import com.coinsclub.funrbx.game.utils.gdxGame

class APanelFree(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    companion object {
        private const val REWARD = 300L
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters("$REWARD RBX")
        .setSize(50)
        .setBorderAndShadow(border = 5f, shadowX = 6, shadowY = 5)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg        = Image(gdxGame.assetsAll.PANEL_FREE)
    private val aRewardLbl    = Label("$REWARD RBX", FontFactory.create(screen, parameter, screen.fontGenerator_LuckiestGuy_Regular, GameColor.yellow_DFA008))
    private val aGetPrizeBtn  = AImageYellowButton(screen, TextureRegionDrawable(gdxGame.assetsAll.icon_get_prize), Vector2(108f, 20f))

    // ------------------------------------------------------------------------
    // Callbacks (виставляє екран)
    // ------------------------------------------------------------------------
    var onGetPrize: (Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addRewardLbl()
        addGetPrizeBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addRewardLbl() {
        aRewardLbl.setSize(188f, 50f)
        add(aRewardLbl) { centerX(); topToTop(margin = 86f) }
        aRewardLbl.setAlignment(Align.center)
    }

    private fun addGetPrizeBtn() {
        aGetPrizeBtn.setSize(307f, 57f)
        add(aGetPrizeBtn) { centerX(); bottomToBottom(margin = 18f) }
        aGetPrizeBtn.setOnClickListener { onGetPrize(REWARD) }
    }

}