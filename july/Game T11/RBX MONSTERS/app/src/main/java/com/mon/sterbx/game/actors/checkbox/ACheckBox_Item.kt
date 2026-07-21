package com.mon.sterbx.game.actors.checkbox

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.checkbox.base.ACheckBox
import com.mon.sterbx.game.actors.checkbox.base.ACheckBoxStyles
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.utils.actor.setSize
import com.mon.sterbx.game.utils.advanced.AdvancedScreen

open class ACheckBox_Item(
    screen : AdvancedScreen,
    val text      : String,
    val textDesc  : String,
    val labelStyle: Label.LabelStyle,
    val labelStyleDesc: Label.LabelStyle,
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
    val lblTitle = Label(text, labelStyle)
    val lblDesc  = Label(textDesc, labelStyleDesc)
    val img = Image(texture)

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

        lblTitle.setSize(180f, 15f)
        layout.add(lblTitle) { startToStart(margin = 12f); topToTop(margin = 12f) }
        lblTitle.setEllipsis(true)

        lblDesc.setSize(220f, 34f)
        layout.add(lblDesc) { startToStart(margin = 12f); bottomToBottom(margin = 38f) }
        lblDesc.wrap = true
        lblDesc.setAlignment(Align.topLeft)

        img.setSize(size)
        layout.add(img) { endToEnd(margin = 8f); bottomToBottom(margin = 8f) }

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