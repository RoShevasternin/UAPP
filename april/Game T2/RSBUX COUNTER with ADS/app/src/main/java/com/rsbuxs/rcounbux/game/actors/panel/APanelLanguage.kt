package com.rsbuxs.rcounbux.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rsbuxs.rcounbux.game.actors.AScrollPane
import com.rsbuxs.rcounbux.game.actors.ATmpGroup
import com.rsbuxs.rcounbux.game.actors.button.AGreenButton
import com.rsbuxs.rcounbux.game.actors.checkbox.base.ACheckBox
import com.rsbuxs.rcounbux.game.actors.checkbox.base.ACheckBoxGroup
import com.rsbuxs.rcounbux.game.actors.checkbox.base.ACheckBoxStyles
import com.rsbuxs.rcounbux.game.actors.layout.AlignH
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.actors.layout.linear.AVerticalGroup
import com.rsbuxs.rcounbux.game.screens.LanguageScreen
import com.rsbuxs.rcounbux.game.screens.WelcomeScreen
import com.rsbuxs.rcounbux.game.utils.actor.disable
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedGroup
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame
import com.rsbuxs.rcounbux.util.log

class APanelLanguage(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, gap = 8f, wrap = true, alignH = AlignH.CENTER)

    private val aContentGroup  = ATmpGroup(screen)
    private val aLanguageImg   = Image(gdxGame.assetsAll.LIST_LANGUAGE)
    private val listBox        = List(10) { ACheckBox(screen, ACheckBoxStyles.GRADIENT) }

    private val aDoneBtn       = AGreenButton(screen, "Done")
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

        if (screen.adBottomUI >= 0f) aVerticalGroup.paddingBottom += screen.adBottomUI
        log("APanelMain adBottomUI = ${screen.adBottomUI}")

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
            screen.animHideScreen { gdxGame.navigationManager.navigate(WelcomeScreen::class.java.name, LanguageScreen::class.java.name) }
        }
    }

}