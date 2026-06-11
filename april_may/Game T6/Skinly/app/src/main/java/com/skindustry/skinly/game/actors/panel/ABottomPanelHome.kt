package com.skindustry.skinly.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBox
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBoxGroup
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBoxStyles
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.runGDX
import kotlinx.coroutines.launch

class ABottomPanelHome(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBoxHome     = ACheckBox(screen, ACheckBoxStyles.HOME)
    private val aBoxSkinBook = ACheckBox(screen, ACheckBoxStyles.SKIN_BOOK)

    private val map = mapOf(
        Type.HOME      to aBoxHome,
        Type.SKIN_BOOK to aBoxSkinBook,
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

        aBoxHome.setSize(160f, 46f)
        aBoxSkinBook.setSize(160f, 46f)

        add(aBoxHome) { startToStart(); endToStart(aBoxSkinBook); centerY() }
        add(aBoxSkinBook) { startToEnd(aBoxHome); endToEnd(); centerY() }

        aBoxHome.checkBoxGroup = cbg
        aBoxSkinBook.checkBoxGroup = cbg

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
        HOME, SKIN_BOOK
    }

}