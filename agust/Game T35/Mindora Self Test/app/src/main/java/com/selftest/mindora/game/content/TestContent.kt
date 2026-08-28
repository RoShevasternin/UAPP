package com.selftest.mindora.game.content

import com.badlogic.gdx.Gdx
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ═════════════════════════════════════════════════════════════════════════════
//  Контент тестів. Джерело — assets/tests/*.json.
//
//  Движки:
//    axis      — бал до осі/шкали; сума проти порога → буква/рівень
//    count_max — очки категоріям; перемагає максимум
//
//  Формати відповідей:
//    scale  — Likert 1..scaleSize (reverse = (scaleSize+1) − відповідь)
//    choice — 2..4 варіанти, single select. Опція БЕЗ category ("No" у
//             Love Language) не дає очок нікому — це легальний стан.
// ═════════════════════════════════════════════════════════════════════════════

@Serializable
data class TestContent(
    val id           : String,
    val title        : String,
    val engine       : String,               // "axis" | "count_max"
    val answerFormat : String,               // "scale" | "choice"
    val scaleSize    : Int = 5,
    val resultRule   : String = "single",    // "single" | "letters" | "matrix" | "per_axis"

    /** Для axis: осі в порядку, який визначає порядок літер/рис у результаті. */
    val axes : List<AxisDef> = emptyList(),

    /** Для count_max: категорії; порядок = пріоритет при нічиїй. */
    val categories : List<String> = emptyList(),

    /** Для resultRule=matrix: ключ "рівень_рівень" за порядком axes → id результату. */
    val matrix : Map<String, String> = emptyMap(),

    /** Для count_max: чи додавати другий за очками результат у outcome. */
    val secondaryResult : Boolean = false,

    val questions : List<TestQuestion>,
    val results   : List<TestResultText>,
) {
    private val resultsById by lazy { results.associateBy { it.id } }

    fun resultById(id: String): TestResultText? = resultsById[id]

    val isScale: Boolean get() = answerFormat == "scale"
}

@Serializable
data class AxisDef(
    val id        : String,
    val pole      : String = "",   // літера при сумі > threshold (для letters)
    val opposite  : String = "",   // літера інакше
    val threshold : Int,           // строго більше → полюс/high
)

@Serializable
data class TestQuestion(
    val id       : String,
    val text     : String,
    val axis     : String?  = null,   // engine=axis
    val category : String?  = null,   // engine=count_max + scale
    val reverse  : Boolean  = false,
    val options  : List<TestOption> = emptyList(),   // answerFormat=choice
)

@Serializable
data class TestOption(
    val text     : String,
    /** null = опція без очок (варіант "No" у бінарних питаннях). */
    val category : String? = null,
)

/** Формат Results Copy EN: Name — tagline · body · At your best · You grow when. */
@Serializable
data class TestResultText(
    val id          : String,
    val name        : String,
    val tagline     : String,
    val body        : String,
    val atYourBest  : String,
    val youGrowWhen : String,
)

// ─────────────────────────────────────────────────────────────────────────────
//  Репозиторій: лінивий кеш, читання з assets/tests/<id>.json
// ─────────────────────────────────────────────────────────────────────────────

object TestRepository {

    const val ARCHETYPE     = "archetype"
    const val TYPES16       = "types16"
    const val ATTACHMENT    = "attachment"
    const val LOVE_LANGUAGE = "love_language"
    const val BIG_FIVE      = "big_five"

    /** Порядок = порядок вимірів портрета на хабі та екрані Portrait. */
    val ALL = listOf(ARCHETYPE, TYPES16, ATTACHMENT, LOVE_LANGUAGE, BIG_FIVE)

    private val json = Json { ignoreUnknownKeys = true }
    private val cache = HashMap<String, TestContent>()

    fun get(id: String): TestContent = cache.getOrPut(id) {
        val raw = Gdx.files.internal("tests/$id.json").readString("UTF-8")
        json.decodeFromString<TestContent>(raw)
    }
}