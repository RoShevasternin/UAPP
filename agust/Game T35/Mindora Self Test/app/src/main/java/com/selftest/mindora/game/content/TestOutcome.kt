package com.selftest.mindora.game.content

// ═════════════════════════════════════════════════════════════════════════════
//  TestScoring — єдиний движок підрахунку для всіх 5 тестів.
//
//  Вхід:  TestContent + список відповідей у порядку questions.
//         scale  → значення 1..scaleSize
//         choice → індекс вибраної опції (0-based)
//
//  Зафіксовані правила:
//    · Пороги СТРОГО більше: sum > threshold → полюс/high, інакше опозит/low.
//    · Нічия в count_max → пріоритет за порядком categories у JSON.
//    · Reverse: (scaleSize + 1) − відповідь.
//    · Опція choice без category (Yes/No: варіант "No") очок не додає.
// ═════════════════════════════════════════════════════════════════════════════

data class TestOutcome(
    val testId    : String,
    val resultIds : List<String>,
    val scores    : Map<String, Int>,
) {
    val primaryId: String get() = resultIds.first()
}

object TestScoring {

    fun score(test: TestContent, answers: List<Int>): TestOutcome {
        require(answers.size == test.questions.size) {
            "Test '${test.id}': answers=${answers.size}, questions=${test.questions.size}"
        }
        return when (test.engine) {
            "axis"      -> scoreAxis(test, answers)
            "count_max" -> scoreCountMax(test, answers)
            else        -> error("Unknown engine '${test.engine}' in test '${test.id}'")
        }
    }

    // ── axis ──────────────────────────────────────────────────────────────────

    private fun scoreAxis(test: TestContent, answers: List<Int>): TestOutcome {
        val sums = LinkedHashMap<String, Int>()
        test.axes.forEach { sums[it.id] = 0 }

        test.questions.forEachIndexed { i, q ->
            val axis = q.axis ?: error("Test '${test.id}': question '${q.id}' has no axis")
            val raw  = answers[i].coerceIn(1, test.scaleSize)
            val v    = if (q.reverse) (test.scaleSize + 1) - raw else raw
            sums[axis] = (sums[axis] ?: 0) + v
        }

        val resultIds = when (test.resultRule) {

            "letters" -> {
                val type = test.axes.joinToString("") { axis ->
                    if (sums.getValue(axis.id) > axis.threshold) axis.pole else axis.opposite
                }
                listOf(type.lowercase())
            }

            "matrix" -> {
                val key = test.axes.joinToString("_") { axis ->
                    if (sums.getValue(axis.id) > axis.threshold) "high" else "low"
                }
                listOf(test.matrix[key] ?: error("Test '${test.id}': matrix has no key '$key'"))
            }

            "per_axis" -> test.axes.map { axis ->
                val level = if (sums.getValue(axis.id) > axis.threshold) "high" else "low"
                "${axis.id}_$level"
            }

            else -> error("Unsupported resultRule '${test.resultRule}' for axis test '${test.id}'")
        }

        return TestOutcome(test.id, resultIds, sums)
    }

    // ── count_max ─────────────────────────────────────────────────────────────

    private fun scoreCountMax(test: TestContent, answers: List<Int>): TestOutcome {
        val sums = LinkedHashMap<String, Int>()
        test.categories.forEach { sums[it] = 0 }

        test.questions.forEachIndexed { i, q ->
            if (test.isScale) {
                val cat = q.category ?: error("Test '${test.id}': question '${q.id}' has no category")
                val raw = answers[i].coerceIn(1, test.scaleSize)
                val v   = if (q.reverse) (test.scaleSize + 1) - raw else raw
                sums[cat] = (sums[cat] ?: 0) + v
            } else {
                val idx = answers[i]
                require(idx in q.options.indices) {
                    "Test '${test.id}': question '${q.id}' option index $idx out of ${q.options.size}"
                }
                // Опція без category ("No") легально не дає очок
                q.options[idx].category?.let { cat ->
                    sums[cat] = (sums[cat] ?: 0) + 1
                }
            }
        }

        val best   = sums.values.max()
        val winner = test.categories.first { sums.getValue(it) == best }

        val resultIds = if (test.secondaryResult && sums.size > 1) {
            val restBest  = sums.filterKeys { it != winner }.values.max()
            val secondary = test.categories.first { it != winner && sums.getValue(it) == restBest }
            listOf(winner, secondary)
        } else listOf(winner)

        return TestOutcome(test.id, resultIds, sums)
    }
}