package com.skindustry.skinly.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.skindustry.skinly.game.actors.button.base.AButtonAnimTexture
import com.skindustry.skinly.game.actors.coin.ACoinIcon
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.actor.disable
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter

// ═══════════════════════════════════════════════════════════════════════════
// Зелена кнопка «FREE …» — єдиний зелений елемент на світло-помаранчевому UI,
// щоб око йшло на неї. Фон генерується drawerUtil (закруглений прямокутник з
// градієнтом), зліва монета на світлій підкладці, справа шеврон.
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
        default = TextureRegionDrawable(screen.drawerUtil.getRoundedRegion(344, 72, RADIUS, GameColor.green_22, GameColor.green_16)),
    ),
) {

    // ------------------------------------------------------------------------
    // Decor
    // ------------------------------------------------------------------------
    private val aIconBg  = Image(screen.drawerUtil.getRoundedRegion(ICON, ICON, ICON / 2, GameColor.white_22))
    private val aCoin    = ACoinIcon(screen, COIN)
    private val aChevron = Label("›", FontFactory.create(
        screen, FontParameter().setCharacters("A›").setSize(30), screen.fontGenerator_Medium, GameColor.white_70))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        val iconY = (height - ICON) / 2f
        addActor(aIconBg); aIconBg.setBounds(PAD, iconY, ICON.toFloat(), ICON.toFloat())

        val coinOffset = (ICON - COIN) / 2f
        addActor(aCoin); aCoin.setBounds(PAD + coinOffset, iconY + coinOffset, COIN.toFloat(), COIN.toFloat())

        addActor(aChevron)
        aChevron.setBounds(width - PAD - CHEVRON, 0f, CHEVRON, height)
        aChevron.setAlignment(Align.center)

        listOf(aIconBg, aCoin, aChevron).forEach { it.disable() }
    }

    companion object {
        private const val RADIUS  = 16
        private const val PAD     = 14f
        private const val ICON    = 44
        private const val COIN    = 28
        private const val CHEVRON = 24f
    }
}
