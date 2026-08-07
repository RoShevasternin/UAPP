package com.fimer.skintool.game.actors.panel.select

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.AScrollPane
import com.fimer.skintool.game.actors.ATmpGroup
import com.fimer.skintool.game.actors.layout.autoLayout.AAutoLayout
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.utils.actor.setOnTouchListener
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.global.GLOBAL_SCREEN_NAME

class APanelSelectSkin(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTable = AAutoLayout(
        screen        = screen,
        direction     = AAutoLayout.Direction.VERTICAL,
        sizingH       = AAutoLayout.Sizing.HUG,
        gapMain       = 8f,
        paddingBottom = 20f
    )

    private val aScrollPane = AScrollPane(aTable)

    private val aGroup   = ATmpGroup(screen)
    private val aContent = Image(gdxGame.assetsAll.listSelectContent[3])

    private val aTableItem = AAutoLayout(
        screen        = screen,
        direction     = AAutoLayout.Direction.HORIZONTAL,
        wrap          = true,
        sizingH       = AAutoLayout.Sizing.HUG,
        gapMain       = 8f,
        gapCross      = 8f,
    )
    private val listItemBox = List(3) { Actor() }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        aTable.setUpTable()
    }

    override fun sizeChanged() {
        super.sizeChanged()
        aTable.minH = height
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun AAutoLayout.setUpTable() {
        aTable.width = width
        addContent()
    }

    private fun AAutoLayout.addContent() {
        aGroup.setSize(344f, 328f)
        add(aGroup)

        aGroup.addAndFillActor(aContent)
        aGroup.addAndFillActor(aTableItem)
        aTableItem.addListBox()
    }

    private fun AAutoLayout.addListBox() {
        listItemBox.forEachIndexed { index, box ->
            box.setSize(344f, 104f)
            add(box)

            box.setOnTouchListener {
                screen.animHideScreen {
                    gdxGame.navigationManager.navigate(GLOBAL_SCREEN_NAME, screen::class.java.name)
                }
            }

        }
    }

}