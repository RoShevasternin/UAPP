package com.redwave.downloader.game.utils

// ═════════════════════════════════════════════════════════════════════════════
//  Constants.kt — ЗАМІНЮЄ однойменний файл T35 (ті самі WIDTH_UI / HEIGHT_UI /
//  TIME_ANIM_SCREEN / VERTICAL_BIAS) і додає Dimens.
//
//  Розміри UI у world-юнітах (дизайн-база T35: 376 × 815, ExtendViewport).
//
//  Прототип малювався в телефоні з екраном 350 CSS-px завширшки, тож
//  1 px прототипу = 376 / 350 ≈ 1.074 wu. Усе нижче вже переведене —
//  в екранах пиши Dimens.SIDE, а не 17.2f.
// ═════════════════════════════════════════════════════════════════════════════
const val WIDTH_UI  = 376f
const val HEIGHT_UI = 815f

const val TIME_ANIM_SCREEN = 0.27f

const val VERTICAL_BIAS = 0.55f

/** Перевести px з прототипу (index.html) у world-юніти. */
fun px(p: Float): Float = p * (WIDTH_UI / 350f)

object Dimens {
    // ── Сітка ─────────────────────────────────────────────────────────────────
    val SIDE        = px(16f)   // бічний відступ контенту
    val GAP_SECTION = px(18f)   // між секціями головної
    val GAP_CARD    = px(10f)
    val GAP_SMALL   = px(6f)

    // ── Радіуси ───────────────────────────────────────────────────────────────
    val R_PASTE   = px(24f)
    val R_CARD    = px(20f)
    val R_FEAT    = px(22f)
    val R_COVER_S = px(12f)    // обкладинка в списку
    val R_COVER_L = px(22f)    // обкладинка в плеєрі
    val R_BUTTON  = px(15f)
    val R_CHIP    = px(999f)   // «пігулка» — радіус = половина висоти

    // ── Компоненти ────────────────────────────────────────────────────────────
    val BTN_H        = px(46f)  // червона кнопка Download
    val INPUT_H      = px(46f)
    val ROW_H        = px(62f)  // рядок треку (обкладинка 48 + відступи)
    val COVER_ROW    = px(48f)
    val COVER_CAR    = px(124f) // карусель «Recently added»
    val COVER_MINI   = px(40f)
    val ICON         = px(20f)
    val ICON_BTN     = px(38f)  // кругла кнопка-іконка
    val PLAY_BIG     = px(68f)
    val TABBAR_H     = px(58f)
    val MINI_H       = px(56f)
    val FEAT_W       = px(250f)
    val FEAT_H       = px(250f * 11f / 16f)
    val WAVE_H       = px(110f)
    val VIZ_H        = px(58f)
    val HANDLE_R     = px(7f)

    // ── Шрифти (розмір у wu) ──────────────────────────────────────────────────
    val FS_HERO     = px(29f)  // "What are we downloading?" — Archivo Expanded ExtraBold
    val FS_TITLE    = px(30f)  // заголовки вкладок
    val FS_ONB      = px(31f)
    val FS_CLOCK    = px(62f)  // годинник лаунчера — Archivo Expanded Bold
    val FS_H3       = px(16f)  // заголовки секцій — Onest Bold
    val FS_BODY     = px(14f)  // Onest Regular/SemiBold (будь-який текст користувача — лише Onest: у ньому є кирилиця)
    val FS_SMALL    = px(12f)
    val FS_CAPTION  = px(10.5f)
    val FS_MONO     = px(11f)  // таймери, мітки — JetBrains Mono
}
