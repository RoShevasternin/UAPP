package com.driftglass.home.core.logic

import com.driftglass.home.core.model.AppState
import com.driftglass.home.core.model.Catalog
import com.driftglass.home.core.model.DaySlot
import com.driftglass.home.core.model.Saver
import com.driftglass.home.core.model.Shuffle
import com.driftglass.home.core.model.Wallpaper
import kotlin.random.Random

// ═════════════════════════════════════════════════════════════════════════════
//  Чиста логіка без Android і GDX (тести — test/.../PoliciesTest.kt).
// ═════════════════════════════════════════════════════════════════════════════

object DayCycle {
    /** Як у прототипі: світанок 5–10, день 10–17, захід 17–21, ніч 21–5. [hour] — 0..24 з дробом. */
    fun slotOf(hour: Float): DaySlot = when {
        hour >= 5f && hour < 10f  -> DaySlot.DAWN
        hour >= 10f && hour < 17f -> DaySlot.DAY
        hour >= 17f && hour < 21f -> DaySlot.DUSK
        else                      -> DaySlot.NIGHT
    }

    /** Наступна межа слоту після [hour] — коли перемалювати Home (година, 0..24). */
    fun nextBoundary(hour: Float): Float = when {
        hour < 5f  -> 5f
        hour < 10f -> 10f
        hour < 17f -> 17f
        hour < 21f -> 21f
        else       -> 29f   // 5:00 наступного дня
    }
}

object HomeWallpaper {
    /** Що зараз має стояти на Home: слот циклу дня або вибране вручну. */
    fun current(s: AppState, hour: Float): Wallpaper {
        val id = if (s.dayCycle) s.slots[DayCycle.slotOf(hour)] ?: s.applied else s.applied
        return s.wallpaper(id) ?: s.wallpaper(s.applied) ?: Catalog.ALL.first()
    }
}

object ShufflePolicy {
    const val HOUR_MS = 60L * 60 * 1000
    const val DAY_MS = 24 * HOUR_MS

    /** Чи пора змінити шпалери за розкладом (щогодини / щодня). UNLOCK і OFF — ні: їх веде подія. */
    fun isDue(mode: Shuffle, changedAt: Long, now: Long): Boolean = when (mode) {
        Shuffle.HOURLY -> now - changedAt >= HOUR_MS
        Shuffle.DAILY  -> now - changedAt >= DAY_MS
        else           -> false
    }

    /**
     * Пул для зміни: з обраного, якщо там ≥ 2 (Auto-shuffle), інакше весь каталог.
     * Ручний shuffle (подвійний тап) — завжди весь каталог + створені.
     */
    fun pool(s: AppState, auto: Boolean): List<String> {
        val favs = s.favorites.filter { s.wallpaper(it) != null }
        return if (auto && favs.size >= 2) favs else Catalog.ALL.map { it.id } + s.created.map { it.id }
    }

    fun pickNext(pool: List<String>, current: String, random: Random = Random.Default): String {
        val options = pool.filter { it != current }
        return if (options.isEmpty()) current else options[random.nextInt(options.size)]
    }
}

object PowerPolicy {
    /** Режим економії: завжди, ніколи або автоматично нижче 20 % (на зарядці — ні). */
    fun saverOn(mode: Saver, batteryPct: Int, charging: Boolean): Boolean = when (mode) {
        Saver.ON   -> true
        Saver.OFF  -> false
        Saver.AUTO -> !charging && batteryPct in 0 until 20
    }

    const val FPS_NORMAL = 60
    const val FPS_SAVER = 30
    const val SPEED_SAVER = 0.5f
}
