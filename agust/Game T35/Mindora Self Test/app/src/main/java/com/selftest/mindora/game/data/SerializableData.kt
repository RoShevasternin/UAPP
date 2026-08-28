package com.selftest.mindora.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    // Версія схеми — підвищуй при НЕсумісних змінах (зміна типу поля тощо).
    // Сумісні зміни (нове поле з дефолтом) версію змінювати НЕ потребують.
    val schemaVersion : Int = PlayerDataMigration.CURRENT_VERSION,

    val lumens             : Long = 0L,

    // ── Daily Reward ──
    // dailyRewardDay  — день циклу 1..7 (яку клітинку підсвітити / яка сума)
    // dailyStreak     — днів підряд, НЕ обнуляється на 8-му дні
    // dailyRewardTime — millis останнього клейму (0 = ще не забирав)
    val dailyRewardDay  : Int  = 1,
    val dailyRewardTime : Long = 0L,
    val dailyStreak     : Int  = 0,

    // ── Портрет ──
    // testResults     — testId → збережений результат (archetype, types16,
    //                   attachment, love_language, big_five)
    // portraitTitleId — id титулу з synthesis.json, зафіксований при першому
    //                   синтезі; null = портрет ще не відкрито
    val testResults     : Map<String, SavedTestResult> = emptyMap(),
    val portraitTitleId : String? = null,

    // ── Покупки ──
    // Куплені, але ще не обов'язково пройдені тести. Пройдений тест теж
    // вважається відкритим (див. PlayerModel.isTestUnlocked), тож старі
    // збереження без цього поля не втрачають доступ до вже пройдених.
    //
    // Нове поле з дефолтом → міграція НЕ потрібна, CURRENT_VERSION не рухаємо.
    val purchasedTests  : Set<String> = emptySet(),

    val isFirstOpen : Boolean = true,
)

/**
 * Збережений результат одного теста.
 *
 * resultIds — що показувати:
 *   archetype / attachment / love_language → 1 id
 *   types16                               → 1 id ("intj")
 *   big_five                              → 5 id у порядку осей
 *                                            ("openness_high", …)
 * scores — сирі суми по осях/категоріях: для "Take Again"-порівнянь,
 *          майбутньої аналітики і щоб не перераховувати відповіді.
 */
@Serializable
data class SavedTestResult(
    val resultIds : List<String>     = emptyList(),
    val scores    : Map<String, Int> = emptyMap(),
)