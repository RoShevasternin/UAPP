package com.coinsclub.funrbx.game.actors.panel.character

import com.badlogic.gdx.math.Vector2
import com.coinsclub.funrbx.game.actors.AScrollPane
import com.coinsclub.funrbx.game.actors.checkbox.base.ACheckBoxGroup
import com.coinsclub.funrbx.game.actors.checkbox.ACheckBox_Item
import com.coinsclub.funrbx.game.actors.layout.autoLayout.AAutoLayout
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.data.ItemData
import com.coinsclub.funrbx.game.screens.home.character.CharacterScreen
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.global.GLOBAL_SELECTED_CHARACTER_INDEX

class APanelSelectCharacter(
    override val screen: AdvancedScreen,
    listItemData: List<ItemData>,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(18)
        .setBorderAndShadow()

    private val labelStyle = FontFactory.create(screen, parameter, screen.fontGenerator_LuckiestGuy_Regular, GameColor.white_FFF5E3)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val vSize = Vector2(109f, 109f)

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
        gapMain        = 6f,
        gapCross       = 6f,
    )
    private val aScrollPane   = AScrollPane(aTable)
    private val listItemBox   = List(listItemData.size) {
        val item = listItemData[it]
        ACheckBox_Item(screen, item.name, labelStyle, item.texture, vSize)
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        aTable.setUpTable()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun AAutoLayout.setUpTable() {
        aTable.setSize(width, 1f)
        addListBox()
    }

    private fun AAutoLayout.addListBox() {
        val cbg = ACheckBoxGroup()

        listItemBox.forEachIndexed { index, box ->
            box.setSize(170f, 165f)
            add(box)

            box.checkBoxGroup = cbg
            box.setOnCheckListener { if (it) screen.animHideScreen {
                GLOBAL_SELECTED_CHARACTER_INDEX = index
                gdxGame.navigationManager.navigate(CharacterScreen::class.java.name, screen::class.java.name)
            } }
        }

    }

}