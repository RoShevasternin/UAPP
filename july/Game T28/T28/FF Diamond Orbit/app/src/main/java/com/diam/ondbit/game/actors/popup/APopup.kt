package com.diam.ondbit.game.actors.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.diam.ondbit.game.actors.button.AYellowButton
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame

class APopup(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef by lazy {
        MsdfStyle(msdf, msdf.fontSpaceGrotesk_Medium, 18f)
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg    = Image(gdxGame.assetsAll.POPUP)
    private val aResultLbl   = AMsdfLabel("0", styleDef)
    private val aClaimBtn    = AYellowButton(screen, "CLAIM")


    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onClaim = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }
        addRewardLbl()
        addClaimBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addRewardLbl() {
        aResultLbl.autoSize = true
        aResultLbl.setSize(1f, 18f)
        add(aResultLbl) { centerX(); topToTop(margin = 64f) }

        val aDiImg = Image(gdxGame.assetsAll.di)
        aDiImg.setSize(18f, 18f)
        add(aDiImg) { startToEnd(aResultLbl, 4f); centerY(aResultLbl) }
    }

    private fun addClaimBtn() {
        aClaimBtn.setSize(296f, 52f)
        add(aClaimBtn) { centerX(); bottomToBottom(margin = 24f) }

        aClaimBtn.setOnClickListener {
            gdxGame.soundUtil.apply { play(REWARD) }
            gdxGame.modelPlayer.addRbx(reward)
            onClaim()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    private var reward = 0L

    fun setReward(reward: Long) {
        this.reward = reward
        aResultLbl.setText("You earned: $reward")
    }

}