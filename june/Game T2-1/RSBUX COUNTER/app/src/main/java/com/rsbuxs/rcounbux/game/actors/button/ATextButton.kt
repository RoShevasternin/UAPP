package com.rsbuxs.rcounbux.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.rsbuxs.rcounbux.game.actors.button.base.AButtonAnim
import com.rsbuxs.rcounbux.game.actors.button.base.AButtonBase
import com.rsbuxs.rcounbux.game.actors.button.base.AButtonStyles
import com.rsbuxs.rcounbux.game.actors.button.base.AButtonTexture
import com.rsbuxs.rcounbux.game.actors.label.ALabel
import com.rsbuxs.rcounbux.game.utils.actor.disable
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.font.FontGenerator
import com.rsbuxs.rcounbux.game.utils.font.FontParameter

// ------------------------------------------------------------------------
// WithLabel
// ------------------------------------------------------------------------
interface WithLabel {
    val label: ALabel

    fun addLabel(group: AButtonBase) {
        group.addAndFillActor(label)
        label.disable()
        label.setAlignment(Align.center)
    }
}

// ------------------------------------------------------------------------
// ATextButton Texture
// ------------------------------------------------------------------------
open class ATextButtonTexture(
    override val screen: AdvancedScreen,
    text: String, color: Color,
    parameter: FontParameter, generator: FontGenerator,
    style: Style = AButtonStyles.Texture.NONE,
) : AButtonTexture(screen, style), WithLabel {

    override val label = ALabel(screen, text, color, parameter, generator)

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addLabel(this)
    }
}

// ------------------------------------------------------------------------
// ATextButton Anim
// ------------------------------------------------------------------------
open class ATextButtonAnim(
    override val screen: AdvancedScreen,
    text: String, color: Color,
    parameter: FontParameter, generator: FontGenerator,
    style: Style = AButtonStyles.Anim.NONE,
) : AButtonAnim(screen, style), WithLabel {

    override val label = ALabel(screen, text, color, parameter, generator)

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addLabel(this)
    }
}