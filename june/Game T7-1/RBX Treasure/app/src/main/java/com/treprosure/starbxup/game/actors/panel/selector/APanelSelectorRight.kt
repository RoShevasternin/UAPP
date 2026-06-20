package com.treprosure.starbxup.game.actors.panel.selector

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.treprosure.starbxup.game.actors.AScrollPane
import com.treprosure.starbxup.game.actors.ATmpGroup
import com.treprosure.starbxup.game.actors.checkbox.base.ACheckBox
import com.treprosure.starbxup.game.actors.checkbox.base.ACheckBoxGroup
import com.treprosure.starbxup.game.actors.checkbox.base.ACheckBoxStyles
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.utils.advanced.AdvancedGroup
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen

class APanelSelectorRight(
    override val screen: AdvancedScreen,
    texture: Texture,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentGroup = ATmpGroup(screen)
    private val aContentImg   = Image(texture)
    private val listBox       = List(8) { ACheckBox(screen, ACheckBoxStyles.BOX_DEF) }
    private val aScrollPane   = AScrollPane(aContentGroup)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        setUpContentGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun setUpContentGroup() {
        aContentGroup.width  = 366f
        aContentGroup.height = (643f + 20f).coerceAtLeast(height)

        aContentGroup.also {
            aContentImg.setBounds(0f, 20f, 366f, 643f)
            it.addActor(aContentImg)
            it.addListBox()
        }
    }

    private fun AdvancedGroup.addListBox() {
        var ny  = 571f + 20f

        listBox.forEach { box ->
            addActor(box)
            box.setBounds(302f, ny, 50f, 50f)
            ny -= 30f + 50f

            box.setOnCheckListener { }
        }

        listBox.first().check()
    }

}