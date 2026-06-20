package com.bossrbx.rbxcalculator.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.adsmodule.AdSizeManager
import com.bossrbx.rbxcalculator.game.actors.AScrollPane
import com.bossrbx.rbxcalculator.game.actors.ATmpGroup
import com.bossrbx.rbxcalculator.game.actors.button.ABlueButton
import com.bossrbx.rbxcalculator.game.actors.checkbox.base.ACheckBox
import com.bossrbx.rbxcalculator.game.actors.checkbox.base.ACheckBoxGroup
import com.bossrbx.rbxcalculator.game.actors.checkbox.base.ACheckBoxStyles
import com.bossrbx.rbxcalculator.game.actors.layout.AlignH
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.actors.layout.linear.AVerticalGroup
import com.bossrbx.rbxcalculator.game.screens.LanguageScreen
import com.bossrbx.rbxcalculator.game.screens.onboarding.Onboarding_1_Screen
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedGroup
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.game.utils.runGDX
import com.bossrbx.rbxcalculator.util.log
import kotlinx.coroutines.launch

class APanelLanguage(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, gap = 14f, alignH = AlignH.CENTER, wrap = true)

    private val aContentGroup  = ATmpGroup(screen)
    private val aLanguageImg   = Image(gdxGame.assetsAll.LIST_LANGUAGE)
    private val listBox        = List(10) { ACheckBox(screen, ACheckBoxStyles.DEF) }

    private val aDoneBtn       = ABlueButton(screen, "DONE")
    private val aScrollPane    = AScrollPane(aVerticalGroup)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val contentW = 344f
    private val contentH = 712f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addScrollPane()
        setUpVerticalGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addScrollPane() {
        add(aScrollPane) { fillParent() }
    }

    private fun setUpVerticalGroup() {
        aVerticalGroup.setSize(width, 1f)

        val space = aScrollPane.height - contentH
        if (space > 0) aVerticalGroup.paddingBottom += space

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    if (screen.adBottomUI >= 0f) aVerticalGroup.paddingBottom += screen.adBottomUI
                    log("APanelMain adBottomUI = ${screen.adBottomUI} | banner = ${screen.adBannerUI}")
                }
            }
        }

        aVerticalGroup.also {
            it.addContentGroup()
            it.addDoneBtn()
        }

    }

    // Content Group start ------------------------------------------------------------------------

    private fun AVerticalGroup.addContentGroup() {
        aContentGroup.setSize(contentW, contentH)
        addActor(aContentGroup)

        aContentGroup.also {
            it.addAndFillActor(aLanguageImg)
            it.addListBox()
        }
    }

    private fun AdvancedGroup.addListBox() {
        var ny  = 648f
        val cbg = ACheckBoxGroup()

        listBox.forEach { box ->
            addActor(box)
            box.setBounds(0f, ny, 344f, 64f)
            ny -= 8f + 64f

            box.checkBoxGroup = cbg

            box.setOnCheckListener { if (it) aDoneBtn.enable() }
        }

        //listBox.first().check()
    }

    // Content Group end ------------------------------------------------------------------------


    private fun AVerticalGroup.addDoneBtn() {
        aDoneBtn.setSize(344f, 64f)
        addActor(aDoneBtn)
        aDoneBtn.disable()

        aDoneBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(Onboarding_1_Screen::class.java.name, LanguageScreen::class.java.name) }
        }
    }

}