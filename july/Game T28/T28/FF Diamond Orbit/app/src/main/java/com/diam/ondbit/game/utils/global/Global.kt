package com.diam.ondbit.game.utils.global

var GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.entries.first()

enum class ConverterType(
    val title       : String,
    val fromCurrency: String,
    val toCurrency  : String,
    val coff        : Double,
) {
    DO_TO_DI(
        "DO to DI",
        "DP",
        "DI",
        33.0
    ),

    DI_TO_DO(
        "DI to DO",
        "DI",
        "DO",
        0.1
    ),

    FF_TO_DI(
        "FF to DI",
        "FF",
        "DI",
        25.0
    ),
}

var GLOBAL_SELECTED_CHARACTER_INDEX = 0

val GLOBAL_LIST_CHARACTER_NAMES = listOf(
    "RAY",
    "MORSE",
    "NERO",
    "RIN",
    "OSCAR",
    "KASSIE",
    "KAIROS",
    "RYDEN",
)

val GLOBAL_LIST_CHARACTER_DESCRIPTIONS = listOf(

    // ── Ray ───────────────────────────────────────────────────────────────────
    "Role: Scout / Attacker\n\n" +
            "Ray is a seasoned fighter known for lightning-fast reflexes and an uncanny ability to spot enemies first on the battlefield. Thanks to his unique ability, he gains a temporary boost to accuracy and movement speed during active combat, making him a great pick for an aggressive playstyle.\n\n" +
            "Ability: \"Precision Strike\" — temporarily boosts weapon accuracy and reload speed.\n\n" +
            "Tip: Shines best in close-to-mid range team fights — use the ability right before engaging the enemy to maximize the effect.",

    // ── Morse ─────────────────────────────────────────────────────────────────
    "Role: Disruptor / Assassin\n\n" +
            "Morse works in silence behind a sealed mask, and nobody ever hears him coming. He hunts by cutting the enemy off from their own team — jamming signals, killing the radar and turning a coordinated squad into four confused strangers. Patient, cold and built for ambushes.\n\n" +
            "Ability: \"Signal Jam\" — scrambles nearby enemy radar for a short time.\n\n" +
            "Tip: Trigger the jam before you push a building — enemies inside lose track of both you and each other.",

    // ── Nero ──────────────────────────────────────────────────────────────────
    "Role: Controller / Defender\n\n" +
            "Nero carries the cold with him. Where he stands, the air slows and so does everyone in it — perfect for holding a doorway, freezing a rush or buying your team the two seconds they need to reload. He never chases; he makes the enemy come to him.\n\n" +
            "Ability: \"Frost Field\" — slows enemy movement inside a radius around him.\n\n" +
            "Tip: Drop the field on a narrow corridor or staircase. Enemies caught inside are easy targets for your whole squad.",

    // ── Rin ───────────────────────────────────────────────────────────────────
    "Role: Infiltrator\n\n" +
            "Rin moves like she was never there. Trained to slip past positions instead of storming them, she takes the long way around and arrives behind you. She is at her strongest alone, in the quiet minute before anyone realises the flank has already happened.\n\n" +
            "Ability: \"Silent Step\" — muffles her footsteps and speeds up crouched movement.\n\n" +
            "Tip: Do not fight the front line. Loop wide, come in from behind and take out the enemy sniper first.",

    // ── Oscar ─────────────────────────────────────────────────────────────────
    "Role: Rusher / Scout\n\n" +
            "Oscar is the first one out of the drop and the first one into the fight. Loud, fearless and permanently in a hurry, he trades caution for tempo — grabbing loot, claiming ground and pulling the enemy's attention while his team sets up behind him.\n\n" +
            "Ability: \"Overdrive\" — a short burst of extra sprint speed.\n\n" +
            "Tip: Use the burst to close open ground, not to escape. Crossing a field fast is what keeps you alive out there.",

    // ── Kassie ────────────────────────────────────────────────────────────────
    "Role: Support / Rusher\n\n" +
            "Kassie brings the energy nobody asked for and everybody needs. She fights right beside her squad, keeping them patched up and reloaded while the shooting is still going on. Reckless on her own, but she makes the three people around her noticeably harder to kill.\n\n" +
            "Ability: \"Voltage Rush\" — speeds up healing and reloading for nearby teammates.\n\n" +
            "Tip: Stay close to your squad. The ability is wasted the moment you push ahead alone.",

    // ── Kairos ────────────────────────────────────────────────────────────────
    "Role: Assassin / Trickster\n\n" +
            "Kairos fights with misdirection. He is never quite where you shot, and by the time you correct your aim he is already behind you. Enemies waste their whole magazine on an afterimage — and that wasted magazine is the entire point of him.\n\n" +
            "Ability: \"Phantom Veil\" — briefly turns him nearly invisible and leaves a decoy behind.\n\n" +
            "Tip: Activate it while you are being shot at, then move sideways — most players keep firing where you were.",

    // ── Ryden ─────────────────────────────────────────────────────────────────
    "Role: Engineer / Support\n\n" +
            "Ryden solves fights before they start. He reads the map, patches the gear and keeps the squad running when everything is falling apart. Not the flashiest pick, but the one that turns a losing position into a defendable one.\n\n" +
            "Ability: \"Field Repair\" — restores armor and gear durability for himself and nearby allies.\n\n" +
            "Tip: Repair between fights, not during them. A full-armor squad wins the next engagement, not the current one.",
)

var GLOBAL_SELECTED_MAP_INDEX = 0

val GLOBAL_LIST_MAP_NAMES = listOf(
    "Solara",
    "Nexterra",
    "Bermuda Remastered",
    "Alpine",
    "Bermuda",
    "Kalahari",
)

val GLOBAL_LIST_MAP_DESCRIPTIONS = listOf(

    // ── Solara ────────────────────────────────────────────────────────────────
    "Solara is a compact 1400×1400 meter map that combines futuristic tech zones with natural landscapes and diverse biomes. Unlike the classic maps, this one focuses on modern design and a fast, aggressive combat pace.\n\n" +
            "The map's main new mechanic is the rail system for quick movement between key points, which also works as a warning system for nearby enemies. Rails light up red when enemies are close — so always check the rail's color before hopping on.",

    // ── Nexterra ──────────────────────────────────────────────────────────────
    "Nexterra is a futuristic map built around towering tech structures, neon-lit districts and wide stretches of open water. Its layout mixes tight indoor rooms with long sightlines across the bay, so close-range and sniper players both find their space.\n\n" +
            "Verticality decides most fights here. Rooftops and multi-level buildings give you strong angles, but they also expose you from three sides at once. Clear the floors above before you settle into a position.",

    // ── Bermuda Remastered ────────────────────────────────────────────────────
    "Bermuda Remastered is the rebuilt version of the classic map — sharper textures, cleaner sightlines and reworked interiors in every familiar zone. Veterans will recognise each landmark, but cover placement inside the buildings has changed.\n\n" +
            "Because everything feels familiar, most players fall back on their old routes. That predictability is your advantage: expect enemies exactly where they always used to land, and take the new flanking paths instead.",

    // ── Alpine ────────────────────────────────────────────────────────────────
    "Alpine is a cold mountain map of deep snow, pine forest and steep ridges. Bases, sawmills and quarries sit between the slopes, and the height differences shape almost every engagement.\n\n" +
            "Movement matters more than aim here. High ground wins most exchanges, but climbing leaves you exposed on open white snow. Use tree lines and rock faces to break line of sight while you push toward the ridge.",

    // ── Bermuda ───────────────────────────────────────────────────────────────
    "Bermuda is the original map and still the most played one. Its balanced mix of small towns, open fields and coastline became the standard that every later map is measured against.\n\n" +
            "Landmarks like Clock Tower, Peak and Mars Electric are the hottest drops — heavy early loot and even heavier fights. If you prefer a slower start, land on the outer edges and rotate inward as the zone shrinks.",

    // ── Kalahari ──────────────────────────────────────────────────────────────
    "Kalahari is a desert map of red canyons, dry riverbeds and dense industrial blocks. The narrow streets inside the main zones force constant close-range duels, while the open sand between them punishes anyone who crosses without cover.\n\n" +
            "Refinery, Command Post and Bayfront hold the richest loot and the earliest bloodbaths. The canyon walls are your safest rotation route — they cut sightlines from almost every direction.",
)