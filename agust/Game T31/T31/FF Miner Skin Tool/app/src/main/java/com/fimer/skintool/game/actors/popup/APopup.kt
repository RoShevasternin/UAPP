package com.fimer.skintool.game.actors.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.button.AYellowButton
import com.fimer.skintool.game.actors.label.AMsdfLabel
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.font.msdf.MsdfStyle
import com.fimer.skintool.game.utils.gdxGame

class APopup(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef by lazy {
        MsdfStyle(msdf, msdf.fontNunitoSans_Bold, 32f)
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
        aResultLbl.setSize(1f, 33f)
        add(aResultLbl) { startToStart(margin = 160f); topToTop(margin = 68f) }
    }

    private fun addClaimBtn() {
        aClaimBtn.setSize(248f, 40f)
        add(aClaimBtn) { centerX(); bottomToBottom(margin = 30f) }

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
        aResultLbl.setText("$reward")
    }

}