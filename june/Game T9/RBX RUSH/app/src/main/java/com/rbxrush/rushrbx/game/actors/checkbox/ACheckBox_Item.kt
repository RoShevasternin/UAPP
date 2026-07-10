package com.rbxrush.rushrbx.game.actors.checkbox

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.game.actors.checkbox.base.ACheckBox
import com.rbxrush.rushrbx.game.actors.checkbox.base.ACheckBoxStyles
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.actor.setFontColor
import com.rbxrush.rushrbx.game.utils.actor.setSize
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen

open class ACheckBox_Item(
    screen : AdvancedScreen,
    val text      : String,
    val labelStyle: Label.LabelStyle,
    val texture   : Texture,
    val size      : Vector2,
    val marginTop : Float = 0f
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

        lbl.setSize(93f, 13f)
        layout.add(lbl) { centerX(); bottomToBottom(margin = 9f) }
        lbl.setAlignment(Align.center)
        lbl.setEllipsis(true)

        img.setSize(size)
        layout.add(img) { centerX(); topToTop(margin = this@ACheckBox_Item.marginTop) }

        startX = img.x
        startY = img.y
    }

    override fun onChecked() {
        super.onChecked()
        lbl.setFontColor(GameColor.black_2C2C2C)
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
        img.rotation = 0f

        img.addAction(
            Actions.sequence(
                // замах в один бік (половина амплітуди), далі — повне погойдування
                Actions.rotateTo(6f, 0.3f, Interpolation.sine),
                Actions.forever(
                    Actions.sequence(
                        Actions.rotateTo(-6f, 0.6f, Interpolation.sine),
                        Actions.rotateTo( 6f, 0.6f, Interpolation.sine),
                    )
                )
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