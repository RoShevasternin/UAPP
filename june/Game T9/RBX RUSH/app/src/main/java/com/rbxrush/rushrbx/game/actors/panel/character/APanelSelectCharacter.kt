package com.rbxrush.rushrbx.game.actors.panel.character

import com.badlogic.gdx.math.Vector2
import com.rbxrush.rushrbx.game.actors.AScrollPane
import com.rbxrush.rushrbx.game.actors.checkbox.ACheckBox_Item
import com.rbxrush.rushrbx.game.actors.checkbox.base.ACheckBoxGroup
import com.rbxrush.rushrbx.game.actors.layout.autoLayout.AAutoLayout
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.data.ItemData
import com.rbxrush.rushrbx.game.screens.home.character.CharacterScreen
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontFactory
import com.rbxrush.rushrbx.game.utils.font.FontParameter
import com.rbxrush.rushrbx.game.utils.gdxGame
import com.rbxrush.rushrbx.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class APanelSelectCharacter(
    override val screen: AdvancedScreen,
    listItemData: List<ItemData>,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(12)

    private val labelStyle = FontFactory.create(screen, parameter, screen.fontGenerator_Fredoka_Regular)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTable = AAutoLayout(
        screen         = screen,
        direction      = AAutoLayout.Direction.HORIZONTAL,
        wrap           = true,
        sizingH        = AAutoLayout.Sizing.HUG,
        alignMain      = AAutoLayout.AlignMain.CENTER,
        paddingBottom  = 40f,
        gapMain        = 8f,
        gapCross       = 8f,
    )
    private val aScrollPane   = AScrollPane(aTable)
    private val listItemBox   = List(listItemData.size) {
        val item = listItemData[it]
        ACheckBox_Item(screen, item.name, labelStyle, item.texture, Vector2(135f, 135f), -17f)
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
            box.setSize(109f, 137f)
            add(box)

            box.checkBoxGroup = cbg
            box.setOnCheckListener { if (it) screen.animHideScreen {
                GLOBAL_SELECTED_CHARACTER_INDEX = index
                gdxGame.navigationManager.navigate(CharacterScreen::class.java.name, screen::class.java.name)
            } }
        }

    }

}