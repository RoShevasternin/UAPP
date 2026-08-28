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
        val id      : String,
        val testId  : TestId,
        /** Позиція в списку = індекс іконки ic_ena_N / ic_dis_N (N = index+1). */
        val index   : Int,
        val subtitle: String,
    )

    /** id → (ключ ціни, підпис на картці). Порядку тут немає — тільки дані. */
    private val META: Map<String, Pair<TestId, String>> = mapOf(
        TestRepository.ARCHETYPE     to (TestId.ARCHETYPE     to "Reveal the role that shapes you"),
        TestRepository.TYPES16       to (TestId.TYPES16_DEEP  to "Understand how you think and decide"),
        TestRepository.ATTACHMENT    to (TestId.ATTACHMENT    to "See how you connect with others"),
        TestRepository.LOVE_LANGUAGE to (TestId.LOVE_LANGUAGE to "Learn what makes you feel valued"),
        TestRepository.BIG_FIVE      to (TestId.BIG_FIVE      to "Measure the traits that shape you"),
    )

    /**
     * Порядок карток на екрані. Успадковується від TestRepository.ALL —
     * додали тест туди, додайте META тут, і список зійдеться сам.
     */
    val ALL: List<Entry> = TestRepository.ALL.mapIndexed { i, id ->
        val (testId, subtitle) = META[id]
            ?: error("TestCatalog: немає META для '$id' — додай підпис і ключ ціни")
        Entry(id = id, testId = testId, index = i, subtitle = subtitle)
    }

    private val byId = ALL.associateBy { it.id }

    fun byId(id: String): Entry = byId[id] ?: error("TestCatalog: невідомий тест '$id'")
}