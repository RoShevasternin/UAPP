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
}
