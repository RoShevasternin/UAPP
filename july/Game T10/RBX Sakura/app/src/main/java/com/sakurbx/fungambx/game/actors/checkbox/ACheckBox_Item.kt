package com.sakurbx.fungambx.game.actors.checkbox

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBox
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBoxStyles
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.actor.setFontColor
import com.sakurbx.fungambx.game.utils.actor.setSize
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen

open class ACheckBox_Item(
    screen : AdvancedScreen,
    val text      : String,
    val labelStyle: Label.LabelStyle,
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
    val lbl = Label(text, labelStyle)
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

        lbl.setSize(115f, 25f)
        layout.add(lbl) { centerX(); topToTop(margin = 12f) }
        lbl.setAlignment(Align.center)
        lbl.setEllipsis(true)

        img.setSize(size)
        layout.add(img) { centerX(); bottomToBottom(margin = this@ACheckBox_Item.margin) }

        startX = img.x
        startY = img.y
    }

    override fun onChecked() {
        super.onChecked()
        lbl.setFontColor(GameColor.brown_B95A02)
        startSelectedAnim()
    }

    override fun onUnchecked() {
        super.onUnchecked()
        lbl.setFontColor(Color.WHITE)
        stopSelectedAnim()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------
    private fun startSelectedAnim() {
        img.clearActions()
        img.setOrigin(Align.center)
        img.addAction(
            Actions.sequence(
                Actions.scaleTo(1.1f, 1.1f, 0.2f, Interpolation.swingOut),
                Actions.forever(Actions.sequence(
                    Actions.scaleTo(1.05f, 1.05f, 0.7f, Interpolation.sine),
                    Actions.scaleTo(1.1f, 1.1f, 0.7f, Interpolation.sine),
                ))
            )
        )
    }

    private fun stopSelectedAnim() {
        img.clearActions()

        img.addAction(
            Actions.parallel(
                Actions.moveTo(startX, startY, 0.2f, Interpolation.smooth),
                Actions.scaleTo(1f, 1f, 0.2f, Interpolation.smooth),
                Actions.rotateTo(0f, 0.2f, Interpolation.smooth),   // повертаємо рівно
            )
        )
    }

}