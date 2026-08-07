package com.racing.funtols.game.actors.popup

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.racing.funtols.game.actors.button.base.AButtonAnim
import com.racing.funtols.game.actors.button.base.AButtonStyles
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame

class APopup(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef by lazy {
        MsdfStyle(msdf, msdf.fontBarlow_Regular, 20f, Color.valueOf("550008"))
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg    = Image(gdxGame.assetsAll.POPUP)
    private val aResultLbl   = AMsdfLabel("0 RBX", styleDef)
    private val aRewardBtn   = AButtonAnim(screen, AButtonStyles.Anim.CLAIM)


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
        addRewardBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addRewardLbl() {
        aResultLbl.setSize(176f, 20f)
        add(aResultLbl) { centerX(); topToTop(margin = 56f) }
    }

    private fun addRewardBtn() {
        aRewardBtn.setSize(312f, 40f)
        add(aRewardBtn) { centerX(); bottomToBottom(margin = 16f) }

        aRewardBtn.setOnClickListener {
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
        aResultLbl.setText("YOU EARNED $reward RBX")
    }

}