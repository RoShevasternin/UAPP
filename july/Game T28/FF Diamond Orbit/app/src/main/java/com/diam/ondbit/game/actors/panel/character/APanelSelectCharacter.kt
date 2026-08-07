package com.diam.ondbit.game.actors.panel.character

import com.diam.ondbit.game.actors.AScrollPane
import com.diam.ondbit.game.actors.checkbox.ACheckBox_Item
import com.diam.ondbit.game.actors.checkbox.base.ACheckBoxGroup
import com.diam.ondbit.game.actors.layout.autoLayout.AAutoLayout
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.data.ItemData
import com.diam.ondbit.game.screens.home.character.CharacterScreen
import com.diam.ondbit.game.utils.actor.setOnTouchListener
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class APanelSelectCharacter(
    override val screen: AdvancedScreen,
    listItemData: List<ItemData>,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontSpaceGrotesk_Medium, 18f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTable = AAutoLayout(
        screen         = screen,
        direction      = AAutoLayout.Direction.HORIZONTAL,
        wrap           = true,
        sizingH        = AAutoLayout.Sizing.HUG,
        gapMain        = 8f,
        gapCross       = 8f,
    )
    private val aScrollPane   = AScrollPane(aTable)
    private val listItemBox   = List(listItemData.size) {
        val item = listItemData[it]
        ACheckBox_Item(screen, item.name, styleDef, item.texture)
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
        val cbg = ACheckBoxGroup()

        listItemBox.forEachIndexed { index, box ->
            box.setSize(168f, 206f)
            add(box)

            box.checkBoxGroup = cbg
            box.setOnCheckListener {
                if (it) {
                    GLOBAL_SELECTED_CHARACTER_INDEX = index
                    screen.animHideScreen { gdxGame.navigationManager.navigate(CharacterScreen::class.java.name, screen::class.java.name) }
                }
            }
        }

    }

}