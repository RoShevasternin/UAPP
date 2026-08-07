package com.racing.funtols.game.actors.panel.character

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.checkbox.base.ACheckBox
import com.racing.funtols.game.actors.checkbox.base.ACheckBoxStyles
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.actor.setSize
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame

open class AItem(
    screen : AdvancedScreen,
    text   : String,
    style  : MsdfStyle,
    texture: Texture,
) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    val lblTitle = AMsdfLabel(text, style)
    val img      = Image(texture)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        add(Image(gdxGame.assetsAll.ITEM_CHAR)) { fillParent() }

        lblTitle.setSize(80f, 20f)
        add(lblTitle) { endToEnd(margin = 16f); centerY() }
        lblTitle.setAlignment(Align.right)

        img.setSize(80f, 85f)
        add(img) { startToStart(margin = 24f); centerY() }
    }

}