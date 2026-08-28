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

class APopupMore(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val REWARD: Long = gdxGame.activity.appConfig.economy.lumensPerRewarded

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef by lazy {
        MsdfStyle(msdf, msdf.fontMontserrat_Regular, 14f)
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg  = Image(gdxGame.assetsAll.POPUP_MORE)
    private val aResultLbl = AMsdfLabel("Watch a short ad to earn +$REWARD Lumens", styleDef)
    private val aIconImg   = Image(gdxGame.assetsAll.watch_ad)
    private val aWatchBtn  = AMainButton(screen, "Watch")

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onWatch: (reward: Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }
        addRewardLbl()
        addIconImg()
        addWatchBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addRewardLbl() {
        aResultLbl.autoSize = true
        aResultLbl.setAlignment(Align.center)
        aResultLbl.setSize(1f, 17f)
        add(aResultLbl) { centerX(); topToTop(margin = 76f) }
    }

    private fun addWatchBtn() {
        aWatchBtn.setSize(310f, 55f)
        add(aWatchBtn) { centerX(); bottomToBottom(margin = 42f) }

        aWatchBtn.setOnClickListener { onWatch(REWARD) }
    }

    private fun addIconImg() {
        aIconImg.setSize(230f, 154f)
        add(aIconImg) { centerX(); topToTop(margin = 101f) }

        animIcon()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------
    /** Люмени: легка левітація + пульсація масштабу від центру */
    private fun animIcon() {
        aIconImg.clearActions()
        aIconImg.setOrigin(Align.center)

        // левітація — вгору-вниз
        aIconImg.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0f,  3f, 1.3f, Interpolation.sine),
            Actions.moveBy(0f, -3f, 1.3f, Interpolation.sine)
        )))

        // пульс — стискання до центру й назад
        aIconImg.addAction(Actions.forever(Actions.sequence(
            Actions.scaleTo(0.94f, 0.94f, 1.1f, Interpolation.sine),
            Actions.scaleTo(1f, 1f, 1.1f, Interpolation.sine)
        )))
    }

}