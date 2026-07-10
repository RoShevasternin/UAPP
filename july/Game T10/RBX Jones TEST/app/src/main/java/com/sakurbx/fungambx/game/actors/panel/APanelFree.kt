package com.sakurbx.fungambx.game.actors.panel

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.button.AImagePinkButton
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.gdxGame

class APanelFree(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    companion object {
        private const val REWARD = 500L
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters("$REWARD RBX")
        .setSize(60)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg        = Image(gdxGame.assetsAll.PANEL_FREE)
    private val aRewardLbl    = Label("$REWARD RBX", FontFactory.create(screen, parameter, screen.fontGenerator_Laila_Bold, GameColor.yellow_FACA4F))
    private val aGetPrizeBtn  = AImagePinkButton(screen, TextureRegionDrawable(gdxGame.assetsAll.icon_get_prize), Vector2(103f, 18f))

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
        aRewardLbl.setSize(225f, 60f)
        add(aRewardLbl) { centerX(); topToTop(margin = 100f) }
        aRewardLbl.setAlignment(Align.center)
    }

    private fun addGetPrizeBtn() {
        aGetPrizeBtn.setSize(280f, 56f)
        add(aGetPrizeBtn) { centerX(); bottomToBottom(margin = 16f) }
        aGetPrizeBtn.setOnClickListener { onGetPrize(REWARD) }
    }

}