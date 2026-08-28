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

class APopupStart(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val REWARD_START: Long = gdxGame.activity.appConfig.economy.lumensWelcomeBonus

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
    private val aPopupImg  = Image(gdxGame.assetsAll.POPUP_START)
    private val aResultLbl = AMsdfLabel("+$REWARD_START Lumens", styleDef)
    private val aLumensImg = Image(gdxGame.assetsAll.lumens_big)
    private val aClaimBtn  = AMainButton(screen, "Start")

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onStart = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }
        addRewardLbl()
        addLumensImg()
        addStartBtn()
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

    private fun addStartBtn() {
        aClaimBtn.setSize(310f, 55f)
        add(aClaimBtn) { centerX(); bottomToBottom(margin = 42f) }

        aClaimBtn.setOnClickListener {
            gdxGame.soundUtil.apply { play(REWARD) }
            gdxGame.modelPlayer.addLumens(REWARD_START)
            onStart()
        }
    }

    private fun addLumensImg() {
        aLumensImg.setSize(180f, 180f)
        add(aLumensImg) { centerX(); topToTop(margin = 101f) }

        animLumens()
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