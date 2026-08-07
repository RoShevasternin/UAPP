package com.racing.funtols.game.actors.panel.outfit

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.actor.disable
import com.racing.funtols.game.utils.actor.setSize
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame

class ACard(
    override val screen: AdvancedScreen,
    val text      : String,
    labelStyle    : MsdfStyle,
    texture       : Texture,

    private val textureSize: Vector2,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg   = Image(gdxGame.assetsAll.ITEM_CHAR)
    private val lblTitle = AMsdfLabel(text, labelStyle)
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
        lblTitle.setSize(190f, 20f)
        add(lblTitle) { endToEnd(margin = 16f); centerY() }
        lblTitle.setAlignment(Align.right)
        lblTitle.setEllipsis(true)
    }

    private fun addImg() {
        aImg.setSize(textureSize)
        add(aImg) { startToStart(margin = 24f); centerY() }
    }

}