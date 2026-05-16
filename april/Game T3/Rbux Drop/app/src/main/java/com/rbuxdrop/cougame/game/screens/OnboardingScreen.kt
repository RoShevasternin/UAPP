package com.rbuxdrop.cougame.game.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbuxdrop.cougame.game.actors.button.APurpleButton
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.utils.Block
import com.rbuxdrop.cougame.game.utils.TIME_ANIM_SCREEN
import com.rbuxdrop.cougame.game.utils.actor.animDelay
import com.rbuxdrop.cougame.game.utils.actor.animHide
import com.rbuxdrop.cougame.game.utils.actor.animShow
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame

class OnboardingScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var currentIndex = 0
    private val maxIndex     = 2

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aOnboardingImg = Image(gdxGame.assetsAll.listOnboarding[currentIndex])
    private val aPointImg      = Image(gdxGame.assetsAll.listPoint[currentIndex])
    private val aPurpleBtn     = APurpleButton(this, "Next")

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        //val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, safeBannerUI))
        //gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

//    override fun hide() {
//        super.hide()
//        gdxGame.activity.hideNative()
//    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPointImg()
        addPurpleBtn()
        addOnboardingImg()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPointImg() {
        aPointImg.setSize(76f, 44f)
        add(aPointImg) {
            centerX()
            bottomToBottom(margin = 24f)
        }
    }

    private fun AConstraintLayout.addPurpleBtn() {
        aPurpleBtn.setSize(344f, 60f)

        add(aPurpleBtn) {
            centerX()
            bottomToTop(aPointImg, 24f)
        }

        aPurpleBtn.setOnClickListener {
            if (currentIndex >= maxIndex) {
                animHideScreen { gdxGame.navigationManager.navigate(MainScreen::class.java.name, OnboardingScreen::class.java.name) }
                return@setOnClickListener
            }

            showNextPage()
        }
    }

    private fun AConstraintLayout.addOnboardingImg() {
        aOnboardingImg.setSize(376f, 559f)

        add(aOnboardingImg) {
            centerX()
            bottomToTop(aPurpleBtn, 24f)
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun showNextPage() {
        currentIndex++
        updateUI()

        gdxGame.activity.onFrontNavigation()
    }

    private fun updateUI() {
        // onboarding image
        aOnboardingImg.addAction(
            Actions.sequence(
                Actions.fadeOut(0.15f),
                Actions.run { aOnboardingImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listOnboarding[currentIndex]) },
                Actions.fadeIn(0.15f)
            )
        )

        // points image
        aPointImg.addAction(
            Actions.sequence(
                Actions.fadeOut(0.15f),
                Actions.run { aPointImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.listPoint[currentIndex]) },
                Actions.fadeIn(0.15f)
            )
        )
    }

}