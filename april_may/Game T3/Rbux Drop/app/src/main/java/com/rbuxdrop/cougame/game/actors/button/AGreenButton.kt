package com.rbuxdrop.cougame.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.button.base.AButtonTexture
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.utils.GameColor
import com.rbuxdrop.cougame.game.utils.actor.disable
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter

// ═══════════════════════════════════════════════════════════════════════════
// Зелена кнопка «FREE …» — єдиний зелений елемент на фіолетовому меню, щоб око
// йшло на неї. Текстури під неї в атласі немає і не потрібно: фон генерується
// drawerUtil.getRoundedRegion — закруглений прямокутник з градієнтом, зліва
// кругла іконка «R$», справа шеврон.
// ═══════════════════════════════════════════════════════════════════════════

open class AGreenButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonTexture(
    screen    = screen,
    text      = text,
    color     = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(20),
    generator = screen.fontGenerator_Bold,
    style     = AButtonTexture.Style(
        default = TextureRegionDrawable(screen.drawerUtil.getRoundedRegion(344, 72, RADIUS, GameColor.green_22, GameColor.green_16)),
        pressed = TextureRegionDrawable(screen.drawerUtil.getRoundedRegion(344, 72, RADIUS, GameColor.green_16, GameColor.green_15)),
    ),
) {

    // ------------------------------------------------------------------------
    // Decor
    // ------------------------------------------------------------------------
    private val aIconBg  = Image(screen.drawerUtil.getRoundedRegion(ICON, ICON, ICON / 2, GameColor.white_22))
    private val aIconLbl = ALabel(screen, "R$", Color.WHITE,
        FontParameter().setCharacters("AR$").setSize(15), screen.fontGenerator_Bold)
    private val aChevron = ALabel(screen, "›", GameColor.white_60,
        FontParameter().setCharacters("A›").setSize(30), screen.fontGenerator_Medium)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        val iconY = (height - ICON) / 2f
        addActor(aIconBg);  aIconBg.setBounds(PAD, iconY, ICON.toFloat(), ICON.toFloat())
        addActor(aIconLbl); aIconLbl.setBounds(PAD, iconY, ICON.toFloat(), ICON.toFloat())
        aIconLbl.setAlignment(Align.center)

        addActor(aChevron)
        aChevron.setBounds(width - PAD - CHEVRON, 0f, CHEVRON, height)
        aChevron.setAlignment(Align.center)

        listOf(aIconBg, aIconLbl, aChevron).forEach { it.disable() }
    }

    companion object {
        private const val RADIUS  = 16
        private const val PAD     = 14f
        private const val ICON    = 44
        private const val CHEVRON = 24f
    }
}
