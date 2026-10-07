package com.redwave.downloader.game.utils

import com.badlogic.gdx.graphics.Color

// ═════════════════════════════════════════════════════════════════════════════
//  GameColor — палітра Redwave, перенесена з CSS-токенів прототипу (--a-*, --red…).
//  Правило T35: hex по екранах не хардкодимо, лише звідси.
//  Назва = роль_HEX, щоб і роль, і колір було видно в автодоповненні.
// ═════════════════════════════════════════════════════════════════════════════
object GameColor {

    // ── Фон і поверхні ────────────────────────────────────────────────────────
    val background   : Color = Color.valueOf("0B0708")   // --a-bg
    val card_170F12  : Color = Color.valueOf("170F12")   // --a-card
    val card2_21161A : Color = Color.valueOf("21161A")   // --a-card2 (активний сегмент, кнопка Paste)
    val sheet_1B1215 : Color = Color.valueOf("1B1215")   // шторки (EQ, роль, фід)
    val line_white_7 : Color = Color.WHITE.cpy().apply { a = 0.075f } // --a-line, рамки карток

    // ── Текст ─────────────────────────────────────────────────────────────────
    val text_FBF1F2  : Color = Color.valueOf("FBF1F2")   // --a-fg
    val muted_A8949B : Color = Color.valueOf("A8949B")   // --a-muted
    val tabOff_86737A: Color = Color.valueOf("86737A")   // неактивна вкладка
    val hint_7C6A70  : Color = Color.valueOf("7C6A70")   // плейсхолдер поля

    // ── Бренд ─────────────────────────────────────────────────────────────────
    val red_FF2E4D     : Color = Color.valueOf("FF2E4D")   // --red, головний акцент
    val redHi_FF5A6C   : Color = Color.valueOf("FF5A6C")   // --red-hi, текстові акценти
    val redDeep_B8001F : Color = Color.valueOf("B8001F")   // --red-deep
    val gradTop_FF5468 : Color = Color.valueOf("FF5468")   // градієнт кнопок, початок
    val gradBot_E3002B : Color = Color.valueOf("E3002B")   // градієнт кнопок, кінець
    val logoBot_C2001F : Color = Color.valueOf("C2001F")   // градієнт логотипа, кінець
    val peach_FFA07F   : Color = Color.valueOf("FFA07F")   // друге слово заголовка (градієнт red→peach)
    val coral_FF8A70   : Color = Color.valueOf("FF8A70")   // кінець градієнта прогрес-барів
    val pink_FF8A98    : Color = Color.valueOf("FF8A98")   // ліцензія, помилки, чип CC
    val apricot_FFB3A4 : Color = Color.valueOf("FFB3A4")   // формат файлу (MP3/FLAC)
    val vizTop_FFB199  : Color = Color.valueOf("FFB199")   // верх стовпчиків візуалізатора

    // ── Семантика ─────────────────────────────────────────────────────────────
    val ok_3DDC97   : Color = Color.valueOf("3DDC97")   // «вже завантажено», No Wi‑Fi
    val warn_FFB547 : Color = Color.valueOf("FFB547")

    // ── Напівпрозорі ──────────────────────────────────────────────────────────
    val red_14      : Color = red_FF2E4D.cpy().apply { a = 0.14f }  // фон чипа CC
    val red_10      : Color = red_FF2E4D.cpy().apply { a = 0.10f }  // фон помилки
    val white_4     : Color = Color.WHITE.cpy().apply { a = 0.04f }
    val white_16    : Color = Color.WHITE.cpy().apply { a = 0.16f }
    val white_70    : Color = Color.WHITE.cpy().apply { a = 0.70f }
    val black_55    : Color = Color.BLACK.cpy().apply { a = 0.55f }  // затемнення під шторкою

    // ── Додано під час порту (CSS прототипу) ─────────────────────────────────
    val splash_070405  : Color = Color.valueOf("070405")              // фон сплеша
    val input_0C0708   : Color = Color.valueOf("0C0708")              // поле вставки лінка
    val coverBg_2A1B20 : Color = Color.valueOf("2A1B20")              // фон обкладинки, поки вантажиться
    val miniA_3D0D18   : Color = Color.valueOf("3D0D18")              // міні-плеєр, градієнт зліва
    val miniB_1F1317   : Color = Color.valueOf("1F1317")              // міні-плеєр, градієнт справа
    val miniInk_14080B : Color = Color.valueOf("14080B")              // іконка на білій кнопці міні-плеєра
    val toast_282023   : Color = Color.valueOf("282023").apply { a = 0.96f }
    val tabbar_0C0809  : Color = Color.valueOf("0C0809").apply { a = 0.96f }
    val errText_D9C3C8 : Color = Color.valueOf("D9C3C8")              // тіло помилки
    val flText_FFD5CC  : Color = Color.valueOf("FFD5CC")              // рядок лінка на скляній картці
    val ccEye_FFC2B5   : Color = Color.valueOf("FFC2B5")              // eyebrow картки буфера
    val eyebrowPink    : Color = pink_FF8A98
    val glass_160A0E   : Color = Color.valueOf("160A0E").apply { a = 0.66f }  // скляна картка онбордингу
    val skip_0A0506    : Color = Color.valueOf("0A0506").apply { a = 0.45f }
    val handleOff      : Color = Color.WHITE.cpy().apply { a = 0.22f }  // неактивна крапка
    val white_10       : Color = Color.WHITE.cpy().apply { a = 0.10f }
    val white_12       : Color = Color.WHITE.cpy().apply { a = 0.12f }
    val white_14       : Color = Color.WHITE.cpy().apply { a = 0.14f }
    val white_18       : Color = Color.WHITE.cpy().apply { a = 0.18f }
    val white_25       : Color = Color.WHITE.cpy().apply { a = 0.25f }
    val white_60       : Color = Color.WHITE.cpy().apply { a = 0.60f }
    val white_75       : Color = Color.WHITE.cpy().apply { a = 0.75f }
    val white_5        : Color = Color.WHITE.cpy().apply { a = 0.05f }
    val red_20         : Color = red_FF2E4D.cpy().apply { a = 0.20f }
    val red_03         : Color = red_FF2E4D.cpy().apply { a = 0.03f }
    val red_08         : Color = red_FF2E4D.cpy().apply { a = 0.08f }
    val red_13         : Color = red_FF2E4D.cpy().apply { a = 0.13f }
    val red_35         : Color = red_FF2E4D.cpy().apply { a = 0.35f }
    val redHi_25       : Color = redHi_FF5A6C.cpy().apply { a = 0.25f }
    val redHi_30       : Color = redHi_FF5A6C.cpy().apply { a = 0.30f }
    val redHi_35       : Color = redHi_FF5A6C.cpy().apply { a = 0.35f }
    val ok_12          : Color = ok_3DDC97.cpy().apply { a = 0.12f }
    val ok_14          : Color = ok_3DDC97.cpy().apply { a = 0.14f }
    val ok_35          : Color = ok_3DDC97.cpy().apply { a = 0.35f }
    val coral_FF9A7A   : Color = Color.valueOf("FF9A7A")              // кінець градієнта скляної картки
    val wave_FF3D55    : Color = Color.valueOf("FF3D55")              // вибрані стовпчики хвилі
}
