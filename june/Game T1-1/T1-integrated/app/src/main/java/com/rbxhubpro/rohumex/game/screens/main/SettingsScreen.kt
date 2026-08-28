package com.rbxhubpro.rohumex.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxhubpro.rohumex.businesModule.backend.Bt
import com.rbxhubpro.rohumex.game.actors.ATmpGroup
import com.rbxhubpro.rohumex.game.actors.layout.AlignH
import com.rbxhubpro.rohumex.game.actors.layout.AlignV
import com.rbxhubpro.rohumex.game.actors.panel.APanelTop
import com.rbxhubpro.rohumex.game.utils.Block
import com.rbxhubpro.rohumex.game.utils.TIME_ANIM_SCREEN
import com.rbxhubpro.rohumex.game.utils.actor.addActorAligned
import com.rbxhubpro.rohumex.game.utils.actor.addActorWithConstraints
import com.rbxhubpro.rohumex.game.utils.actor.addActors
import com.rbxhubpro.rohumex.game.utils.actor.addAndFillActor
import com.rbxhubpro.rohumex.game.utils.actor.animDelay
import com.rbxhubpro.rohumex.game.utils.actor.animHide
import com.rbxhubpro.rohumex.game.utils.actor.animShow
import com.rbxhubpro.rohumex.game.utils.actor.setOnClickListener
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.gdxGame

class SettingsScreen: AdvancedScreen() {

    override val analyticsBt    = Bt.TOOL
    override val analyticsBlock = "settings_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop    = APanelTop(this)
    private val aTmpGroup    = ATmpGroup(this)
    private val aSettingsImg = Image(gdxGame.assetsAll.PANEL_SETTINGS)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addSettings()

        animShowScreen()

        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)
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

    private fun Group.addPanelTop() {
        aPanelTop.setSize(376f, 56f)
        addActorAligned(aPanelTop, AlignH.CENTER, AlignV.TOP)
        aPanelTop.setTitle("Settings")

        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun Group.addSettings() {
        aTmpGroup.setSize(344f, 248f)
        addActorWithConstraints(aTmpGroup) {
            startToStartOf = this@addSettings
            endToEndOf     = this@addSettings
            topToBottomOf  = aPanelTop

            marginTop = 16f
        }
        aTmpGroup.addAndFillActor(aSettingsImg)

        val aShareApp = Actor()
        val aRateApp  = Actor()
        val aPrivacy  = Actor()
        aTmpGroup.addActors(aShareApp, aRateApp, aPrivacy)
        aShareApp.setBounds(0f, 176f, 344f, 72f)
        aRateApp.setBounds(0f, 88f, 344f, 72f)
        aPrivacy.setBounds(0f, 0f, 344f, 72f)

        aShareApp.setOnClickListener { gdxGame.activity.shareApp() }
        aRateApp.setOnClickListener { gdxGame.activity.rateApp() }
        aPrivacy.setOnClickListener { gdxGame.activity.openPrivacyPolicy() }
    }

}