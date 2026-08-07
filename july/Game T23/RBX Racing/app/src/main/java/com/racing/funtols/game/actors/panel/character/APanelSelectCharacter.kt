package com.racing.funtols.game.actors.panel.character

import com.racing.funtols.game.actors.AScrollPane
import com.racing.funtols.game.actors.checkbox.base.ACheckBoxGroup
import com.racing.funtols.game.actors.layout.autoLayout.AAutoLayout
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.data.ItemData
import com.racing.funtols.game.screens.home.character.CharacterScreen
import com.racing.funtols.game.utils.actor.disable
import com.racing.funtols.game.utils.actor.setOnClickListener
import com.racing.funtols.game.utils.actor.setOnTouchListener
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class APanelSelectCharacter(
    override val screen: AdvancedScreen,
    listItemData: List<ItemData>,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontBarlow_Bold, 14f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTable = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.VERTICAL,
        wrap      = true,
        sizingH   = AAutoLayout.Sizing.HUG,
        gapMain   = 8f,
    )
    private val aScrollPane   = AScrollPane(aTable)
    private val listItemBox   = List(listItemData.size) {
        val item = listItemData[it]
        AItem(screen, item.name, styleDef, item.texture)
    }

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
        addListBox()
    }

    private fun AAutoLayout.addListBox() {
        listItemBox.forEachIndexed { index, box ->
            box.setSize(344f, 80f)
            add(box)

            box.setOnTouchListener {
                screen.animHideScreen {
                    GLOBAL_SELECTED_CHARACTER_INDEX = index
                    gdxGame.navigationManager.navigate(CharacterScreen::class.java.name, screen::class.java.name)
                }
            }
        }

    }

}