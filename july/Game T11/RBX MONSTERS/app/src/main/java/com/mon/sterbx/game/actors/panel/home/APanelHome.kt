package com.mon.sterbx.game.actors.panel.home

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.AScrollPane
import com.mon.sterbx.game.actors.layout.autoLayout.AAutoLayout
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.screens.home.ChestScreen
import com.mon.sterbx.game.screens.home.FreeScreen
import com.mon.sterbx.game.screens.home.QuizScreen
import com.mon.sterbx.game.screens.home.ScratchScreen
import com.mon.sterbx.game.screens.home.WheelScreen
import com.mon.sterbx.game.screens.home.character.SelectMonstersScreen
import com.mon.sterbx.game.screens.home.converter.SelectConverterScreen
import com.mon.sterbx.game.screens.home.outfit.SelectAnimationScreen
import com.mon.sterbx.game.utils.actor.setOnTouchListener
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame

class APanelHome(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDailyImg      = Image(gdxGame.assetsAll.listHomeContent[0])
    private val aConverterImg  = Image(gdxGame.assetsAll.listHomeContent[1])
    private val aPanel3        = APanel3(screen)
    private val aPanel2        = APanel2(screen)
    private val aMonstersImg   = Image(gdxGame.assetsAll.listHomeContent[4])
    private val aAnimationImg  = Image(gdxGame.assetsAll.listHomeContent[5])

    private val aVertical   = AAutoLayout(screen,
        direction     = AAutoLayout.Direction.VERTICAL,
        gapMain       = 8f,
        sizingH       = AAutoLayout.Sizing.HUG,
        alignCross    = AAutoLayout.AlignCross.CENTER,
        paddingBottom = 20f,
    )
    private val aScrollPane = AScrollPane(aVertical)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onDaily = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        setupVerticalGroup()

        with(aVertical) {
            addDaily()
            addConverter()
            addPanel3()
            addPanel2()
            addMonstersImg()
            addAnimationImg()
        }
    }

    override fun sizeChanged() {
        super.sizeChanged()
        aVertical.minH = height
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun setupVerticalGroup() {
        aVertical.width = width
    }

    private fun AAutoLayout.addDaily() {
        aDailyImg.setSize(345f, 135f)
        add(aDailyImg)

        aDailyImg.setOnTouchListener {
            aDailyImg.clickScale()
            onDaily()
        }
    }

    private fun AAutoLayout.addConverter() {
        aConverterImg.setSize(345f, 135f)
        add(aConverterImg)

        aConverterImg.setOnTouchListener {
            aConverterImg.clickScale()
            navTo(SelectConverterScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addPanel3() {
        aPanel3.setSize(345f, 129f)
        add(aPanel3)

        aPanel3.apply {
            onWheel   = { navTo(WheelScreen  ::class.java.name) }
            onScratch = { navTo(ScratchScreen::class.java.name) }
            onQuiz    = { navTo(QuizScreen   ::class.java.name) }
        }
    }

    private fun AAutoLayout.addPanel2() {
        aPanel2.setSize(345f, 129f)
        add(aPanel2)

        aPanel2.apply {
            onChest = { navTo(ChestScreen::class.java.name) }
            onFree  = { navTo(FreeScreen ::class.java.name) }
        }
    }

    private fun AAutoLayout.addMonstersImg() {
        aMonstersImg.setSize(345f, 135f)
        add(aMonstersImg)

        aMonstersImg.setOnTouchListener {
            aMonstersImg.clickScale()
            navTo(SelectMonstersScreen::class.java.name)
        }
    }

    private fun AAutoLayout.addAnimationImg() {
        aAnimationImg.setSize(345f, 135f)
        add(aAnimationImg)

        aAnimationImg.setOnTouchListener {
            aAnimationImg.clickScale()
            navTo(SelectAnimationScreen::class.java.name)
        }
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------
    private fun Image.clickScale() {
        clearActions()
        setOrigin(Align.center)
        addAction(
            Actions.sequence(
                Actions.scaleTo(0.95f, 0.95f, 0.05f, Interpolation.sineOut),   // втиснулась
                Actions.scaleTo(0.9f, 0.9f, 0.08f, Interpolation.fastSlow),   // відскок з перельотом
                Actions.scaleTo(1.00f, 1.00f, 0.07f, Interpolation.sineIn),    // осіла
            )
        )
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun navTo(screenName: String) {
        screen.animHideScreen {
            gdxGame.navigationManager.navigate(screenName, screen::class.java.name)
        }
    }

}