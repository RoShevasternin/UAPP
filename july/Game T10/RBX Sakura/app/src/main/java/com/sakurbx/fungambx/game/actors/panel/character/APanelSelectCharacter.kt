package com.sakurbx.fungambx.game.actors.panel.character

import com.badlogic.gdx.math.Vector2
import com.sakurbx.fungambx.game.actors.AScrollPane
import com.sakurbx.fungambx.game.actors.checkbox.ACheckBox_Item
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBoxGroup
import com.sakurbx.fungambx.game.actors.layout.autoLayout.AAutoLayout
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.data.ItemData
import com.sakurbx.fungambx.game.screens.home.character.CharacterScreen
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.font.setDoubleShadow
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class APanelSelectCharacter(
    override val screen: AdvancedScreen,
    listItemData: List<ItemData>,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)
        .setDoubleShadow()

    private val labelStyle = FontFactory.create(screen, parameter, screen.fontGenerator_Laila_Bold)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTable = AAutoLayout(
        screen         = screen,
        direction      = AAutoLayout.Direction.HORIZONTAL,
        wrap           = true,
        sizingH        = AAutoLayout.Sizing.HUG,
        alignMain      = AAutoLayout.AlignMain.CENTER,
        paddingBottom  = 20f,
        gapMain        = 8f,
        gapCross       = 8f,
    )
    private val aScrollPane   = AScrollPane(aTable)
    private val listItemBox   = List(listItemData.size) {
        val item = listItemData[it]
        ACheckBox_Item(screen, item.name, labelStyle, item.texture, Vector2(104f, 104f), 17f)
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
            box.setSize(168f, 168f)
            add(box)

            box.checkBoxGroup = cbg
            box.setOnCheckListener { if (it) screen.animHideScreen {
                GLOBAL_SELECTED_CHARACTER_INDEX = index
                gdxGame.navigationManager.navigate(CharacterScreen::class.java.name, screen::class.java.name)
            } }
        }

    }

}