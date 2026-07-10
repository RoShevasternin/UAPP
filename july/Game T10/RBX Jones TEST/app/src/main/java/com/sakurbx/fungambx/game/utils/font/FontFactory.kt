package com.sakurbx.fungambx.game.utils.font

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import kotlin.math.roundToInt

// ----------------------------------------------------------------------------
// FontFactory — генерація чітких шрифтів для будь-якого екрана
//
//   ПРОБЛЕМА, яку вирішує:
//     Стейдж працює в world-координатах ExtendViewport(WIDTH_UI×HEIGHT_UI).
//     На реальному екрані 1 world-одиниця = pxPerWorld фізичних пікселів
//     (Redmi 9T 720px: 720/2160 = 0.33; сучасний 1080: 0.5; 1440: 0.67).
//
//     Якщо генерувати гліф у дизайн-розмірі й масштабувати — або блюр (велике→
//     мале), або рвані краї (мале→велике). А border/shadow, задані в дизайн-px,
//     на маленькому гліфі стають гігантськими й зміщеними.
//
//   РІШЕННЯ:
//     1) Генеруємо гліф у ФАКТИЧНОМУ піксельному розмірі (designSize × pxPerWorld),
//        помножене на QUALITY (запас чіткості, щоб навіть на щільних екранах було
//        різко й при невеликому down-scale виглядало гладко).
//     2) border/shadow МАСШТАБУЄМО тим самим коефіцієнтом — лишаються пропорційні.
//     3) setScale повертає гліф у world-координати (1 / (pxPerWorld × QUALITY)).
//
//   Результат: однаково чітко й коректно на старих і сучасних девайсах.
// ----------------------------------------------------------------------------

object FontFactory {

    // Запас чіткості: гліф генерується трохи більшим за екранний розмір і
    // акуратно зменшується (Linear). Дає гладкі краї без рваності.
    // 1.5 — баланс якість/пам'ять. Збільшиш до 2 — чіткіше, але важчі атласи.
    private const val QUALITY = 1.5f

    // Нижня межа фізичного розміру гліфа (щоб дрібний текст не деградував)
    private const val MIN_PX = 12

    fun create(
        screen    : AdvancedScreen,
        parameter : FontParameter,
        generator : FontGenerator,
        color     : Color = Color.WHITE,
    ): Label.LabelStyle {
        // Скільки фізичних пікселів припадає на 1 world-одиницю (дробове!)
        val pxPerWorldRaw = screen.scalerUItoScreen.toActual(1f)
        val pxPerWorld    = if (pxPerWorldRaw > 0.01f) pxPerWorldRaw else 1f

        // Коефіцієнт генерації: фактичний розмір на екрані × запас чіткості
        val genScale = pxPerWorld * QUALITY

        // Гліф у фізичних пікселях (як його реально рендерити на цьому екрані)
        val sizePx = (parameter.size * genScale).roundToInt().coerceAtLeast(MIN_PX)

        // КЛЮЧОВЕ: border/shadow теж масштабуємо тим самим genScale,
        // інакше на маленькому гліфі вони завеликі й зміщені.
        val scaled = parameter.copy().apply {
            size          = sizePx
            borderWidth   = parameter.borderWidth * genScale
            shadowOffsetX = (parameter.shadowOffsetX * genScale).roundToInt()
            shadowOffsetY = (parameter.shadowOffsetY * genScale).roundToInt()
        }

        val generated = generator.generateFont(scaled)

        // Повертаємо гліф у world-координати: ділимо на коефіцієнт генерації
        val font = BitmapFont(generated.data, generated.regions, false)
            .also { it.data.setScale(1f / genScale) }

        // Реєструємо в екрані — dispose разом з екраном
        screen.disposableSet.add(font)

        return Label.LabelStyle(font, color)
    }
}