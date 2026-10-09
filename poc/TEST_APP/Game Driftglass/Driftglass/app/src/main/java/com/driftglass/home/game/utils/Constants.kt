package com.driftglass.home.game.utils

// ═════════════════════════════════════════════════════════════════════════════
//  Constants.kt — як у T35 / Redwave (WIDTH_UI / HEIGHT_UI / TIME_ANIM_SCREEN / VERTICAL_BIAS) + Dimens.
//
//  Дизайн-база T35: 376 × 815 world-юнітів (ExtendViewport).
//  Телефон у прототипі — 348 CSS-px завширшки, тож 1 px прототипу = 376 / 348 ≈ 1.08 wu.
// ═════════════════════════════════════════════════════════════════════════════
const val WIDTH_UI  = 376f
const val HEIGHT_UI = 815f

const val TIME_ANIM_SCREEN = 0.27f

const val VERTICAL_BIAS = 0.55f

/** Перевести px з прототипу (екран телефона 348 px) у world-юніти. */
fun px(p: Float): Float = p * (WIDTH_UI / 348f)

object Dimens {
    val SIDE     = px(16f)    // бічний відступ
    val GAP      = px(12f)
    val R_CARD   = px(20f)
    val R_GLASS  = px(22f)
    val R_SHEET  = px(26f)
    val R_BTN    = px(16f)
    val R_ICON   = px(17f)
    val BTN_H    = px(44f)
    val ICON_BTN = px(38f)
    val APP_ICON = px(52f)
    val TABBAR_H = px(60f)
}

/** Політика конфіденційності (❓ адресу дасть VELDAN — зараз сторінка-заглушка Redwave не підходить). */
const val PRIVACY_URL = "https://driftglass-privacy.oyutetijep68.workers.dev/privacy"
