package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxgolden.fungamems.game.actors.button.base.AButtonAnim
import com.rbxgolden.fungamems.game.actors.button.base.AButtonStyles
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontGenerator
import com.rbxgolden.fungamems.game.utils.font.FontParameter
import kotlin.math.roundToInt

class APanelTop(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackBtn  = AButtonAnim(screen, AButtonStyles.Anim.BACK)
    private val aTitleLbl = Label("", FontFactory.create(screen, parameter, screen.fontGenerator_Bold, Color.WHITE))

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onBack = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addTitleLbl()
        addBackBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBackBtn() {
        aBackBtn.setSize(56f, 56f)
        add(aBackBtn) { startToStart(); bottomToBottom() }
        aBackBtn.setOnClickListener { onBack() }
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(108f, 24f)
        add(aTitleLbl) { center() }
        aTitleLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

}