package com.racing.funtols.game.actors.button

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.button.base.AButtonAnim
import com.racing.funtols.game.actors.button.base.AButtonAnimTexture
import com.racing.funtols.game.actors.button.base.AButtonBase
import com.racing.funtols.game.actors.button.base.AButtonStyles
import com.racing.funtols.game.actors.button.base.AButtonTexture
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.utils.actor.disable
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle

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
    text     : String,
    styleMsdf: MsdfStyle,
    style    : Style = AButtonStyles.Texture.NONE,
) : AButtonTexture(screen, style), WithLabel {

    override val label = AMsdfLabel(text, styleMsdf)

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
    text     : String,
    styleMsdf: MsdfStyle,
    style    : Style = AButtonStyles.Anim.NONE,
) : AButtonAnim(screen, style), WithLabel {

    override val label = AMsdfLabel(text, styleMsdf)

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
    text     : String,
    styleMsdf: MsdfStyle,
    style    : Style = AButtonStyles.AnimTexture.NONE,
) : AButtonAnimTexture(screen, style), WithLabel {

    override val label = AMsdfLabel(text, styleMsdf)

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addLabel(this)
    }
}