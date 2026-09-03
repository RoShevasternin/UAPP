package com.selftest.mindora.game.content

import com.selftest.mindora.config.TestId

// ═════════════════════════════════════════════════════════════════════════════
//  TestCatalog — вітрина тестів: те, що видно в СПИСКУ, а не всередині теста.
//
//  ЧОМУ НЕ В *.json ТЕСТА: subtitle і порядок у списку — це маркетинг
//  вітрини, а tests/*.json — це контент проходження (питання, осі, тексти
//  результатів). Змішувати їх означало б, що зміна одного рядка підпису
//  вимагає чіпати файл зі скорингом.
//
//  ЧОМУ ТУТ МАПА НА TestId: ціни живуть у конфізі під enum-ключами
//  (cost_archetype, cost_flagship_deep…), а контент — під рядковими id
//  (archetype, types16…). Місток між цими двома світами потрібен рівно
//  один, і він тут. Хто хоче ціну за рядковим id — бере звідси testId
//  і питає config.costs.of(...), а не пише свій when.
//
//  ⚠️ ПОРЯДОК НЕ ДУБЛЮЄТЬСЯ. ALL будується поверх TestRepository.ALL, тож
//  список тестів у проєкті рівно один. Раніше порядок був виписаний
//  вручну і вже встиг розійтися з enum TestId — саме тому тут mapIndexed,
//  а не другий літерал списку.
// ═════════════════════════════════════════════════════════════════════════════
object TestCatalog {

    data class Entry(
        val id           : String,
        val testId       : TestId,
        val index        : Int,
        val subtitle     : String,
        /** Рядок над назвою результату: «Your Archetype», «Your love language». */
        val resultKicker : String,
    )

    /** id → (ключ ціни, підпис на картці). Порядку тут немає — тільки дані. */
    private val META: Map<String, Triple<TestId, String, String>> = mapOf(
        TestRepository.ARCHETYPE     to Triple(TestId.ARCHETYPE,     "Reveal the role that shapes you",      "Your Archetype"),
        TestRepository.TYPES16       to Triple(TestId.TYPES16_DEEP,  "Understand how you think and decide",  "Your Personality type"),
        TestRepository.ATTACHMENT    to Triple(TestId.ATTACHMENT,    "See how you connect with others",      "Your attachment style"),
        TestRepository.LOVE_LANGUAGE to Triple(TestId.LOVE_LANGUAGE, "Learn what makes you feel valued",     "Your love language"),
        TestRepository.BIG_FIVE      to Triple(TestId.BIG_FIVE,      "Measure the traits that shape you",    "Your traits"),
    )

    val ALL: List<Entry> = TestRepository.ALL.mapIndexed { i, id ->
        val (testId, subtitle, kicker) = META[id]
            ?: error("TestCatalog: немає META для '$id'")
        Entry(id = id, testId = testId, index = i, subtitle = subtitle, resultKicker = kicker)
    }

    private val byId = ALL.associateBy { it.id }

    fun byId(id: String): Entry = byId[id] ?: error("TestCatalog: невідомий тест '$id'")
}