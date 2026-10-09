package com.driftglass.home.core.model

import kotlinx.serialization.Serializable

// ═════════════════════════════════════════════════════════════════════════════
//  Шпалери Driftglass — не картинки, а параметри шейдера. Ті самі значення, що
//  в прототипі (prototype/index.html → CAT / PAL), тож вигляд збігається 1:1.
//
//  Шейдер обирає Style, кольори — Palette (4 кольори: три акценти + темна основа),
//  решта — ручки Studio: speed (рух), scale (масштаб), grain (зерно), seed (варіація).
// ═════════════════════════════════════════════════════════════════════════════

enum class Style(val shader: String) {
    AURORA("aurora"),
    LIQUID("liquid"),
    WAVES("waves"),
    GLASS("glass"),
    MESH("mesh"),
}

/** Палітра: c1..c3 — акценти, c4 — темна основа. Кольори — #RRGGBB. */
data class Palette(val id: String, val colors: List<String>) {
    /** RGB 0..1 для uniform-ів шейдера. */
    fun rgb(i: Int): FloatArray {
        val h = colors[i].removePrefix("#")
        return floatArrayOf(
            h.substring(0, 2).toInt(16) / 255f,
            h.substring(2, 4).toInt(16) / 255f,
            h.substring(4, 6).toInt(16) / 255f,
        )
    }
}

object Palettes {
    val SEA    = Palette("sea",    listOf("#5FF2D1", "#3A7BFF", "#8B7BFF", "#06121A"))
    val EMBER  = Palette("ember",  listOf("#FF7A45", "#FFC27A", "#C2185B", "#140608"))
    val ORCHID = Palette("orchid", listOf("#FF7AA8", "#8B7BFF", "#2DE2E6", "#0B0718"))
    val NIGHT  = Palette("night",  listOf("#FF2E88", "#7A00FF", "#00C2FF", "#05030C"))
    val PEARL  = Palette("pearl",  listOf("#E8ECEF", "#9AA7B4", "#55616E", "#0A0C0F"))
    val CITRUS = Palette("citrus", listOf("#E8FF6B", "#43E97B", "#38F9D7", "#06140F"))
    val SAHARA = Palette("sahara", listOf("#F6C177", "#EB6F92", "#9CCFD8", "#191724"))
    val LAGOON = Palette("lagoon", listOf("#9EF0FF", "#2B87D9", "#1DE9B6", "#031019"))

    val ALL = listOf(SEA, EMBER, ORCHID, NIGHT, PEARL, CITRUS, SAHARA, LAGOON)

    fun byId(id: String): Palette = ALL.firstOrNull { it.id == id } ?: SEA
}

/**
 * Одні шпалери. [name] — для каталогу це бренд-назва (не перекладається),
 * для створених у Studio — порожньо (UI пише «Скло #N» мовою людини).
 */
@Serializable
data class Wallpaper(
    val id: String,
    val name: String,
    val style: Style,
    val palette: String,
    val speed: Float = 1f,
    val scale: Float = 1f,
    val grain: Float = 0.05f,
    val seed: Float = 0f,
    /** Номер створених у Studio (Скло #3); 0 — каталог. */
    val number: Int = 0,
) {
    val pal: Palette get() = Palettes.byId(palette)
    val isMine: Boolean get() = number > 0
}
