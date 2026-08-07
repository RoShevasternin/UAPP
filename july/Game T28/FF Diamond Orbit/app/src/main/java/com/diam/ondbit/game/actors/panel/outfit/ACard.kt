package com.diam.ondbit.game.actors.panel.outfit

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.actor.disable
import com.diam.ondbit.game.utils.actor.setSize
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame

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
        lblTitle.setSize(110f, 20f)
        add(lblTitle) { centerX(); topToTop(margin = 16f) }
        lblTitle.setAlignment(Align.center)
        lblTitle.setEllipsis(true)
    }

    private fun addImg() {
        aImg.setSize(textureSize)
        add(aImg) { centerX(); bottomToBottom(margin = 8f) }
    }

}