package com.rsbuxs.rcounbux.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rsbuxs.rcounbux.game.actors.button.AGreenButton
import com.rsbuxs.rcounbux.game.actors.button.base.AButtonAnim
import com.rsbuxs.rcounbux.game.actors.button.base.AButtonStyles
import com.rsbuxs.rcounbux.game.actors.layout.AlignH
import com.rsbuxs.rcounbux.game.actors.layout.AlignV
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.actors.panel.APanelLanguage
import com.rsbuxs.rcounbux.game.actors.panel.APanelTop
import com.rsbuxs.rcounbux.game.utils.Block
import com.rsbuxs.rcounbux.game.utils.TIME_ANIM_SCREEN
import com.rsbuxs.rcounbux.game.utils.actor.addActorAligned
import com.rsbuxs.rcounbux.game.utils.actor.animDelay
import com.rsbuxs.rcounbux.game.utils.actor.animHide
import com.rsbuxs.rcounbux.game.utils.actor.animShow
import com.rsbuxs.rcounbux.game.utils.actor.setBounds
import com.rsbuxs.rcounbux.game.utils.actor.setOnClickListener
import com.rsbuxs.rcounbux.game.utils.actor.setSize
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame
import com.rsbuxs.rcounbux.util.log

class LanguageScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop      = APanelTop(this)
    private val aDoneBtn       = AGreenButton(this, "Done")
    private val aPanelLanguage = APanelLanguage(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addDoneBtn()
        addPanelLanguage()

        //addTEST()
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

    private fun AConstraintLayout.addTEST() {
        val debuder3 = Image(drawerUtil.getTexture(Color.GREEN))
        debuder3.setSize(344f, 64f)
        add(debuder3) { center() }

        var flag = true
        debuder3.setOnClickListener {
            if (flag) {
                flag = false
                gdxGame.activity.showBanner()
            } else {
                flag = true
                gdxGame.activity.hideBanner()
            }
        }

    }

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(376f, 56f)
        add(aPanelTop) {
            centerX()
            topToTop()
        }

        aPanelTop.setTitle("Select Language")

        aPanelTop.onBack = { }
    }

    private fun AConstraintLayout.addDoneBtn() {
        aDoneBtn.setSize(344f, 60f)
        add(aDoneBtn) {
            centerX()
            bottomToBottom(margin = 20f + safeBannerUI)
        }

        aDoneBtn.setOnClickListener {
            animHideScreen { gdxGame.navigationManager.navigate(WelcomeScreen::class.java.name, LanguageScreen::class.java.name) }
        }
    }

    private fun AConstraintLayout.addPanelLanguage() {
        aPanelLanguage.width = 376f

        add(aPanelLanguage) {
            centerX()

            topToBottom(aPanelTop)
            bottomToTop(aDoneBtn, 8f)

            matchHeight()
        }
    }

}