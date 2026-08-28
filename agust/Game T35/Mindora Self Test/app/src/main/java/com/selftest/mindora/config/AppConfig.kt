package com.selftest.mindora.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ═════════════════════════════════════════════════════════════════════════════
//  AppConfig — модель Remote Config.
//
//  Дефолти в конструкторах — не декорація, а третій рівень захисту:
//    1. Firebase RC повертає значення з консолі
//    2. Немає мережі → RC віддає закешоване / in-app defaults з assets
//    3. JSON битий або ключ зник → спрацьовують дефолти нижче
//
//  Тому будь-яке поле має дефолт, і жодне не є nullable.
// ═════════════════════════════════════════════════════════════════════════════

@Serializable
data class AppConfig(
    val economy:  Economy  = Economy(),
    val costs:    Costs    = Costs(),
    val portrait: Portrait = Portrait(),
    val ads:      Ads      = Ads()
) {
    companion object {
        private val json = Json {
            ignoreUnknownKeys = true   // нові ключі з консолі не ламають старий білд
            isLenient = true
            coerceInputValues = true   // null з консолі → дефолт, а не крах
        }

        /** Розпарсити JSON з Remote Config. Будь-яка помилка → повний дефолтний конфіг. */
        fun parse(raw: String?): AppConfig = try {
            if (raw.isNullOrBlank()) AppConfig() else json.decodeFromString(raw)
        } catch (e: Exception) {
            AppConfig()
        }
    }
}

// ── Економіка ─────────────────────────────────────────────────────────────────

@Serializable
data class Economy(
    @SerialName("lumens_welcome_bonus")       val lumensWelcomeBonus:    Long = 100,
    @SerialName("lumens_per_rewarded")        val lumensPerRewarded:     Long = 50,
    @SerialName("daily_reward")               val dailyReward:           List<Long> = listOf(50, 80, 120, 150, 180, 200, 200),
    @SerialName("memory_reward_per_level")    val memoryRewardPerLevel:  Long = 10,
    @SerialName("memory_full_levels_per_day") val memoryFullLevelsPerDay: Int = 5,
    @SerialName("memory_reward_after_cap")    val memoryRewardAfterCap:  Long = 2
) {

    /**
     * Нагорода за день стріку. У спеці масив індексується з 1, тут — з 0.
     * День поза діапазоном затискається до останнього (стрік 8+ = нагорода 7-го дня).
     */
    fun dailyRewardFor(day: Int): Long =
        if (dailyReward.isEmpty()) 0L
        else dailyReward[(day - 1).coerceIn(0, dailyReward.lastIndex)]

    /**
     * Нагорода за рівень Memory Match з урахуванням м'якого денного потолка.
     * @param levelsClearedToday скільки рівнів уже пройдено сьогодні ДО цього
     */
    fun memoryRewardFor(levelsClearedToday: Int): Long =
        if (levelsClearedToday < memoryFullLevelsPerDay) memoryRewardPerLevel
        else memoryRewardAfterCap
}

// ── Ціни на тести ─────────────────────────────────────────────────────────────

@Serializable
data class Costs(
    @SerialName("cost_archetype")      val archetype:     Long = 100,
    @SerialName("cost_flagship_deep")  val flagshipDeep:  Long = 150,
    @SerialName("cost_attachment")     val attachment:    Long = 200,
    @SerialName("cost_love")           val loveLanguage:  Long = 250,
    @SerialName("cost_bigfive")        val bigFive:       Long = 300
) {
    /** Ціна за ідентифікатором тесту — щоб не писати when по всьому коду. */
    fun of(test: TestId): Long = when (test) {
        TestId.ARCHETYPE     -> archetype
        TestId.TYPES16_DEEP  -> flagshipDeep
        TestId.ATTACHMENT    -> attachment
        TestId.LOVE_LANGUAGE -> loveLanguage
        TestId.BIG_FIVE      -> bigFive
    }
}

/**
 * 5 тестів. Порядок оголошення = порядок у дизайні (він же порядок карток
 * на екрані Tests і вимірів портрета) — щоб два списки тестів у проєкті не
 * розходились і не плутали при читанні.
 *
 * ⚠️ Порядок тут НЕ несе логіки: ordinal ніде не зберігається і не
 * серіалізується, enum використовується лише як ключ у Costs.of().
 * Єдине справжнє джерело порядку — TestRepository.ALL.
 *
 * TYPES16_DEEP названий так історично: планувався як апгрейд слота
 * Personality Type поверх ARCHETYPE. Зараз це повноцінний окремий вимір,
 * але ключ конфігу (cost_flagship_deep) уже в картках на сервері — тому
 * ім'я лишаємо, перейменування коштувало б синхронного релізу з бекендом.
 */
enum class TestId {
    ARCHETYPE,
    TYPES16_DEEP,
    ATTACHMENT,
    LOVE_LANGUAGE,
    BIG_FIVE
}

// ── Портрет ───────────────────────────────────────────────────────────────────

@Serializable
data class Portrait(
    @SerialName("portrait_synthesis_threshold") val synthesisThreshold: Int = 3
) {
    /** Портрет складається з 4 вимірів — це структура, не тюнінг. */
    val dimensionsTotal: Int get() = 4

    /** Чи можна відкривати фінальний свод (ще потрібен rewarded-ролик). */
    fun canSynthesize(filledDimensions: Int): Boolean = filledDimensions >= synthesisThreshold
}

// ── Реклама ───────────────────────────────────────────────────────────────────

@Serializable
data class Ads(
    @SerialName("interstitial_min_interval_sec") val interstitialMinIntervalSec: Int = 150,
    @SerialName("ads_enabled")                   val adsEnabled:      Boolean = true,
    @SerialName("app_open_enabled")              val appOpenEnabled:  Boolean = true,
    @SerialName("banner_enabled")                val bannerEnabled:   Boolean = true
) {
    val interstitialMinIntervalMs: Long get() = interstitialMinIntervalSec * 1000L

    /** ads_enabled = false глушить усі формати, а не тільки свій. */
    val appOpenActive: Boolean get() = adsEnabled && appOpenEnabled
    val bannerActive:  Boolean get() = adsEnabled && bannerEnabled
}