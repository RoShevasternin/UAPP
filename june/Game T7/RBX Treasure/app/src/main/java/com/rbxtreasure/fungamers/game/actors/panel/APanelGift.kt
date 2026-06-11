package com.rbxtreasure.fungamers.game.actors.panel

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbxtreasure.fungamers.game.actors.button.AImageYellowButton
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.actor.setOnClickListener
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.font.FontFactory
import com.rbxtreasure.fungamers.game.utils.font.FontParameter
import com.rbxtreasure.fungamers.game.utils.gdxGame

class APanelGift(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    companion object {
        private const val REWARD = 200L
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters("$REWARD RBX")
        .setSize(48)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg     = Image(gdxGame.assetsAll.PANEL_GIFT)
    private val aRewardLbl = Label("$REWARD RBX", FontFactory.create(screen, parameter, screen.fontGenerator_Anton_Regular, GameColor.yellow_DDA334))
    private val aClaimBtn  = AImageYellowButton(screen, TextureRegionDrawable(gdxGame.assetsAll.claim), Vector2(78f, 20f))

    // ------------------------------------------------------------------------
    // Callbacks (виставляє екран)
    // ------------------------------------------------------------------------
    var onClaim: (Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addRewardLbl()
        addClaimBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addRewardLbl() {
        aRewardLbl.setSize(152f, 48f)
        add(aRewardLbl) { centerX(); topToTop(margin = 64f) }
        aRewardLbl.setAlignment(Align.center)
    }

    private fun addClaimBtn() {
        aClaimBtn.setSize(312f, 52f)
        add(aClaimBtn) { centerX(); bottomToBottom(margin = 40f) }

        aClaimBtn.setOnClickListener { onClaim(REWARD) }
    }

}