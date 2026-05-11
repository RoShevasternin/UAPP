package com.rsbuxs.rcounbux.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rsbuxs.rcounbux.game.actors.AScrollPane
import com.rsbuxs.rcounbux.game.actors.ATmpGroup
import com.rsbuxs.rcounbux.game.actors.checkbox.base.ACheckBox
import com.rsbuxs.rcounbux.game.actors.checkbox.base.ACheckBoxGroup
import com.rsbuxs.rcounbux.game.actors.checkbox.base.ACheckBoxStyles
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.utils.actor.disable
import com.rsbuxs.rcounbux.game.utils.actor.setBounds
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedGroup
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame

class APanelLanguage(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentGroup = ATmpGroup(screen)
    private val aLanguageImg  = Image(gdxGame.assetsAll.LIST_LANGUAGE)
    private val listBox       = List(10) { ACheckBox(screen, ACheckBoxStyles.GRADIENT) }
    private val aScrollPane   = AScrollPane(aContentGroup)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addScrollPane()
        setUpContentGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addScrollPane() {
        add(aScrollPane) { fillParent() }
    }

    // Content Group ------------------------------------------------------------------------
    private fun setUpContentGroup() {
        aContentGroup.setSize(376f, 744f)
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

}