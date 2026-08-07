package com.diam.ondbit.game.actors.panel.map

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.diam.ondbit.game.actors.layout.autoLayout.AAutoLayout
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.screens.home.map.MapScreen
import com.diam.ondbit.game.utils.actor.setOnClickListener
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.global.GLOBAL_SELECTED_MAP_INDEX

class APanelSelectMap(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aListMap = List(6) { Actor() }

    private val aTable = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        wrap      = true,
        gapMain   = 8f,
        gapCross  = 32f
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addTable()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addTable() {
        add(Image(gdxGame.assetsAll.MAP)) {  fillParent()}
        add(aTable) { fillParent() }

        aListMap.forEachIndexed { index, map ->
            map.setSize(168f, 168f)
            aTable.add(map)

            map.setOnClickListener {
                GLOBAL_SELECTED_MAP_INDEX = index
                screen.animHideScreen { gdxGame.navigationManager.navigate(MapScreen::class.java.name, screen::class.java.name) }
            }
        }
    }

}