package com.rbuxdrop.cougame.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxdrop.cougame.adsmodule.AdSizeManager
import com.rbuxdrop.cougame.game.actors.AScrollPane
import com.rbuxdrop.cougame.game.actors.ATmpGroup
import com.rbuxdrop.cougame.game.actors.button.APurpleButton
import com.rbuxdrop.cougame.game.actors.checkbox.base.ACheckBox
import com.rbuxdrop.cougame.game.actors.checkbox.base.ACheckBoxGroup
import com.rbuxdrop.cougame.game.actors.checkbox.base.ACheckBoxStyles
import com.rbuxdrop.cougame.game.actors.layout.AlignH
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.actors.layout.linear.AVerticalGroup
import com.rbuxdrop.cougame.game.screens.LanguageScreen
import com.rbuxdrop.cougame.game.screens.OnboardingScreen
import com.rbuxdrop.cougame.game.utils.actor.disable
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedGroup
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.game.utils.runGDX
import com.rbuxdrop.cougame.util.log
import kotlinx.coroutines.launch

class APanelLanguage(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, gap = 8f, wrap = true, alignH = AlignH.CENTER)

    private val aContentGroup  = ATmpGroup(screen)
    private val aLanguageImg   = Image(gdxGame.assetsAll.LIST_LANGUAGE)
    private val listBox        = List(10) { ACheckBox(screen, ACheckBoxStyles.GRADIENT) }

    private val aDoneBtn       = APurpleButton(screen, "Done")
    private val aScrollPane    = AScrollPane(aVerticalGroup)

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
        aVerticalGroup.width = 376f

        aVerticalGroup.addUpContentGroup()
        aVerticalGroup.addDoneBtn()

        aVerticalGroup.paddingBottom = 20f

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    if (screen.adBottomUI >= 0f) aVerticalGroup.paddingBottom += screen.adBottomUI
                    log("APanelLanguage adBottomUI = ${screen.adBottomUI} | banner = ${screen.safeBannerUI}")
                }
            }
        }

    }

    // Content Group start ------------------------------------------------------------------------

    private fun AVerticalGroup.addUpContentGroup() {
        aContentGroup.setSize(376f, 744f)
        addActor(aContentGroup)

        aContentGroup.also {
            it.addListBox()
            it.addAndFillActor(aLanguageImg)
        }

        aLanguageImg.disable()
    }

    private fun AdvancedGroup.addListBox() {
        var ny  = 664f
        val cbg = ACheckBoxGroup()

        listBox.forEach { box ->
            addActor(box)
            box.setBounds(16f, ny, 344f, 64f)
            ny -= 8f + 64f

            box.checkBoxGroup = cbg

            box.setOnCheckListener { }
        }

        listBox.first().check()
    }

    // Content Group end ------------------------------------------------------------------------


    private fun AVerticalGroup.addDoneBtn() {
        aDoneBtn.setSize(344f, 60f)
        addActor(aDoneBtn)

        aDoneBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(OnboardingScreen::class.java.name, LanguageScreen::class.java.name) }
        }
    }

}