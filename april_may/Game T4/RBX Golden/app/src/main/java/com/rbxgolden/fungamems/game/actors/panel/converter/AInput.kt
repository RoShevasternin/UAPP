package com.rbxgolden.fungamems.game.actors.panel.converter

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.NumberFormatter
import com.rbxgolden.fungamems.game.utils.actor.setFontColor
import com.rbxgolden.fungamems.game.utils.actor.setOnClickListener
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontParameter
import com.rbxgolden.fungamems.game.utils.gdxGame

class AInput(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aInputImg   = Image(gdxGame.assetsAll.INPUT)
    private val aLbl        = Label("Enter here", FontFactory.create(screen, parameter, screen.fontGenerator_Medium, GameColor.gray_5C))
    private val aRestartBtn = Actor()

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onInput: (Int) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addInputImg()
        addLbl()
        addRestartBtn()

        setOnClickListener {
            gdxGame.activity.showInput { value ->
                val text = NumberFormatter.formatDollars(value.toDouble())
                aLbl.setText(text)
                aLbl.setFontColor(Color.WHITE)

                onInput(value)
            }
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addInputImg() {
        add(aInputImg) { fillParent() }
    }

    private fun addLbl() {
        aLbl.setSize(104f, 24f)
        add(aLbl) { startToStart(margin = 16f); centerY() }
    }

    private fun addRestartBtn() {
        aRestartBtn.setSize(50f, 50f)
        add(aRestartBtn) { endToEnd(margin = 3f); centerY() }
        aRestartBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(screen::class.java.name) }
        }
    }

}