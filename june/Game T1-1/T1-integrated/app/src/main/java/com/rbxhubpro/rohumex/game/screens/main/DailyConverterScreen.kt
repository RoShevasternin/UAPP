package com.rbxhubpro.rohumex.game.screens.main

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.rbxhubpro.rohumex.businesModule.backend.Bt
import com.rbxhubpro.rohumex.game.actors.ATmpGroup
import com.rbxhubpro.rohumex.game.actors.daily.ADailyConverterItem
import com.rbxhubpro.rohumex.game.actors.layout.AlignH
import com.rbxhubpro.rohumex.game.actors.layout.AlignV
import com.rbxhubpro.rohumex.game.actors.panel.APanelTop
import com.rbxhubpro.rohumex.game.utils.Block
import com.rbxhubpro.rohumex.game.utils.TIME_ANIM_SCREEN
import com.rbxhubpro.rohumex.game.utils.actor.addActorAligned
import com.rbxhubpro.rohumex.game.utils.actor.addActorWithConstraints
import com.rbxhubpro.rohumex.game.utils.actor.animDelay
import com.rbxhubpro.rohumex.game.utils.actor.animHide
import com.rbxhubpro.rohumex.game.utils.actor.animShow
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.gdxGame

class DailyConverterScreen: AdvancedScreen() {

    override val analyticsBt    = Bt.TOOL
    override val analyticsBlock = "daily_converter_screen"

    private val listTitle = listOf(
        "Daily Free Rbx Calculator",
        "RBX To Dollar Calculator",
        "Dollar To RBX Calculator",
        "Quiz Time to Earn Coin",
    )

    private val listToScreenName = listOf(
        DailyFreeRbxCalculatorScreen::class.java.name,
        DailyFreeRbxCalculatorScreen::class.java.name,
        DailyFreeRbxCalculatorScreen::class.java.name,
        LogicQuizTimeScreen::class.java.name,
    )

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop = APanelTop(this)
    private val listItem  = List(listTitle.size) { ADailyConverterItem(this, listTitle[it]) }

    private val aContentGroup = ATmpGroup(this)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        super.show()
    }

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addContentGroup()

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
        aPanelTop.setTitle("Daily R$ Converter")

        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun Group.addContentGroup() {
        aContentGroup.setSize(344f, 336f)
        addActorWithConstraints(aContentGroup) {
            startToStartOf = this@addContentGroup
            endToEndOf     = this@addContentGroup
            topToBottomOf  = aPanelTop

            marginTop = 16f
        }

        aContentGroup.addListItem()
    }

    private fun Group.addListItem() {
        var ny = 264f
        listItem.forEachIndexed { index, item ->
            addActor(item)
            item.setBounds(0f, ny, 344f, 72f)
            ny -= 16f + 72f

            item.onClick = {
                animHideScreen {
                    gdxGame.navigationManager.navigate(listToScreenName[index], this@DailyConverterScreen::class.java.name)
                }
            }
        }
    }

}