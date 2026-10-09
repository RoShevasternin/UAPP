package com.driftglass.home.core

import com.driftglass.home.core.i18n.L
import com.driftglass.home.core.i18n.Lang
import com.driftglass.home.core.logic.DayCycle
import com.driftglass.home.core.logic.HomeWallpaper
import com.driftglass.home.core.logic.PowerPolicy
import com.driftglass.home.core.logic.ShufflePolicy
import com.driftglass.home.core.model.AppState
import com.driftglass.home.core.model.AppStateCodec
import com.driftglass.home.core.model.Catalog
import com.driftglass.home.core.model.DaySlot
import com.driftglass.home.core.model.Palettes
import com.driftglass.home.core.model.Saver
import com.driftglass.home.core.model.Shuffle
import com.driftglass.home.core.model.Style
import com.driftglass.home.core.model.Wallpaper
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PoliciesTest {

    @Test fun dayCycleSlots() {
        assertEquals(DaySlot.NIGHT, DayCycle.slotOf(4.99f))
        assertEquals(DaySlot.DAWN, DayCycle.slotOf(5f))
        assertEquals(DaySlot.DAY, DayCycle.slotOf(13f))
        assertEquals(DaySlot.DUSK, DayCycle.slotOf(18.67f))
        assertEquals(DaySlot.NIGHT, DayCycle.slotOf(23.17f))
        assertEquals(10f, DayCycle.nextBoundary(6.5f))
        assertEquals(29f, DayCycle.nextBoundary(22f))
    }

    @Test fun homeWallpaperFollowsDayCycle() {
        val s = AppState(applied = "tidepool", dayCycle = true)
        assertEquals("sahara-dusk", HomeWallpaper.current(s, 18.6f).id)
        assertEquals("tidepool", HomeWallpaper.current(s.copy(dayCycle = false), 18.6f).id)
    }

    @Test fun shuffleSchedule() {
        val h = ShufflePolicy.HOUR_MS
        assertTrue(ShufflePolicy.isDue(Shuffle.HOURLY, 0L, h))
        assertFalse(ShufflePolicy.isDue(Shuffle.HOURLY, 0L, h - 1))
        assertFalse(ShufflePolicy.isDue(Shuffle.DAILY, 0L, 23 * h))
        assertFalse(ShufflePolicy.isDue(Shuffle.UNLOCK, 0L, Long.MAX_VALUE))
    }

    @Test fun shufflePoolUsesFavoritesWhenAuto() {
        val s = AppState(favorites = listOf("pearl", "lava-lamp"))
        assertEquals(listOf("pearl", "lava-lamp"), ShufflePolicy.pool(s, auto = true))
        assertEquals(Catalog.ALL.size, ShufflePolicy.pool(s, auto = false).size)
        assertEquals(Catalog.ALL.size, ShufflePolicy.pool(s.copy(favorites = listOf("pearl")), auto = true).size)
    }

    @Test fun pickNextNeverRepeats() {
        val pool = listOf("a", "b", "c")
        repeat(50) { assertNotEquals("b", ShufflePolicy.pickNext(pool, "b", Random(it))) }
        assertEquals("a", ShufflePolicy.pickNext(listOf("a"), "a"))
    }

    @Test fun powerPolicy() {
        assertTrue(PowerPolicy.saverOn(Saver.AUTO, 19, charging = false))
        assertFalse(PowerPolicy.saverOn(Saver.AUTO, 19, charging = true))
        assertFalse(PowerPolicy.saverOn(Saver.AUTO, 20, charging = false))
        assertTrue(PowerPolicy.saverOn(Saver.ON, 90, charging = true))
        assertFalse(PowerPolicy.saverOn(Saver.OFF, 5, charging = false))
    }

    @Test fun catalogMatchesPrototype() {
        assertEquals(24, Catalog.ALL.size)
        assertEquals(Catalog.ALL.size, Catalog.ALL.map { it.id }.toSet().size)
        assertEquals(Style.LIQUID, Catalog.byId("tidepool")!!.style)
        // seed як у прototипі: id.length * 1.37 % 7
        assertEquals((8 * 1.37f) % 7f, Catalog.byId("tidepool")!!.seed, 1e-5f)
        assertEquals(3.1f, Catalog.byId("ember-drift")!!.seed)
    }

    @Test fun paletteRgb() {
        val c = Palettes.SEA.rgb(0)
        assertEquals(0x5F / 255f, c[0], 1e-6f)
        assertEquals(0xF2 / 255f, c[1], 1e-6f)
    }

    @Test fun stateRoundTrip() {
        val mine = Wallpaper("mine-1", "", Style.GLASS, "night", 1.2f, 0.9f, 0.1f, 4.2f, number = 1)
        val s = AppState(onboarded = true, created = listOf(mine), shuffle = Shuffle.HOURLY, dayCycle = true)
        val back = AppStateCodec.decodeOrDefault(AppStateCodec.encode(s))
        assertEquals(s, back)
        assertEquals(2, back.nextNumber)
        assertEquals(AppState(), AppStateCodec.decodeOrDefault("{broken"))
    }

    @Test fun languages() {
        assertEquals(Lang.UK, Lang.fromSystem("uk"))
        assertEquals(Lang.EN, Lang.fromSystem("de"))
        L.lang = Lang.RU
        assertEquals("Обзор", L.discover)
        L.lang = Lang.UK
        assertEquals("Скло #3", L.glassName(3))
        L.lang = Lang.EN
    }
}
