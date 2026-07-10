package com.sakurbx.fungambx.game.actors.panel.outfit

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.actor.setSize
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame

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
        aLbl.setSize(94f, 13f)
        add(aLbl) { centerX(); bottomToBottom(margin = 8f) }
        aLbl.setAlignment(Align.center)
        aLbl.setEllipsis(true)
    }

    private fun addImg() {
        aImg.setSize(textureSize)
        add(aImg) { centerX(); topToTop() }
    }

}