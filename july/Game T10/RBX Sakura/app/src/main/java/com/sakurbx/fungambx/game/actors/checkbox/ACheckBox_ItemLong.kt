package com.sakurbx.fungambx.game.actors.checkbox

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBox
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBoxStyles
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.actor.setFontColor
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen

open class ACheckBox_ItemLong(
    screen : AdvancedScreen,
    val icon      : Texture,
    val text      : String,
    val labelStyle: Label.LabelStyle,
) : ACheckBox(
    screen    = screen,
    style     = ACheckBoxStyles.ITEM_LONG,
) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    val img = Image(icon)
    val lbl = Label(text, labelStyle)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        val layout = AConstraintLayout(screen)
        addAndFillActor(layout)

        img.setSize(36f, 36f)
        layout.add(img) { startToStart(margin = 18f); centerY() }

        lbl.setSize(230f, 25f)
        layout.add(lbl) { startToEnd(img, margin = 8f); centerY() }
        lbl.setEllipsis(true)
    }

    override fun onChecked() {
        super.onChecked()
        lbl.setFontColor(GameColor.brown_B95A02)
    }

    override fun onUnchecked() {
        super.onUnchecked()
        lbl.setFontColor(Color.WHITE)
    }

}