package com.coinsclub.funrbx.game.actors.panel.selector

import com.coinsclub.funrbx.game.actors.AScrollPane
import com.coinsclub.funrbx.game.actors.checkbox.ACheckBox_ItemLong
import com.coinsclub.funrbx.game.actors.layout.autoLayout.AAutoLayout
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow

class APanelSelectorVertical(
    override val screen: AdvancedScreen,
    listName: List<String>,
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
    // Actors
    // ------------------------------------------------------------------------
    private val aVertical = AAutoLayout(
        screen         = screen,
        direction      = AAutoLayout.Direction.VERTICAL,
        sizingH        = AAutoLayout.Sizing.HUG,
        alignMain      = AAutoLayout.AlignMain.CENTER,
        paddingBottom  = 40f,
        gapMain        = 6f,
    )
    private val aScrollPane   = AScrollPane(aVertical)
    private val listItemBox   = List(listName.size) { ACheckBox_ItemLong(screen, listName[it], labelStyle) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        aVertical.setUpTable()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun AAutoLayout.setUpTable() {
        aVertical.setSize(width, 1f)
        addListBox()
    }

    private fun AAutoLayout.addListBox() {
        listItemBox.forEach { box ->
            box.setSize(346f, 83f)
            add(box)

            box.setOnCheckListener { }
        }

        listItemBox.first().check()
    }

}