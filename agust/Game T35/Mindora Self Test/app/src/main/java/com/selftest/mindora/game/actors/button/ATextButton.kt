package com.selftest.mindora.game.actors.button

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.base.AButtonAnim
import com.selftest.mindora.game.actors.button.base.AButtonAnimTexture
import com.selftest.mindora.game.actors.button.base.AButtonBase
import com.selftest.mindora.game.actors.button.base.AButtonStyles
import com.selftest.mindora.game.actors.button.base.AButtonTexture
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.utils.actor.disable
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle

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