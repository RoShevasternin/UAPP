package com.racing.funtols.game.actors.checkbox

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

open class ACheckBox_Item(
    screen : AdvancedScreen,
    text   : String,
    style  : MsdfStyle,

    val texture   : Texture,
    val size      : Vector2,
    val margin    : Float = 0f
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
    // Fields
    // ------------------------------------------------------------------------
    private var startX = 0f
    private var startY = 0f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        val layout = AConstraintLayout(screen)
        addAndFillActor(layout)

        lblTitle.setSize(80f, 20f)
        layout.add(lblTitle) { endToEnd(margin = 57f); centerY() }
        lblTitle.setAlignment(Align.right)

        img.setSize(size)
        layout.add(img) { startToStart(margin = 23f); centerY() }

        startX = img.x
        startY = img.y
    }

    override fun onChecked() {
        super.onChecked()
        startSelectedAnim()
    }

    override fun onUnchecked() {
        super.onUnchecked()
        stopSelectedAnim()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------
    private fun startSelectedAnim() {
        img.clearActions()
        img.setOrigin(Align.center)
        img.addAction(
            Actions.parallel(
                Actions.scaleTo(1.1f, 1.1f, 0.25f, Interpolation.swingOut),
                Actions.moveTo(startX, startY + 6f, 0.25f, Interpolation.swingOut),
            )
        )
    }

    private fun stopSelectedAnim() {
        img.clearActions()
        img.addAction(
            Actions.parallel(
                Actions.scaleTo(1f, 1f, 0.2f, Interpolation.smooth),
                Actions.moveTo(startX, startY, 0.2f, Interpolation.smooth),
            )
        )
    }

}