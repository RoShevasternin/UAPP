package com.selftest.mindora.game.screens

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.AMainButton
import com.selftest.mindora.game.actors.checkbox.base.ACheckBox
import com.selftest.mindora.game.actors.checkbox.base.ACheckBoxStyles
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.TIME_ANIM_SCREEN
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animHideAndDisable
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.actor.animShowAndEnable
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame

class OnboardingScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var maxIndex    = gdxGame.assetsAll.listOnboarding.lastIndex
    private var isAnimating = false

    private var currentIndex = 0
        set(value) {
            if (isAnimating) return
            gdxGame.activity.onFrontNavigation()
            animateTransition(value)
            field = value
        }

    // ------------------------------------------------------------------------
    // Animation settings
    // ------------------------------------------------------------------------
    private val timeContentOut = 0.22f // старий контент відлітає й гасне
    private val timeContentIn  = 0.32f // новий виринає з глибини

    private val zoomContentOut = 1.06f // до чого розростається той, що йде
    private val zoomContentIn  = 0.94f // з чого виринає новий

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aNextBtn     by lazy { AMainButton(this, "Next") }
    private val aContentImg  by lazy { Image(gdxGame.assetsAll.listOnboarding[currentIndex]) }
    private val aPImg        by lazy { Image(gdxGame.assetsAll.listP[currentIndex]) }
    private val aCheckBox    by lazy { ACheckBox(this, ACheckBoxStyles.CHECK) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addNextBtn()
        addPImg()
        addContentImg()
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
    private fun AConstraintLayout.addNextBtn() {
        aNextBtn.setSize(344f, 56f)
        add(aNextBtn) { centerX(); bottomToBottom(margin = 20f) }

        aNextBtn.setOnClickListener {
            if (currentIndex == maxIndex) {
                animHideScreen { gdxGame.navigationManager.navigate(MenuScreen::class.java.name, OnboardingScreen::class.java.name) }
            }

            if ((currentIndex + 1) <= maxIndex) currentIndex++
        }
    }

    private fun AConstraintLayout.addPImg() {
        aPImg.setSize(46f, 10f)
        add(aPImg) { centerX(); bottomToTop(aNextBtn, 26f) }
    }

    private fun AConstraintLayout.addContentImg() {
        aContentImg.setSize(362f, 539f)
        add(aContentImg) { centerX(); topToTop(); bottomToTop(aNextBtn) }

        aCheckBox.animHideAndDisable()
        aCheckBox.setSize(24f, 24f)
        add(aCheckBox) { startToStart(aContentImg, 42f); bottomToBottom(aContentImg, 221f) }
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------
    private fun animateTransition(newIndex: Int) {
        isAnimating = true
        animateContent(newIndex)
    }

    // ── Контент: відлітає вперед і гасне, новий виринає з глибини ─────────────
    // Рухаємо ТІЛЬКИ scale і alpha — позицією розпоряджається констрейнт-лейаут.
    // Якби ми зсували актора вручну, adBottomFlow міг би переставити його
    // посеред анімації, і збережена позиція стала б недійсною.
    private fun animateContent(newIndex: Int) {
        aContentImg.setOrigin(Align.center)
        aContentImg.clearActions()

        aContentImg.addAction(Actions.sequence(

            // Фаза 1 — старий розростається й гасне
            Actions.parallel(
                Actions.fadeOut(timeContentOut, Interpolation.pow2In),
                Actions.scaleTo(zoomContentOut, zoomContentOut, timeContentOut, Interpolation.pow2In)
            ),

            // Підміна картинки в невидимій точці + відкат у глибину
            Actions.run {
                aPImg.drawable       = TextureRegionDrawable(gdxGame.assetsAll.listP[newIndex])
                aContentImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listOnboarding[newIndex])
                aContentImg.setScale(zoomContentIn)

                if (newIndex == 2) {
                    aCheckBox.check()
                    aCheckBox.animShowAndEnable()
                    aCheckBox.setOnCheckListener {  }
                }
            },

            // Фаза 2 — новий виринає на місце
            Actions.parallel(
                Actions.fadeIn(timeContentIn, Interpolation.pow2Out),
                Actions.scaleTo(1f, 1f, timeContentIn, Interpolation.pow3Out)
            ),

            Actions.run { isAnimating = false }
        ))
    }

}