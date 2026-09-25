package com.racing.funtols.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.button.base.AButtonAnim
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.utils.GameColor
import com.racing.funtols.game.utils.actor.disable
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame

// ═══════════════════════════════════════════════════════════════════════════
// Зелена кнопка «FREE …» — єдиний зелений елемент меню, щоб око йшло на неї.
// Зроблена в стилі плиток: закруглені кути, кругла іконка зліва, шеврон
// справа. Текстури в атласі немає і не треба — фон генерується
// drawerUtil.getRoundedRegion. Шрифти MSDF: гліфа «›» в атласах немає,
// тому шеврон — «>».
// ═══════════════════════════════════════════════════════════════════════════

open class AGreenButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnim(
    screen    = screen,
    text      = text,
    styleMsdf = MsdfStyle(gdxGame.msdfManager, gdxGame.msdfManager.fontTitilliumWeb_BoldItalic, 22f),
    style     = AButtonAnim.Style(
        TextureRegionDrawable(screen.drawerUtil.getRoundedRegion(344, 72, RADIUS, GameColor.green_3DC44B, GameColor.green_2A9C36))
    ),
) {

    private val msdf = gdxGame.msdfManager

    private val aIconBg  = Image(screen.drawerUtil.getRoundedRegion(ICON, ICON, ICON / 2, GameColor.white_22))
    private val aIconLbl = AMsdfLabel("R$", MsdfStyle(msdf, msdf.fontBarlow_Bold, 17f))
    private val aChevron = AMsdfLabel(">", MsdfStyle(msdf, msdf.fontBarlow_Bold, 26f, GameColor.white_55))

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
