package com.zahbx.blitzrbx.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.screens.main.NtoRBXScreen
import com.zahbx.blitzrbx.game.utils.GLOBAL_SELECTED_RBX_CALCULATOR_TITLE
import com.zahbx.blitzrbx.game.utils.actor.setOnClickListener
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.gdxGame

class APanel_N_to_RBX(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg  = Image(gdxGame.assetsAll.PANEL_N_TO_RBX)
    private val listBtn    = List(3) { Actor() }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelImg()
        addListBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelImg() {
        add(aPanelImg) { fillParent() }
    }

    private fun addListBtn() {
        var ny  = 208f

        val listTitle = listOf(
            "BC to RBX",
            "TBC to RBX",
            "OBC to RBX",
        )

        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(16f, ny, 344f, 88f)
            ny -= 8f + 88f

            btn.setOnClickListener {
                GLOBAL_SELECTED_RBX_CALCULATOR_TITLE = listTitle[index]
                screen.animHideScreen { gdxGame.navigationManager.navigate(NtoRBXScreen::class.java.name, screen::class.java.name) }
            }
        }
    }

}