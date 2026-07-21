package com.mon.sterbx.game.actors.panel.outfit

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.utils.actor.disable
import com.mon.sterbx.game.utils.actor.setSize
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame

class ACard(
    override val screen: AdvancedScreen,
    val text      : String,
    textDesc      : String,
    labelStyle    : Label.LabelStyle,
    labelStyleDesc: Label.LabelStyle,
    texture       : Texture,

    private val textureSize: Vector2,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg   = Image(gdxGame.assetsAll.PANEL_ITEM)
    private val lblTitle = Label(text, labelStyle)
    private val lblDesc  = Label(textDesc, labelStyleDesc)
    private val aImg     = Image(texture)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        disable()

        add(aBgImg) { fillParent() }
        addLbl()
        addImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLbl() {
        lblTitle.setSize(180f, 15f)
        add(lblTitle) { startToStart(margin = 12f); topToTop(margin = 12f) }
        lblTitle.setEllipsis(true)

        lblDesc.setSize(220f, 34f)
        add(lblDesc) { startToStart(margin = 12f); bottomToBottom(margin = 38f) }
        lblDesc.wrap = true
        lblDesc.setAlignment(Align.topLeft)
    }

    private fun addImg() {
        aImg.setSize(textureSize)
        add(aImg) { endToEnd(margin = 8f); bottomToBottom(margin = 8f) }
    }

}