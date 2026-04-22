package com.rbuxrds.counter.game.actors.daily

import com.badlogic.gdx.scenes.scene2d.Group
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

class ADailyFreeRbxCalculatorSelect(
    override val screen: AdvancedScreen,
): AdvancedGroup() {

    private val listTitle = listOf(
        "Daily Free Rbx Calculator",
        "TBC RBX Counter",
        "OBC RBX Counter",
    )

    private var selectedText = ""

    var onSelect: (String) -> Unit = {}

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listItem = List(listTitle.size) { ADailyFreeRbxCalculatorItem(screen, listTitle[it]) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addListItem()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun Group.addListItem() {
        var ny = 192f
        listItem.forEachIndexed { index, item ->
            addActor(item)
            item.setBounds(0f, ny, 344f, 80f)
            ny -= 16f + 80f

            item.onClick = {
                selectedText = listTitle[index]
                onSelect(selectedText)
            }
        }
    }

}