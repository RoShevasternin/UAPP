package com.rbuxrds.counter.game.screens.main

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counter.game.actors.ATmpGroup
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.panel.APanelTop
import com.rbuxrds.counter.game.utils.Block
import com.rbuxrds.counter.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counter.game.utils.actor.addActorAligned
import com.rbuxrds.counter.game.utils.actor.addActorWithConstraints
import com.rbuxrds.counter.game.utils.actor.addActors
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.actor.animDelay
import com.rbuxrds.counter.game.utils.actor.animHide
import com.rbuxrds.counter.game.utils.actor.animShow
import com.rbuxrds.counter.game.utils.actor.setOnClickListener
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame

class SettingsScreen: AdvancedScreen() {

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