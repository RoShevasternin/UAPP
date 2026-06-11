package com.skindustry.skinly.game.actors.vfx

import com.badlogic.gdx.graphics.Texture
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.vfx.effects.MaskEffect
import com.skindustry.skinly.game.utils.vfx.VfxGroup

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