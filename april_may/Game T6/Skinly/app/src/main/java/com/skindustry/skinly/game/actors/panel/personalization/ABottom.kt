package com.skindustry.skinly.game.actors.panel.personalization

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBox
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBoxGroup
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBoxStyles
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.runGDX
import kotlinx.coroutines.launch

class ABottom(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBoxTexture = ACheckBox(screen, ACheckBoxStyles.BOT_TEXTURE)
    private val aBoxSticker = ACheckBox(screen, ACheckBoxStyles.BOT_STICKER)

    private val map = mapOf(
        Type.TEXTURE to aBoxTexture,
        Type.STICKER to aBoxSticker,
    )

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val cbg = ACheckBoxGroup()

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onTabChanged: ((Type) -> Unit)? = null

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(Image(screen.drawerUtil.getTexture(GameColor.gray_F2F2F2))) { fillParent() }

        aBoxTexture.setSize(160f, 46f)
        aBoxSticker.setSize(160f, 46f)

        add(aBoxTexture) { startToStart(); endToStart(aBoxSticker); centerY() }
        add(aBoxSticker) { startToEnd(aBoxTexture); endToEnd(); centerY() }

        aBoxTexture.checkBoxGroup = cbg
        aBoxSticker.checkBoxGroup = cbg

        map.forEach { (type, box) ->
            coroutine?.launch {
                box.checkFlow.collect { isChecked ->
                    if (isChecked) runGDX { onTabChanged?.invoke(type) }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    fun check(type: Type) {
        map[type]?.check()
    }

    enum class Type {
        TEXTURE, STICKER
    }

}