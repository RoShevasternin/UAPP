package com.rbxgolden.fungamems.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbxgolden.fungamems.game.actors.button.base.AButtonAnimTexture
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontParameter

// ═══════════════════════════════════════════════════════════════════════════
// Зелена кнопка «FREE …» — єдиний зелений елемент екрана, щоб око йшло на неї.
//
// Зроблена в стилі плиток меню (закруглені кути, кругла іконка зліва, шеврон
// справа), але текстури під неї в атласі немає і не потрібно: фон генерується
// drawerUtil.getRoundedRegion — закруглений прямокутник з градієнтом.
// ═══════════════════════════════════════════════════════════════════════════

open class AGreenButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnimTexture(
    screen    = screen,
    text      = text,
    color     = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(20),
    generator = screen.fontGenerator_Bold,
    style     = AButtonAnimTexture.Style(
        default  = TextureRegionDrawable(screen.drawerUtil.getRoundedRegion(344, 72, RADIUS, GameColor.green_28, GameColor.green_1E)),
        disabled = TextureRegionDrawable(screen.drawerUtil.getRoundedRegion(344, 72, RADIUS, GameColor.gray_5C)),
    ),
) {

    // ------------------------------------------------------------------------
    // Decor
    // ------------------------------------------------------------------------
    private val aIconBg = Image(screen.drawerUtil.getRoundedRegion(ICON, ICON, ICON / 2, GameColor.white_22))
    private val aIconLbl = Label("R$", FontFactory.create(
        screen, FontParameter().setCharacters("AR$").setSize(15), screen.fontGenerator_Bold, Color.WHITE))
    private val aChevron = Label("›", FontFactory.create(
        screen, FontParameter().setCharacters("A›").setSize(30), screen.fontGenerator_Medium, GameColor.white_25))

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
    }

    companion object {
        private const val RADIUS  = 16
        private const val PAD     = 14f
        private const val ICON    = 44
        private const val CHEVRON = 24f
    }
}
