package com.selftest.mindora.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.AMainButton
import com.selftest.mindora.game.actors.button.base.AButtonAnim
import com.selftest.mindora.game.actors.button.base.AButtonStyles
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.panel.APanelTop
import com.selftest.mindora.game.actors.panel.daily.APanelDaily
import com.selftest.mindora.game.actors.panel.daily.APanelDailyStreak
import com.selftest.mindora.game.actors.popup.APopup
import com.selftest.mindora.game.controller.DailyController
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.TIME_ANIM_SCREEN
import com.selftest.mindora.game.utils.VERTICAL_BIAS
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animHideAndDisable
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.actor.animShowAndEnable
import com.selftest.mindora.game.utils.actor.disable
import com.selftest.mindora.game.utils.actor.enable
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.actor.setSize
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.overlay.OverlayManager

class DailyScreen : AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 12f, GameColor.white_70)

    // ------------------------------------------------------------------------
    // Overlay
    // ------------------------------------------------------------------------
    private enum class Overlay { POPUP }

    private val overlayManager = OverlayManager(
        onShowDim = { aDimImg.animShowAndEnable(timeShow) },
        onHideDim = { aDimImg.animHideAndDisable(timeHide) },
    )

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.2f
    private val timeHide = 0.2f

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller by lazy {
        DailyController(
            scope = coroutine,
            model = gdxGame.modelPlayer,
        )
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.black_0A001D_80)) }
    private val aPopup  by lazy { APopup(this) }

    private val aPanelTop         by lazy { APanelTop(this) }
    private val aPanelDailyStreak by lazy { APanelDailyStreak(this) }
    private val aPanelDaily       by lazy { APanelDaily(this) }

    private val aHintLbl   by lazy { AMsdfLabel("Double your reward by watching an ad", styleDef) }
    private val aDoubleBtn by lazy { AButtonAnim(this, AButtonStyles.Anim.DOUBLE) }
    private val aClaimBtn  by lazy { AMainButton(this, "claim +0 lumens") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

    override fun hide() {
        controller.dispose()   // зупинити тікер, інакше корутина переживе екран
        super.hide()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelDailyStreak()
        addPanelDaily()

        addHintLbl()
        addDoubleBtn()
        addClaimBtn()

        addDimImg()
        addPopup()

        initController()
    }

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private fun initController() {
        controller.onRender  = { state -> render(state) }
        controller.onClaimed = { reward -> showRewardPopup(reward) }

        controller.initialize()
    }

    /**
     * Єдина точка рендеру екрана. Контролер віддає повний стан — тут тільки
     * розкладка його по акторах, жодних рішень і жодних звернень до моделі.
     */
    private fun render(state: DailyController.State) {
        aPanelDailyStreak.setStreak(state.streak)
        aPanelDaily.render(state.day, state.canClaim)

        if (state.canClaim) {
            aClaimBtn.label.setText("claim +${state.finalReward} lumens")
            aClaimBtn.enable()
        } else {
            aClaimBtn.label.setText("next reward in ${formatTime(state.remainingSeconds)}")
            aClaimBtn.disable()
        }

        if (controller.canDouble()) {
            aDoubleBtn.enable()
            aHintLbl.setText("Double your reward by watching an ad")
        } else {
            aDoubleBtn.disable()
            aHintLbl.setText(
                if (state.isDoubled) "Your reward is doubled"
                else                 "Come back tomorrow to keep your streak"
            )
        }
    }

    private fun showRewardPopup(reward: Long) {
        aPopup.setReward(reward)          // сам попап нарахує люмени в своїй кнопці
        overlayManager.show(Overlay.POPUP)
    }

    private fun formatTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    // ------------------------------------------------------------------------
    // Add Actors - Panel
    // ------------------------------------------------------------------------
    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 48f)
        add(aPanelTop) { centerX(); topToTop(margin = 8f) }

        aPanelTop.setTitle("Daily Reward")
    }

    private fun AConstraintLayout.addPanelDailyStreak() {
        aPanelDailyStreak.setSize(344f, 171f)
        add(aPanelDailyStreak) { centerX(); topToBottom(aPanelTop, 24f) }
    }

    private fun AConstraintLayout.addPanelDaily() {
        aPanelDaily.setSize(344f, 218f)
        add(aPanelDaily) { centerX(); topToBottom(aPanelDailyStreak, 16f) }
    }

    private fun AConstraintLayout.addHintLbl() {
        aHintLbl.setSize(233f, 14f)
        add(aHintLbl) { centerX(); bottomToBottom(margin = 20f) }
        aHintLbl.setAlignment(Align.center)
    }

    private fun AConstraintLayout.addDoubleBtn() {
        aDoubleBtn.setSize(344f, 50f)
        add(aDoubleBtn) { centerX(); bottomToTop(aHintLbl, 10f) }

        aDoubleBtn.setOnClickListener {
            if (!controller.canDouble()) return@setOnClickListener

            gdxGame.activity.showInterstitial {
                controller.applyDouble()
            }
        }
    }

    private fun AConstraintLayout.addClaimBtn() {
        aClaimBtn.setSize(344f, 55f)
        add(aClaimBtn) { centerX(); bottomToTop(aDoubleBtn, 10f) }

        aClaimBtn.setOnClickListener { controller.tryClaim() }
    }

    // ------------------------------------------------------------------------
    // Add Actors - Dim | Popup
    // ------------------------------------------------------------------------
    private fun AConstraintLayout.addDimImg() {
        aDimImg.animHideAndDisable()
        add(aDimImg) {
            matchConstraint()
            centerX(); bottomToBottom(); topToTop(margin = -safeStatusBarUI)
        }
        aDimImg.setOnClickListener(null) {
            if (overlayManager.isClosable) overlayManager.close()
        }
    }

    private fun AConstraintLayout.addPopup() {
        aPopup.animHideAndDisable()
        aPopup.setSize(362f, 401f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        overlayManager.register(
            Overlay.POPUP, OverlayManager.Config(
                showDim    = true,
                isClosable = false, // поки не забрав нагороду — не закривати кліком по фону
                onShow     = { aPopup.animShowAndEnable(timeShow) },
                onHide     = { aPopup.animHideAndDisable(timeHide) },
            ))

        aPopup.onClaim = { overlayManager.close() }
    }

}