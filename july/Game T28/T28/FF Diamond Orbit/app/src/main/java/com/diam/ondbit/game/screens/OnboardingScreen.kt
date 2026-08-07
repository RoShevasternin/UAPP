package com.diam.ondbit.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.video.VideoPlayer
import com.badlogic.gdx.video.VideoPlayerCreator
import com.diam.ondbit.adsmodule.AdSizeManager
import com.diam.ondbit.game.actors.AVideoActor
import com.diam.ondbit.game.actors.button.AYellowButton
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.screens.selector.Selector_1_Screen
import com.diam.ondbit.game.utils.Block
import com.diam.ondbit.game.utils.TIME_ANIM_SCREEN
import com.diam.ondbit.game.utils.actor.animHide
import com.diam.ondbit.game.utils.actor.animShow
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.runGDX
import com.diam.ondbit.util.log
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
    private val timeContentOut = 0.22f // контент відлітає й гасне
    private val timeContentIn  = 0.32f // новий контент виринає з глибини
    private val timeVideoCross = 0.50f // кросфейд між відео

    private val zoomContentOut = 1.06f // до чого розростається той, що йде
    private val zoomContentIn  = 0.94f // з чого виринає новий
    private val zoomVideoIn    = 0.96f // з чого виринає нове відео

    // ------------------------------------------------------------------------
    // Video
    // ------------------------------------------------------------------------
    private val listOnboardingVideo = List<VideoPlayer?>(3) {
        runCatching {
            val videoPlayer = VideoPlayerCreator.createVideoPlayer().apply {
                isLooping = true   // зациклити фон
                load(Gdx.files.internal("video/onboarding_${it.inc()}.mp4"))
                play()
            }
            disposableSet.add(videoPlayer)
            videoPlayer
        }.getOrNull()
    }

    // Для кросфейду потрібні ДВА актори: новий проявляється поверх старого.
    // Один актор перетекти сам у себе не може — була б чорнота посередині.
    private var isVideoA = true

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVideoActorA by lazy { AVideoActor() }
    private val aVideoActorB by lazy { AVideoActor() }

    private val aContinueBtn by lazy { AYellowButton(this, "CONTINUE") }
    private val aContentImg  by lazy { Image(gdxGame.assetsAll.listOnboarding[currentIndex]) }
    private val aPImg        by lazy { Image(gdxGame.assetsAll.listP[currentIndex]) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addContentVideoActor()
        addContentImg()
        addContinueBtn()
        addPImg()
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

    private fun AConstraintLayout.addContentVideoActor() {
        // Обидва актори з однаковими констрейнтами — лежать один на одному
        add(aVideoActorA) { center(); marginTop = -safeStatusBarUI; matchConstraint() }
        add(aVideoActorB) { center(); marginTop = -safeStatusBarUI; matchConstraint() }

        aVideoActorA.setPlayer(listOnboardingVideo[currentIndex])
        aVideoActorB.color.a = 0f
    }

    private fun AConstraintLayout.addContentImg() {
        aContentImg.setSize(344f, 152f)
        add(aContentImg) { centerX(); topToTop(margin = 22f) }
    }

    private fun AConstraintLayout.addPImg() {
        aPImg.setSize(34f, 10f)
        add(aPImg) { centerX(); bottomToTop(aContinueBtn, 32f) }
    }

    private fun AConstraintLayout.addContinueBtn() {
        aContinueBtn.setSize(345f, 62f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 40f) }

        aContinueBtn.setOnClickListener {
            if (currentIndex == maxIndex) {
                animHideScreen { gdxGame.navigationManager.navigate(Selector_1_Screen::class.java.name, OnboardingScreen::class.java.name) }
            }

            if ((currentIndex + 1) <= maxIndex) currentIndex++
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aContinueBtn) { marginBottom = screen.adBottomUI + 40f }
                log("OnboardingScreen: marginBottom += ${screen.adBottomUI}")
            } }
        }
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------
    private fun animateTransition(newIndex: Int) {
        isAnimating = true

        animateVideo(newIndex)
        animateContent(newIndex)
        animatePoints(newIndex)
    }

    // ── Відео: новий кадр проявляється ПОВЕРХ старого ─────────────────────────
    // Старий НЕ гасимо: інакше в середині переходу обидва були б напівпрозорі
    // і картинка провалилась би в темряву.
    private fun animateVideo(newIndex: Int) {
        val aVideoOld = if (isVideoA) aVideoActorA else aVideoActorB
        val aVideoNew = if (isVideoA) aVideoActorB else aVideoActorA

        // Новий строго над старим, але обидва лишаються під UI
        aVideoOld.zIndex = 0
        aVideoNew.zIndex = 1

        aVideoNew.setPlayer(listOnboardingVideo[newIndex])
        aVideoNew.setOrigin(Align.center)
        aVideoNew.color.a = 0f
        aVideoNew.setScale(zoomVideoIn)

        aVideoNew.clearActions()
        aVideoNew.addAction(Actions.parallel(
            Actions.fadeIn(timeVideoCross, Interpolation.fade),
            Actions.scaleTo(1f, 1f, timeVideoCross, Interpolation.pow3Out)
        ))

        isVideoA = !isVideoA
    }

    // ── Контент: відлітає вперед і гасне, новий виринає з глибини ─────────────
    private fun animateContent(newIndex: Int) {
        aContentImg.setOrigin(Align.center)
        aContentImg.clearActions()

        aContentImg.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeOut(timeContentOut, Interpolation.pow2In),
                Actions.scaleTo(zoomContentOut, zoomContentOut, timeContentOut, Interpolation.pow2In)
            ),

            Actions.run {
                aContentImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listOnboarding[newIndex])
                aContentImg.setScale(zoomContentIn)
            },

            Actions.parallel(
                Actions.fadeIn(timeContentIn, Interpolation.pow2Out),
                Actions.scaleTo(1f, 1f, timeContentIn, Interpolation.pow3Out)
            ),

            Actions.run { isAnimating = false }
        ))
    }

    // ── Індикатор сторінок: коротко гасне і повертається ──────────────────────
    private fun animatePoints(newIndex: Int) {
        aPImg.clearActions()

        aPImg.addAction(Actions.sequence(
            Actions.fadeOut(timeContentOut, Interpolation.pow2In),
            Actions.run { aPImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listP[newIndex]) },
            Actions.fadeIn(timeContentIn, Interpolation.pow2Out)
        ))
    }

}