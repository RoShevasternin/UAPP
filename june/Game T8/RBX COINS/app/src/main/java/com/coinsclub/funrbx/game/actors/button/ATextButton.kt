package com.coinsclub.funrbx.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.button.base.AButtonAnim
import com.coinsclub.funrbx.game.actors.button.base.AButtonAnimTexture
import com.coinsclub.funrbx.game.actors.button.base.AButtonBase
import com.coinsclub.funrbx.game.actors.button.base.AButtonStyles
import com.coinsclub.funrbx.game.actors.button.base.AButtonTexture
import com.coinsclub.funrbx.game.utils.actor.disable
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontGenerator
import com.coinsclub.funrbx.game.utils.font.FontParameter

// ------------------------------------------------------------------------
// WithLabel
// ------------------------------------------------------------------------
interface WithLabel {
    val label: Label

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

    override val label = Label(text, FontFactory.create(screen, parameter, generator, color))

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

    override val label = Label(text, FontFactory.create(screen, parameter, generator, color))

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addLabel(this)
    }
}

// ------------------------------------------------------------------------
// ATextButton AnimTexture
// ------------------------------------------------------------------------
open class ATextButtonAnimTexture(
    override val screen: AdvancedScreen,
    text: String, color: Color,
    parameter: FontParameter, generator: FontGenerator,
    style: Style = AButtonStyles.AnimTexture.NONE,
) : AButtonAnimTexture(screen, style), WithLabel {

    override val label = Label(text, FontFactory.create(screen, parameter, generator, color))

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addLabel(this)
    }
}