package com.zahbx.blitzrbx.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.zahbx.blitzrbx.game.actors.ATmpGroup
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.actors.panel.APanelTop
import com.zahbx.blitzrbx.game.screens.LanguageScreen
import com.zahbx.blitzrbx.game.utils.Block
import com.zahbx.blitzrbx.game.utils.TIME_ANIM_SCREEN
import com.zahbx.blitzrbx.game.utils.actor.addActors
import com.zahbx.blitzrbx.game.utils.actor.animDelay
import com.zahbx.blitzrbx.game.utils.actor.animHide
import com.zahbx.blitzrbx.game.utils.actor.animShow
import com.zahbx.blitzrbx.game.utils.actor.setOnClickListener
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.gdxGame

class SettingsScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop     = APanelTop(this)
    private val aContentGroup = ATmpGroup(this)
    private val aSettingsImg  = Image(gdxGame.assetsAll.PANEL_SETTINGS)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addContentGroup()
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

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(376f, 56f)
        add(aPanelTop) {
            centerX()
            topToTop()
        }

        aPanelTop.setTitle("Settings")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addContentGroup() {
        aContentGroup.setSize(376f, 404f)
        add(aContentGroup) {
            centerX()
            topToBottom(aPanelTop)
        }

        aContentGroup.addAndFillActor(aSettingsImg)

        val aLanguage = Actor()
        val aShaeApp  = Actor()
        val aRateUs   = Actor()
        val aPrivacy  = Actor()
        aContentGroup.addActors(aLanguage, aShaeApp, aRateUs, aPrivacy)
        aLanguage.setBounds(16f, 300f, 344f, 88f)
        aShaeApp.setBounds(16f, 204f, 344f, 88f)
        aRateUs.setBounds(16f, 108f, 344f, 88f)
        aPrivacy.setBounds(16f, 12f, 344f, 88f)

        aLanguage.setOnClickListener { gdxGame.navigationManager.navigate(LanguageScreen::class.java.name, screen::class.java.name) }
        aShaeApp.setOnClickListener { gdxGame.activity.shareApp() }
        aRateUs.setOnClickListener { gdxGame.activity.rateApp() }
        aPrivacy.setOnClickListener { gdxGame.activity.openPrivacyPolicy() }
    }

}