package com.rbuxrds.counterds.game.screens.main

import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counterds.game.actors.ATmpGroup
import com.rbuxrds.counterds.game.actors.daily.ADailyConverterItem
import com.rbuxrds.counterds.game.actors.layout.AlignH
import com.rbuxrds.counterds.game.actors.layout.AlignV
import com.rbuxrds.counterds.game.actors.panel.APanelTop
import com.rbuxrds.counterds.game.utils.Block
import com.rbuxrds.counterds.game.utils.TIME_ANIM_SCREEN
import com.rbuxrds.counterds.game.utils.actor.addActorAligned
import com.rbuxrds.counterds.game.utils.actor.addActorWithConstraints
import com.rbuxrds.counterds.game.utils.actor.animDelay
import com.rbuxrds.counterds.game.utils.actor.animHide
import com.rbuxrds.counterds.game.utils.actor.animShow
import com.rbuxrds.counterds.game.utils.actor.setBounds
import com.rbuxrds.counterds.game.utils.actor.setSize
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame

class DailyConverterScreen: AdvancedScreen() {

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