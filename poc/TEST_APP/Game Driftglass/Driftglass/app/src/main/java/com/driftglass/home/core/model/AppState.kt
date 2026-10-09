package com.driftglass.home.core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ═════════════════════════════════════════════════════════════════════════════
//  AppState — увесь стан Driftglass одним JSON у DataStore.
//  Нове поле — завжди з дефолтом: старий JSON має читатися без помилок.
// ═════════════════════════════════════════════════════════════════════════════

enum class Shuffle { OFF, UNLOCK, HOURLY, DAILY }
enum class Saver { AUTO, ON, OFF }
enum class DaySlot { DAWN, DAY, DUSK, NIGHT }

@Serializable
data class Settings(
    val labels: Boolean = true,
    val cols: Int = 4,
    val parallax: Boolean = true,
    val doubleTap: Boolean = true,
    val saver: Saver = Saver.AUTO,
    /** Нерухомий кадр на екран блокування при кожній зміні шпалер. */
    val lockStill: Boolean = true,
    /** null — мова системи; інакше "en" / "uk" / "ru". */
    val lang: String? = null,
)

@Serializable
data class AppState(
    val onboarded: Boolean = false,
    /** Що стоїть на Home (id каталогу або створених). */
    val applied: String = "tidepool",
    val favorites: List<String> = listOf("northern-hush", "orchid-rain"),
    /** Створені в Studio, найновіші першими. */
    val created: List<Wallpaper> = emptyList(),
    val shuffle: Shuffle = Shuffle.OFF,
    val dayCycle: Boolean = false,
    val slots: Map<DaySlot, String> = mapOf(
        DaySlot.DAWN to "first-light", DaySlot.DAY to "citrus-fizz",
        DaySlot.DUSK to "sahara-dusk", DaySlot.NIGHT to "polar-night",
    ),
    /** Коли шпалери змінювались востаннє (мс) — для Auto-shuffle щогодини / щодня. */
    val changedAt: Long = 0L,
    val settings: Settings = Settings(),
) {
    fun wallpaper(id: String): Wallpaper? = Catalog.byId(id) ?: created.firstOrNull { it.id == id }
    fun isFavorite(id: String) = id in favorites
    val nextNumber: Int get() = (created.maxOfOrNull { it.number } ?: 0) + 1
}

object AppStateCodec {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    fun encode(s: AppState): String = json.encodeToString(AppState.serializer(), s)
    fun decodeOrDefault(text: String?): AppState =
        if (text.isNullOrBlank()) AppState() else runCatching { json.decodeFromString(AppState.serializer(), text) }.getOrDefault(AppState())
}
