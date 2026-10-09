package com.driftglass.home.game.utils

import com.badlogic.gdx.graphics.Color

// ═════════════════════════════════════════════════════════════════════════════
//  GameColor — палітра Driftglass з CSS прототипу (prototype/index.html: --teal,
//  --glass, .btn.teal…). Правило T35: hex по екранах не хардкодимо, лише звідси.
//  Назва = роль_HEX (або роль_альфа для білих напівпрозорих).
// ═════════════════════════════════════════════════════════════════════════════
object GameColor {

    private fun w(a: Float) = Color(1f, 1f, 1f, a)

    // ── Фон і поверхні ────────────────────────────────────────────────────────
    val background   : Color = Color.valueOf("070B10")   // .a-solid
    val splash_04070A: Color = Color.valueOf("04070A")
    val glass        : Color = Color(16 / 255f, 24 / 255f, 32 / 255f, 0.42f)   // --glass
    val glassStrong  : Color = Color(10 / 255f, 14 / 255f, 18 / 255f, 0.86f)   // тост, меню
    val drawer       : Color = Color(6 / 255f, 10 / 255f, 14 / 255f, 0.80f)    // список апок
    val tabbar       : Color = Color(7 / 255f, 11 / 255f, 16 / 255f, 0.96f)
    val sheet_F2F3F5 : Color = Color.valueOf("F2F3F5")   // системний діалог (світлий, як у Android)
    val sheetDark    : Color = Color.valueOf("141C23")   // наші шторки

    // ── Текст ─────────────────────────────────────────────────────────────────
    val text         : Color = Color.valueOf("EAF2F5")
    val muted        : Color = w(0.62f)
    val dim          : Color = w(0.45f)
    val ink_071016   : Color = Color.valueOf("071016")   // текст на білій кнопці
    val inkTeal_04110E: Color = Color.valueOf("04110E")  // текст на бірюзовій кнопці
    val sysText_1A1C1E: Color = Color.valueOf("1A1C1E")
    val sysMuted_5A5F66: Color = Color.valueOf("5A5F66")
    val sysBlue_0B57D0: Color = Color.valueOf("0B57D0")

    // ── Бренд ─────────────────────────────────────────────────────────────────
    val teal_5FF2D1  : Color = Color.valueOf("5FF2D1")
    val sky_7BB8FF   : Color = Color.valueOf("7BB8FF")   // кінець градієнта .btn.teal
    val violet_8B7BFF: Color = Color.valueOf("8B7BFF")
    val rose_FF7AA8  : Color = Color.valueOf("FF7AA8")   // серце «в обраному»
    val amber_FFC27A : Color = Color.valueOf("FFC27A")   // режим економії
    val danger_FF8A8A: Color = Color.valueOf("FF8A8A")   // «Видалити» в меню

    // ── Білі напівпрозорі ─────────────────────────────────────────────────────
    val white_05 = w(0.05f)
    val white_08 = w(0.08f)
    val white_10 = w(0.10f)
    val white_14 = w(0.14f)    // --glass-line
    val white_20 = w(0.20f)
    val white_35 = w(0.35f)
    val white_55 = w(0.55f)
    val white_70 = w(0.70f)
    val white_92 = w(0.92f)
    val black_35 = Color(0f, 0f, 0f, 0.35f)
    val black_55 = Color(0f, 0f, 0f, 0.55f)
}
