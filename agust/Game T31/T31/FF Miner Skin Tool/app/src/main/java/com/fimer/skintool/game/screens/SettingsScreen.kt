package com.fimer.skintool.game.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.ATmpGroup
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.actors.panel.APanelTop
import com.fimer.skintool.game.utils.Block
import com.fimer.skintool.game.utils.TIME_ANIM_SCREEN
import com.fimer.skintool.game.utils.actor.addActors
import com.fimer.skintool.game.utils.actor.animHide
import com.fimer.skintool.game.utils.actor.animShow
import com.fimer.skintool.game.utils.actor.setOnClickListener
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame

class SettingsScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop     by lazy { APanelTop(this) }
    private val aContentGroup by lazy { ATmpGroup(this) }
    private val aSettingsImg  by lazy { Image(gdxGame.assetsAll.PANEL_SETTINGS) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

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
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 32f)
        add(aPanelTop) { centerX(); topToTop(margin = 16f) }

        aPanelTop.setTitle("SETTINGS")
    }

    private fun AConstraintLayout.addContentGroup() {
        aContentGroup.setSize(344f, 208f)
        add(aContentGroup) { centerX(); topToBottom(aPanelTop, 24f) }

        aContentGroup.addAndFillActor(aSettingsImg)

        val aShareApp = Actor()
        val aRateUs   = Actor()
        val aPrivacy  = Actor()
        aContentGroup.addActors(aShareApp, aRateUs, aPrivacy)
        aShareApp.setBounds(0f, 144f, 344f, 64f)
        aRateUs  .setBounds(0f, 72f, 344f, 64f)
        aPrivacy .setBounds(0f, 0f, 344f, 64f)

        aShareApp.setOnClickListener { gdxGame.activity.shareApp() }
        aRateUs  .setOnClickListener { gdxGame.activity.rateApp() }
        aPrivacy .setOnClickListener { gdxGame.activity.openPrivacyPolicy() }
    }

}