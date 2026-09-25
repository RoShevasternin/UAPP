package com.rbxtreasure.fungamers.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbxtreasure.fungamers.game.actors.button.base.AButtonAnim
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.font.FontFactory
import com.rbxtreasure.fungamers.game.utils.font.FontParameter

// ═══════════════════════════════════════════════════════════════════════════
// Зелена кнопка «FREE …» — єдиний зелений елемент екрана (палітра апки
// коричнево-золота, скарбниця), тому око йде саме на неї. Фон генерується
// drawerUtil.getRoundedRegion — нову графіку пакувати не треба.
// ═══════════════════════════════════════════════════════════════════════════

open class AGreenButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    color     = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(20),
    generator = screen.fontGenerator_AlanSans_Bold,
    style     = AButtonAnim.Style(
        TextureRegionDrawable(screen.drawerUtil.getRoundedRegion(344, 72, RADIUS, GameColor.green_3FAA2A, GameColor.green_2E8420))
    ),
) {

    private val aIconBg = Image(screen.drawerUtil.getRoundedRegion(ICON, ICON, ICON / 2, GameColor.white_22))
    private val aIconLbl = Label("R$", FontFactory.create(
        screen, FontParameter().setCharacters("AR$").setSize(15), screen.fontGenerator_AlanSans_Bold, Color.WHITE))
    private val aChevron = Label("›", FontFactory.create(
        screen, FontParameter().setCharacters("A›").setSize(30), screen.fontGenerator_AlanSans_Medium, GameColor.white_25))

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
