package com.rbuxrds.counter.game.actors.vfx

import com.badlogic.gdx.graphics.Texture
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.vfx.MaskEffect
import com.rbuxrds.counter.game.utils.vfx.VfxGroup

class AMask(
    override val screen: AdvancedScreen,
    maskTexture: Texture? = null,
) : VfxGroup(screen) {

    private val maskEffect = MaskEffect(maskTexture)

    var maskTexture: Texture?
        get()      = maskEffect.maskTexture
        set(value) { maskEffect.maskTexture = value; rerenderOnce() }

    fun rerenderStaticOnce() { rerenderOnce() }

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addEffect(maskEffect)
    }
}