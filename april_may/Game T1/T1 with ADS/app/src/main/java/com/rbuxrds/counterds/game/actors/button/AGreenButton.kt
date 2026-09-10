package com.rbuxrds.counterds.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counterds.game.actors.button.base.AButtonTexture
import com.rbuxrds.counterds.game.actors.label.ALabel
import com.rbuxrds.counterds.game.utils.GameColor
import com.rbuxrds.counterds.game.utils.actor.disable
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.font.FontParameter

// ═══════════════════════════════════════════════════════════════════════════
// Зелена кнопка «FREE …» — єдиний зелений елемент на екрані, щоб око йшло на неї.
//
// Зроблена в стилі плиток меню (радіус 12, кругла іконка зліва, шеврон «›»
// справа), але текстури під неї в атласі немає і не потрібно: фон генерується
// drawerUtil.getRoundedRegion — закруглений прямокутник із градієнтом, тож
// нову графіку пакувати не треба. Стани default/pressed/disabled — три фони.
// ═══════════════════════════════════════════════════════════════════════════

open class AGreenButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButton(
    screen    = screen,
    text      = text,
    color     = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(20),
    generator = screen.fontGenerator_InterTight_Bold,
    style     = AButtonTexture.Style(
        default  = rounded(screen, GameColor.green_22C55E, GameColor.green_16A34A),
        pressed  = rounded(screen, GameColor.green_16A34A, GameColor.green_0E7A34),
        disabled = rounded(screen, GameColor.gray_5C6070),
    ),
) {

    // ------------------------------------------------------------------------
    // Decor
    // ------------------------------------------------------------------------
    private val aIconBg  = Image(screen.drawerUtil.getRoundedRegion(ICON, ICON, ICON / 2, GameColor.white_22))
    private val aIconLbl = ALabel(screen, "R$", Color.WHITE,
        FontParameter().setCharacters("AR$").setSize(15), screen.fontGenerator_InterTight_Bold)
    private val aChevron = ALabel(screen, "›", GameColor.white_55,
        FontParameter().setCharacters("A›").setSize(30), screen.fontGenerator_InterTight_Medium)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addIcon()
        addChevron()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addIcon() {
        val y = (height - ICON) / 2f
        addActor(aIconBg);  aIconBg.setBounds(PAD, y, ICON.toFloat(), ICON.toFloat())
        addActor(aIconLbl); aIconLbl.setBounds(PAD, y, ICON.toFloat(), ICON.toFloat())
        aIconLbl.setAlignment(Align.center)
        aIconLbl.disable()
    }

    private fun addChevron() {
        addActor(aChevron)
        aChevron.setBounds(width - PAD - CHEVRON, 0f, CHEVRON, height)
        aChevron.setAlignment(Align.center)
        aChevron.disable()
    }

    companion object {
        private const val RADIUS  = 12      // як у плиток меню (24px @2x в атласі)
        private const val PAD     = 14f
        private const val ICON    = 44
        private const val CHEVRON = 24f

        private fun rounded(screen: AdvancedScreen, top: Color, bottom: Color = top) =
            TextureRegionDrawable(screen.drawerUtil.getRoundedRegion(344, 72, RADIUS, top, bottom))
    }
}
