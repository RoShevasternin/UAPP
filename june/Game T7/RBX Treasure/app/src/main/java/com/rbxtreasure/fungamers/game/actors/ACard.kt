package com.rbxtreasure.fungamers.game.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.utils.actor.setSize
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.gdxGame

class ACard(
    override val screen: AdvancedScreen,
    text      : String,
    labelStyle: Label.LabelStyle,
    texture   : Texture,

    private val textureSize: Vector2,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg = Image(gdxGame.assetsAll.PANEL_ITEM)
    private val aLbl   = Label(text, labelStyle)
    private val aImg   = Image(texture)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addLbl()
        addImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLbl() {
        aLbl.setSize(180f, 24f)
        add(aLbl) { startToStart(margin = 16f); centerY() }
        aLbl.setEllipsis(true)
    }

    private fun addImg() {
        aImg.setSize(textureSize)
        add(aImg) { endToEnd(margin = 16f); centerY() }
    }

}