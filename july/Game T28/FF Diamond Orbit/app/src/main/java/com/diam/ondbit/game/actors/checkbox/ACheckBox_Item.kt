package com.diam.ondbit.game.actors.checkbox

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.checkbox.base.ACheckBox
import com.diam.ondbit.game.actors.checkbox.base.ACheckBoxStyles
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle

open class ACheckBox_Item(
    screen : AdvancedScreen,
    text   : String,
    style  : MsdfStyle,

    val texture: Texture,
) : ACheckBox(
    screen    = screen,
    style     = ACheckBoxStyles.ITEM,
) {

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

        val layout = AConstraintLayout(screen)
        addAndFillActor(layout)

        lblTitle.setSize(110f, 18f)
        layout.add(lblTitle) { centerX(); topToTop(margin = 16f) }
        lblTitle.setAlignment(Align.center)

        img.setSize(147f, 147f)
        layout.add(img) { centerX(); bottomToBottom(margin = 7f) }
    }

}