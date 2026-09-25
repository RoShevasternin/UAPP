package com.zahbx.blitzrbx.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.zahbx.blitzrbx.adsmodule.AdSizeManager
import com.zahbx.blitzrbx.game.actors.AScrollPane
import com.zahbx.blitzrbx.game.actors.ATmpGroup
import com.zahbx.blitzrbx.game.actors.button.AGreenButton
import com.zahbx.blitzrbx.game.actors.checkbox.base.ACheckBox
import com.zahbx.blitzrbx.game.actors.checkbox.base.ACheckBoxGroup
import com.zahbx.blitzrbx.game.actors.checkbox.base.ACheckBoxStyles
import com.zahbx.blitzrbx.game.actors.layout.AlignH
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.actors.layout.linear.AVerticalGroup
import com.zahbx.blitzrbx.game.screens.LanguageScreen
import com.zahbx.blitzrbx.game.screens.MainScreen
import com.zahbx.blitzrbx.game.utils.actor.disable
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedGroup
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.gdxGame
import com.zahbx.blitzrbx.game.utils.runGDX
import com.zahbx.blitzrbx.util.log
import kotlinx.coroutines.launch

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

        // ⚠️ «=», а не «+=»: adBottomFlow це StateFlow — з «+=» відступ
        // накопичувався б з кожною емісією (порожнеча під списком росла).
        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX {
                aVerticalGroup.paddingBottom = screen.adBottomUI.coerceAtLeast(0f)
                log("APanelLanguage adBottomUI = ${screen.adBottomUI}")
            } }
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
            screen.animHideScreen { gdxGame.navigationManager.navigate(MainScreen::class.java.name) }
        }
    }

}