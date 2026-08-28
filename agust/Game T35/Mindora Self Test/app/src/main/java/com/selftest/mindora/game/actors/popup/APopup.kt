package com.selftest.mindora.game.actors.popup

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.AMainButton
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

class APopup(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef by lazy {
        MsdfStyle(msdf, msdf.fontMontserrat_Medium, 26f)
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg  = Image(gdxGame.assetsAll.POPUP)
    private val aResultLbl = AMsdfLabel("0", styleDef)
    private val aLumensImg = Image(gdxGame.assetsAll.lumens_big)
    private val aClaimBtn  = AMainButton(screen, "Claim")


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
        addLumensImg()
        addClaimBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addRewardLbl() {
        aResultLbl.autoSize = true
        aResultLbl.setAlignment(Align.center)
        aResultLbl.setSize(1f, 26f)
        add(aResultLbl) { centerX(); topToTop(margin = 67f) }
    }

    private fun addClaimBtn() {
        aClaimBtn.setSize(310f, 55f)
        add(aClaimBtn) { centerX(); bottomToBottom(margin = 42f) }

        aClaimBtn.setOnClickListener {
            gdxGame.soundUtil.apply { play(REWARD) }
            gdxGame.modelPlayer.addLumens(reward)
            onClaim()
        }
    }

    private fun addLumensImg() {
        aLumensImg.setSize(180f, 180f)
        add(aLumensImg) { centerX(); topToTop(margin = 101f) }

        animLumens()
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    private var reward = 0L

    fun setReward(reward: Long) {
        this.reward = reward
        aResultLbl.setText("+$reward Lumens")
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------
    /** Люмени: легка левітація + пульсація масштабу від центру */
    private fun animLumens() {
        aLumensImg.clearActions()
        aLumensImg.setOrigin(Align.center)

        // левітація — вгору-вниз
        aLumensImg.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0f,  5f, 1.7f, Interpolation.sine),
            Actions.moveBy(0f, -5f, 1.7f, Interpolation.sine)
        )))

        // пульс — стискання до центру й назад
        aLumensImg.addAction(Actions.forever(Actions.sequence(
            Actions.scaleTo(0.95f, 0.95f, 1.15f, Interpolation.sine),
            Actions.scaleTo(1f, 1f, 1.15f, Interpolation.sine)
        )))
    }

}