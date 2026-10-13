package com.vidtools.framefall.game.utils

import com.badlogic.gdx.graphics.Color

// ═════════════════════════════════════════════════════════════════════════════
//  GameColor — палітра Framefall з прототипу: тепле світло кінопроєктора,
//  бурштин #FFC94D → помаранч #FF8A2A → полум'я #F2511B на теплому чорному.
//  Правило T35: hex по екранах не хардкодимо, лише звідси.
//  Назва = роль_HEX, щоб і роль, і колір було видно в автодоповненні.
// ═════════════════════════════════════════════════════════════════════════════
object GameColor {

    // ── Фон і поверхні ────────────────────────────────────────────────────────
    val background   : Color = Color.valueOf("0A0705")   // ink
    val card_16100C  : Color = Color.valueOf("16100C")   // картки
    val card2_221812 : Color = Color.valueOf("221812")   // кнопка Paste, активний сегмент
    val sheet_1A130E : Color = Color.valueOf("1A130E")   // шторки
    val line_white_7 : Color = Color.WHITE.cpy().apply { a = 0.075f }

    // ── Текст ─────────────────────────────────────────────────────────────────
    val text_FFF6EC  : Color = Color.valueOf("FFF6EC")   // paper
    val muted_B3A293 : Color = Color.valueOf("B3A293")
    val tabOff_8C7B6D: Color = Color.valueOf("8C7B6D")   // неактивна вкладка
    val hint_80705F  : Color = Color.valueOf("80705F")   // плейсхолдер поля

    // ── Бренд ─────────────────────────────────────────────────────────────────
    val amber_FFC94D     : Color = Color.valueOf("FFC94D")   // amber hi — верх градієнта
    val accent_FF8A2A    : Color = Color.valueOf("FF8A2A")   // orange — головний акцент (іконки, прогрес)
    val flame_F2511B     : Color = Color.valueOf("F2511B")   // flame — низ градієнта
    val accentText_FFB04A: Color = Color.valueOf("FFB04A")   // текстові акценти («Library», «All tools»)
    val gradTop_FFC94D   : Color = amber_FFC94D              // градієнт кнопок, початок
    val gradBot_F2511B   : Color = flame_F2511B              // градієнт кнопок, кінець
    val onAccent_1A0E05  : Color = Color.valueOf("1A0E05")   // текст і іконки НА бурштиновій кнопці
    val apricot_FFC98F   : Color = Color.valueOf("FFC98F")   // формат файлу (MP4 · H.264)
    val err_FF7A6B       : Color = Color.valueOf("FF7A6B")   // помилки
    val blue_8EC5FF      : Color = Color.valueOf("8EC5FF")   // чип ліцензії CC BY

    // ── Семантика ─────────────────────────────────────────────────────────────
    val ok_47E0A0   : Color = Color.valueOf("47E0A0")   // «вже збережено», суспільне надбання
    val warn_FFB547 : Color = Color.valueOf("FFB547")

    // ── Напівпрозорі ──────────────────────────────────────────────────────────
    val accent_10   : Color = accent_FF8A2A.cpy().apply { a = 0.10f }
    val accent_14   : Color = accent_FF8A2A.cpy().apply { a = 0.14f }
    val accent_20   : Color = accent_FF8A2A.cpy().apply { a = 0.20f }
    val accent_35   : Color = accent_FF8A2A.cpy().apply { a = 0.35f }
    val accent_03   : Color = accent_FF8A2A.cpy().apply { a = 0.03f }
    val accentText_25 : Color = accentText_FFB04A.cpy().apply { a = 0.25f }
    val accentText_35 : Color = accentText_FFB04A.cpy().apply { a = 0.35f }
    val err_10      : Color = err_FF7A6B.cpy().apply { a = 0.10f }
    val err_35      : Color = err_FF7A6B.cpy().apply { a = 0.35f }
    val blue_14     : Color = blue_8EC5FF.cpy().apply { a = 0.14f }
    val ok_12       : Color = ok_47E0A0.cpy().apply { a = 0.12f }
    val ok_14       : Color = ok_47E0A0.cpy().apply { a = 0.14f }
    val ok_35       : Color = ok_47E0A0.cpy().apply { a = 0.35f }
    val white_4     : Color = Color.WHITE.cpy().apply { a = 0.04f }
    val white_5     : Color = Color.WHITE.cpy().apply { a = 0.05f }
    val white_10    : Color = Color.WHITE.cpy().apply { a = 0.10f }
    val white_12    : Color = Color.WHITE.cpy().apply { a = 0.12f }
    val white_14    : Color = Color.WHITE.cpy().apply { a = 0.14f }
    val white_16    : Color = Color.WHITE.cpy().apply { a = 0.16f }
    val white_18    : Color = Color.WHITE.cpy().apply { a = 0.18f }
    val white_25    : Color = Color.WHITE.cpy().apply { a = 0.25f }
    val white_60    : Color = Color.WHITE.cpy().apply { a = 0.60f }
    val white_70    : Color = Color.WHITE.cpy().apply { a = 0.70f }
    val white_75    : Color = Color.WHITE.cpy().apply { a = 0.75f }
    val black_55    : Color = Color.BLACK.cpy().apply { a = 0.55f }  // затемнення під шторкою
    val handleOff   : Color = Color.WHITE.cpy().apply { a = 0.22f }  // неактивна крапка онбордингу

    // ── Компоненти ────────────────────────────────────────────────────────────
    val splash_070504  : Color = Color.valueOf("070504")
    val input_0D0906   : Color = Color.valueOf("0D0906")              // поле вставки лінка
    val coverBg_2A1E16 : Color = Color.valueOf("2A1E16")              // мініатюра, поки вантажиться
    val toast_2A221B   : Color = Color.valueOf("2A221B").apply { a = 0.96f }
    val tabbar_0C0806  : Color = Color.valueOf("0C0806").apply { a = 0.96f }
    val errText_E3CFC0 : Color = Color.valueOf("E3CFC0")              // тіло помилки
    val flText_FFE2C4  : Color = Color.valueOf("FFE2C4")              // рядок лінка на скляній картці
    val ccEye_FFD59A   : Color = Color.valueOf("FFD59A")              // eyebrow картки буфера
    val glass_171009   : Color = Color.valueOf("171009").apply { a = 0.66f }  // скляна картка онбордингу
    val skip_0A0705    : Color = Color.valueOf("0A0705").apply { a = 0.45f }
    val coral_FFB15C   : Color = Color.valueOf("FFB15C")              // кінець градієнта прогрес-барів
    val scrim_0A0705   : Color = Color.valueOf("0A0705")              // база для градієнтів поверх відео
}
