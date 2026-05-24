package com.bossrbx.rbxcalculator.game.actors.panel.wheel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.NumberFormatter
import com.bossrbx.rbxcalculator.game.utils.actor.animShow
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelSpin(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    private val spinCount        = 5
    private var currentSpinCount = 0

    val isSpin get() = currentSpinCount < spinCount

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.PANEL_SPIN)
    private val listImg   = List(spinCount) { Image(screen.drawerUtil.getTexture(GameColor.green_55BF40)) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPanelImg) { fillParent() }
        addListImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addListImg() {
        var nx = 157f
        listImg.forEachIndexed { index, img ->
            addActor(img)
            img.setBounds(nx, 82f, 8f, 8f)

            nx += 8f + 8f

            img.color.a = 0f
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun markSpin() {
        currentSpinCount++
        val index = currentSpinCount.dec().coerceAtMost(listImg.lastIndex)
        listImg[index].animShow(0.25f)
    }

}