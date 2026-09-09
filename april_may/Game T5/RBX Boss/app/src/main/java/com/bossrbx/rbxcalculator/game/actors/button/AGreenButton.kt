package com.bossrbx.rbxcalculator.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.bossrbx.rbxcalculator.game.actors.button.base.AButtonAnimTexture
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.actor.setFontColor
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter

// ═══════════════════════════════════════════════════════════════════════════
// Зелена кнопка — єдиний зелений елемент на екрані, щоб око йшло на неї.
//
// Текстури під неї в атласі немає і не потрібно: у грі рівно так само
// намальовані всі плашки (панель балансу, панель питання квізу) — плоским
// прямокутником з drawerUtil. Тому нову графіку пакувати не треба.
// ═══════════════════════════════════════════════════════════════════════════

open class AGreenButton(
    screen: AdvancedScreen,
    text: String,
) : ATextButtonAnimTexture(
    screen    = screen,
    text      = text,
    color     = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(20),
    generator = screen.fontGenerator_FIRENIGHT,
    style     = AButtonAnimTexture.Style(
        default = TextureRegionDrawable(screen.drawerUtil.getRegion(GameColor.green_55BF40)),
    ),
) {

    override fun enable() {
        super.enable()
        label.setFontColor(Color.WHITE)
    }

    override fun disable() {
        super.disable()
        label.setFontColor(GameColor.gray_333333)
    }
}
