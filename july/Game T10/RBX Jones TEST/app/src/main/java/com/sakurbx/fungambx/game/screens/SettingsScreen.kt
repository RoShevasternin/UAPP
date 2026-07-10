package com.sakurbx.fungambx.game.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.sakurbx.fungambx.game.actors.ATmpGroup
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBox
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBoxStyles
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.APanelTop
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.actor.addActors
import com.sakurbx.fungambx.game.utils.actor.animDelay
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.actor.setOnClickListener
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame

class SettingsScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop     by lazy { APanelTop(this) }
    private val aContentGroup by lazy { ATmpGroup(this) }
    private val aSettingsImg  by lazy { Image(gdxGame.assetsAll.PANEL_SETTINGS) }
    //private val aMusicBox     by lazy { ACheckBox(this, ACheckBoxStyles.BOX_DEF) }

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
        //addMusicBox()
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
        aPanelTop.height = 72f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle("Settings")
    }

    private fun AConstraintLayout.addContentGroup() {
        aContentGroup.setSize(344f, 208f)
        add(aContentGroup) { centerX(); topToBottom(aPanelTop, 16f) }

        aContentGroup.addAndFillActor(aSettingsImg)

        val aShareApp = Actor()
        val aRateUs   = Actor()
        val aPrivacy  = Actor()
        aContentGroup.addActors(aShareApp, aRateUs, aPrivacy)
        aShareApp.setBounds(0f, 144f, 344f, 64f)
        aRateUs.setBounds(0f, 72f, 344f, 64f)
        aPrivacy.setBounds(0f, 0f, 344f, 64f)

        aShareApp.setOnClickListener { gdxGame.activity.shareApp() }
        aRateUs  .setOnClickListener { gdxGame.activity.rateApp() }
        aPrivacy .setOnClickListener { gdxGame.activity.openPrivacyPolicy() }
    }

//    private fun AConstraintLayout.addMusicBox() {
//        aMusicBox.setSize(344f, 56f)
//        add(aMusicBox) { centerX(); topToBottom(aContentGroup) }
//
//        if (gdxGame.musicUtil.currentMusic?.isPlaying == true) aMusicBox.check(false)
//
//        aMusicBox.setOnCheckListener { isCheck ->
//            if (isCheck) gdxGame.musicUtil.currentMusic?.play() else gdxGame.musicUtil.currentMusic?.pause()
//        }
//    }

}