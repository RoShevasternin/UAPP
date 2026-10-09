package com.driftglass.home.core.model

// ═════════════════════════════════════════════════════════════════════════════
//  Catalog — 24 шпалери з прототипу (CAT). Порядок = порядок у «Огляді».
//  seed за замовчуванням — як у прототипі: id.length * 1.37 % 7.
// ═════════════════════════════════════════════════════════════════════════════
object Catalog {

    private fun mk(id: String, name: String, style: Style, pal: Palette, speed: Float = 1f, scale: Float = 1f, grain: Float = 0.05f, seed: Float? = null) =
        Wallpaper(id, name, style, pal.id, speed, scale, grain, seed ?: ((id.length * 1.37f) % 7f))

    val ALL: List<Wallpaper> = listOf(
        mk("tidepool",      "Tidepool",      Style.LIQUID, Palettes.SEA),
        mk("northern-hush", "Northern Hush", Style.AURORA, Palettes.SEA),
        mk("ember-drift",   "Ember Drift",   Style.LIQUID, Palettes.EMBER, seed = 3.1f),
        mk("orchid-rain",   "Orchid Rain",   Style.MESH,   Palettes.ORCHID),
        mk("night-drive",   "Night Drive",   Style.WAVES,  Palettes.NIGHT),
        mk("pearl",         "Pearl",         Style.MESH,   Palettes.PEARL, grain = 0.07f),
        mk("citrus-fizz",   "Citrus Fizz",   Style.GLASS,  Palettes.CITRUS),
        mk("sahara-dusk",   "Sahara Dusk",   Style.WAVES,  Palettes.SAHARA),
        mk("lagoon-glass",  "Lagoon Glass",  Style.GLASS,  Palettes.LAGOON),
        mk("velvet-aurora", "Velvet Aurora", Style.AURORA, Palettes.ORCHID, seed = 2.2f),
        mk("lava-lamp",     "Lava Lamp",     Style.LIQUID, Palettes.NIGHT, scale = 0.8f),
        mk("quiet-hours",   "Quiet Hours",   Style.WAVES,  Palettes.PEARL, speed = 0.6f),
        mk("neon-pebbles",  "Neon Pebbles",  Style.GLASS,  Palettes.NIGHT, scale = 1.2f),
        mk("first-light",   "First Light",   Style.MESH,   Palettes.SAHARA, seed = 4.4f),
        mk("mint-tide",     "Mint Tide",     Style.WAVES,  Palettes.CITRUS, seed = 1.9f),
        mk("solar-wind",    "Solar Wind",    Style.AURORA, Palettes.EMBER),
        mk("ink-rose",      "Ink & Rose",    Style.LIQUID, Palettes.ORCHID, seed = 5.3f),
        mk("stained-glass", "Stained Glass", Style.GLASS,  Palettes.EMBER, scale = 0.85f),
        mk("polar-night",   "Polar Night",   Style.AURORA, Palettes.PEARL, seed = 6.1f),
        mk("soft-static",   "Soft Static",   Style.MESH,   Palettes.NIGHT, grain = 0.09f),
        mk("desert-glass",  "Desert Glass",  Style.GLASS,  Palettes.SAHARA, seed = 2.7f),
        mk("deep-current",  "Deep Current",  Style.LIQUID, Palettes.LAGOON, speed = 0.7f),
        mk("green-flash",   "Green Flash",   Style.AURORA, Palettes.CITRUS, seed = 3.8f),
        mk("peach-fuzz",    "Peach Fuzz",    Style.MESH,   Palettes.EMBER, seed = 1.4f),
    )

    fun byId(id: String): Wallpaper? = ALL.firstOrNull { it.id == id }

    /** «Скло дня»: щодня інші шпалери, однакові для всіх у цей день. */
    fun today(epochDay: Long): Wallpaper = ALL[(epochDay % ALL.size).toInt()]
}
