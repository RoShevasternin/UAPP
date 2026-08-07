package com.fimer.skintool.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.label.AMsdfLabel
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.utils.NumberFormatter
import com.fimer.skintool.game.utils.actor.setOnClickListener
import com.fimer.skintool.game.utils.actor.setOnTouchListener
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.font.msdf.MsdfStyle
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelProtocol(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg = Image(gdxGame.assetsAll.PANEL_PROTOCOL)
    private val aBtn   = Actor()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        aBtn.setSize(248f, 40f)
        add(aBtn) { centerX(); bottomToBottom(margin = 24f) }
        //aBtn.setOnTouchListener {  }
    }

}