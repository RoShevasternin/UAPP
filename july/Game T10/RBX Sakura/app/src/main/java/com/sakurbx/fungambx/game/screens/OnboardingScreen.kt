package com.sakurbx.fungambx.game.screens

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.adsmodule.AdSizeManager
import com.sakurbx.fungambx.game.actors.button.AImagePinkButton
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.screens.selector.Selector_1_Screen
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.actor.animDelay
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.actor.setSize
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.runGDX
import com.sakurbx.fungambx.util.log
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
    // Actors
    // ------------------------------------------------------------------------
    private val aContinueBtn by lazy { AImagePinkButton(this, TextureRegionDrawable(gdxGame.assetsAll.icon_btn_1), Vector2(119f, 25f)) }
    private val aContentImg  by lazy { Image(gdxGame.assetsAll.listOnboarding[currentIndex]) }
    private val aStarImg     by lazy { Image(gdxGame.assetsLoader.STAR) }
    private val aPointImg    by lazy { Image(gdxGame.assetsAll.listPoint[currentIndex]) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsAll.BACKGROUND_PUPRLE)
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPointImg()
        addContinueBtn()
        addContentImg()
        addContentStarImg()
        aContentImg.toFront()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN)
        rootConstraintLayout.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN)
        rootConstraintLayout.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPointImg() {
        aPointImg.setSize(60f, 10f)
        add(aPointImg) { centerX(); topToTop(margin = 32f) }
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(344f, 57f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 32f) }

        aContinueBtn.setOnClickListener {
            if (currentIndex == maxIndex) {
                animHideScreen { gdxGame.navigationManager.navigate(Selector_1_Screen::class.java.name, OnboardingScreen::class.java.name) }
            }

            if ((currentIndex + 1) <= maxIndex) currentIndex++
            //if (currentIndex == maxIndex) aContinueBtn.label.setText("START")
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aContinueBtn) { marginBottom += screen.adBottomUI }
                log("OnboardingScreen: marginBottom += ${screen.adBottomUI}")
            } }
        }
    }

    private fun AConstraintLayout.addContentImg() {
        aContentImg.setSize(344f, 464f)
        add(aContentImg) { centerX(); topToBottom(aPointImg); bottomToTop(aContinueBtn) }
    }

    private fun AConstraintLayout.addContentStarImg() {
        aStarImg.setSize(489f, 489f)
        add(aStarImg) { centerX(aContentImg); topToTop(aContentImg, 12f) }

        animStar()
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------

    private fun animStar() {
        aStarImg.apply {
            setOrigin(Align.center)

            // повільне безперервне обертання
            addAction(
                Actions.forever(
                    Actions.rotateBy(360f, 24f, Interpolation.linear)
                )
            )

            // пульсація (сяйво) — окремо, паралельно з обертанням
            addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.scaleTo(1.12f, 1.12f, 2f, Interpolation.sine),
                        Actions.scaleTo(0.95f, 0.95f, 2f, Interpolation.sine),
                    )
                )
            )
        }
    }

    private fun animateTransition(newIndex: Int) {

        isAnimating = true

        val outDur = 0.35f   // відліт
        val inDur  = 0.45f   // приліт (трохи довший — ефектніше)

        val startX = aContentImg.x
        val startY = aContentImg.y

        aContentImg.setOrigin(Align.center)

        aContentImg.addAction(
            Actions.sequence(

                // ── провалювання в глибину: стискається майже в точку + крутиться + блідне ──
                Actions.parallel(
                    Actions.scaleTo(0.15f, 0.15f, outDur, Interpolation.pow3In),
                    Actions.rotateBy(-25f, outDur, Interpolation.sineIn),
                    Actions.fadeOut(outDur, Interpolation.pow2In),
                    Actions.moveBy(0f, 30f, outDur, Interpolation.sineIn)  // трохи "всмоктується" вглиб-вгору
                ),

                // ── підміна + підготовка нової глибоко "позаду" ──
                Actions.run {
                    aPointImg.drawable   = TextureRegionDrawable(gdxGame.assetsAll.listPoint[newIndex])
                    aContentImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listOnboarding[newIndex])

                    aContentImg.color.a  = 0f
                    aContentImg.rotation = 20f
                    aContentImg.setScale(0.15f)
                    aContentImg.setPosition(startX, startY + 30f)
                },

                // ── виринання з глибини: розростається до розміру з overshoot + докручується ──
                Actions.parallel(
                    Actions.scaleTo(1f, 1f, inDur, Interpolation.swingOut),
                    Actions.rotateTo(0f, inDur, Interpolation.swingOut),
                    Actions.fadeIn(inDur * 0.6f, Interpolation.pow2Out),
                    Actions.moveTo(startX, startY, inDur, Interpolation.swingOut)
                ),

                Actions.run { isAnimating = false }
            )
        )
    }

}