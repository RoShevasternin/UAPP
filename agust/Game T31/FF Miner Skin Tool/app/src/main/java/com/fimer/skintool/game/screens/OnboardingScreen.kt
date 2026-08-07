package com.fimer.skintool.game.screens

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.fimer.skintool.adsmodule.AdSizeManager
import com.fimer.skintool.game.actors.button.AYellowButton
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.utils.Block
import com.fimer.skintool.game.utils.TIME_ANIM_SCREEN
import com.fimer.skintool.game.utils.actor.animHide
import com.fimer.skintool.game.utils.actor.animShow
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.runGDX
import com.fimer.skintool.util.log
import kotlinx.coroutines.launch

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
    private val aContinueBtn by lazy { AYellowButton(this, "NEXT") }
    private val aContentImg  by lazy { Image(gdxGame.assetsAll.listOnboarding[currentIndex]) }

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
        addContentImg()
        addContinueBtn()
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
    private fun AConstraintLayout.addContentImg() {
        aContentImg.setSize(344f, 431f)
        add(aContentImg) { center() }
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(344f, 50f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 56f) }

        aContinueBtn.setOnClickListener {
            if (currentIndex == maxIndex) {
                animHideScreen { gdxGame.navigationManager.navigate(HomeScreen::class.java.name, OnboardingScreen::class.java.name) }
            }

            if ((currentIndex + 1) <= maxIndex) currentIndex++
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aContinueBtn) { marginBottom = screen.adBottomUI + 30f }
                log("OnboardingScreen: marginBottom += ${screen.adBottomUI}")
            } }
        }
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
                aContentImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listOnboarding[newIndex])
                aContentImg.setScale(zoomContentIn)
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