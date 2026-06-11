package com.skindustry.skinly.game.screens

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.skindustry.skinly.game.actors.ATmpGroup
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBox
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBoxStyles
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.actors.panel.APanelTopSettings
import com.skindustry.skinly.game.utils.Block
import com.skindustry.skinly.game.utils.TIME_ANIM_SCREEN
import com.skindustry.skinly.game.utils.actor.addActors
import com.skindustry.skinly.game.utils.actor.animDelay
import com.skindustry.skinly.game.utils.actor.animHide
import com.skindustry.skinly.game.utils.actor.animShow
import com.skindustry.skinly.game.utils.actor.setOnClickListener
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class SettingsScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop     by lazy { APanelTopSettings(this) }
    private val aContentGroup by lazy { ATmpGroup(this) }
    private val aSettingsImg  by lazy { Image(gdxGame.assetsAll.PANEL_SETTINGS) }
    private val aMusicBox     by lazy { ACheckBox(this, ACheckBoxStyles.MUSIC) }

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
        addMusicBox()
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
        aPanelTop.setSize(WIDTH, 69f)
        add(aPanelTop) { centerX(); topToTop() }
    }

    private fun AConstraintLayout.addContentGroup() {
        aContentGroup.setSize(WIDTH, 232f)
        add(aContentGroup) { centerX(); topToBottom(aPanelTop) }

        aContentGroup.addAndFillActor(aSettingsImg)

        val aRateUs   = Actor()
        val aShareApp = Actor()
        val aPrivacy  = Actor()
        aContentGroup.addActors(aShareApp, aRateUs, aPrivacy)
        aRateUs.setBounds(16f, 160f, 344f, 56f)
        aShareApp.setBounds(16f, 88f, 344f, 56f)
        aPrivacy.setBounds(16f, 16f, 344f, 56f)

        aRateUs  .setOnClickListener { gdxGame.activity.rateApp() }
        aShareApp.setOnClickListener { gdxGame.activity.shareApp() }
        aPrivacy .setOnClickListener { gdxGame.activity.openPrivacyPolicy() }
    }

    private fun AConstraintLayout.addMusicBox() {
        aMusicBox.setSize(344f, 56f)
        add(aMusicBox) { centerX(); topToBottom(aContentGroup) }

        if (gdxGame.musicUtil.currentMusic?.isPlaying == true) aMusicBox.check(false)

        aMusicBox.setOnCheckListener { isCheck ->
            if (isCheck) gdxGame.musicUtil.currentMusic?.play() else gdxGame.musicUtil.currentMusic?.pause()
        }
    }

}