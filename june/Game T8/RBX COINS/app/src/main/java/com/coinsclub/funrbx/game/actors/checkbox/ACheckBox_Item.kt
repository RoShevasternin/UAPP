package com.coinsclub.funrbx.game.actors.checkbox

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.checkbox.base.ACheckBox
import com.coinsclub.funrbx.game.actors.checkbox.base.ACheckBoxStyles
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.actor.setFontColor
import com.coinsclub.funrbx.game.utils.actor.setSize
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen

open class ACheckBox_Item(
    screen : AdvancedScreen,
    val text      : String,
    val labelStyle: Label.LabelStyle,
    val texture   : Texture,
    val size      : Vector2,
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

        lbl.setSize(130f, 18f)
        layout.add(lbl) { centerX(); topToTop(margin = 19f) }
        lbl.setAlignment(Align.center)
        lbl.setEllipsis(true)

        img.setSize(size)
        layout.add(img) { centerX(); topToBottom(lbl); bottomToBottom() }

        startX = img.x
        startY = img.y
    }

    override fun onChecked() {
        super.onChecked()
        lbl.setFontColor(GameColor.yellow_DFA008)
        startSelectedAnim()
    }

    override fun onUnchecked() {
        super.onUnchecked()
        lbl.setFontColor(GameColor.white_FFF5E3)
        stopSelectedAnim()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------
    private fun startSelectedAnim() {
        img.clearActions()
        img.setOrigin(Align.center)

        img.addAction(
            Actions.forever(
                Actions.sequence(
                    upAnim(),
                    downAnim(),
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
            )
        )
    }

    private fun upAnim() = Actions.parallel(
        Actions.moveBy(0f, 8f, 1f, Interpolation.sine),
        Actions.scaleTo(1.06f, 1.06f, 1f, Interpolation.sine),
    )

    private fun downAnim() = Actions.parallel(
        Actions.moveBy(0f, -8f, 1f, Interpolation.sine),
        Actions.scaleTo(0.96f, 0.96f, 1f, Interpolation.sine),
    )

}