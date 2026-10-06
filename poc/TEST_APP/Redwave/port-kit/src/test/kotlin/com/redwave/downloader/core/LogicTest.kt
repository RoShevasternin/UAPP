package com.redwave.downloader.core

import com.redwave.downloader.core.clip.ClipGate
import com.redwave.downloader.core.eq.EqCurve
import com.redwave.downloader.core.feed.RssParser
import com.redwave.downloader.core.link.ResolvedLink
import com.redwave.downloader.core.model.AppState
import com.redwave.downloader.core.model.AppStateCodec
import com.redwave.downloader.core.model.CatalogCodec
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.core.ringtone.CutHandle
import com.redwave.downloader.core.ringtone.CutSelection
import com.redwave.downloader.core.ringtone.RingtoneCut
import com.redwave.downloader.core.ringtone.SaveAs
import com.redwave.downloader.core.viz.SpectrumAnalyzer
import kotlin.math.PI
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ClipGateTest {

    @Test fun readsOnlyNewClips_andDedupes() {
        val g = ClipGate()
        assertTrue(g.shouldRead(100L, hasText = true))
        assertNotNull(g.onClipText(100L, "https://x.com/a.mp3"))
        // той самий timestamp → не читаємо (немає тосту Android 12+)
        assertEquals(false, g.shouldRead(100L, hasText = true))
        assertEquals(false, g.shouldRead(200L, hasText = false))
        // новий кліп, але заблокований сервіс → картки немає
        assertTrue(g.shouldRead(300L, true))
        assertNull(g.onClipText(300L, "https://youtu.be/x"))
        // закрита картка не повертається
        g.dismiss("https://x.com/b.mp3")
        assertNull(g.onClipText(400L, "https://x.com/b.mp3"))
        assertIs<ResolvedLink.PodcastFeed>(g.onClipText(500L, "https://feeds.a.fm/show.rss"))
    }

    @Test fun api24_noTimestamp_dedupByText() {
        val g = ClipGate()
        assertTrue(g.shouldRead(null, true))
        assertNotNull(g.onClipText(null, "https://x.com/a.mp3"))
        assertNull(g.onClipText(null, "https://x.com/a.mp3"))
    }

    @Test fun dismissedListIsBounded() {
        val g = ClipGate()
        repeat(80) { g.dismiss("https://x.com/$it.mp3") }
        assertEquals(ClipGate.DISMISSED_LIMIT, g.dismissed.size)
        assertEquals("https://x.com/79.mp3", g.dismissed.last())
    }
}

class RingtoneCutTest {

    @Test fun defaultSelection() {
        val s = RingtoneCut.default(226_000)
        assertEquals(67_800, s.startMs); assertEquals(25_000, s.lengthMs)
        val short = RingtoneCut.default(10_000)
        assertEquals(3_000, short.startMs); assertEquals(10_000, short.endMs)
        assertEquals(CutSelection(0, 2_000), RingtoneCut.default(2_000))
    }

    @Test fun dragKeepsLimits() {
        val d = 226_000L
        val s = CutSelection(60_000, 85_000)
        // ручка старту не проходить крізь кінець (мін. 3 с)
        assertEquals(82_000, RingtoneCut.drag(s, CutHandle.START, 90_000, d).startMs)
        // і не робить фрагмент довшим за 40 с
        assertEquals(45_000, RingtoneCut.drag(s, CutHandle.START, 0, d).startMs)
        // кінець не виходить за трек і не довший за 40 с
        assertEquals(100_000, RingtoneCut.drag(s, CutHandle.END, 300_000, d).endMs)
        assertEquals(63_000, RingtoneCut.drag(s, CutHandle.END, 0, d).endMs)
        assertEquals(CutHandle.END, RingtoneCut.nearestHandle(s, 80_000))
        assertEquals(CutHandle.START, RingtoneCut.nearestHandle(s, 61_000))
    }

    @Test fun fadesAndNames() {
        assertEquals(1_500, RingtoneCut.fadeMs(25_000))
        assertEquals(1_000, RingtoneCut.fadeMs(4_000))
        assertEquals("Night Drive (ringtone).m4a", RingtoneCut.fileName("Night Drive", SaveAs.RINGTONE))
        assertEquals("AC DC (alarm).m4a", RingtoneCut.fileName("AC/DC", SaveAs.ALARM))
    }
}

class EqCurveTest {

    @Test fun interpolatesOnLogFrequency() {
        val c = EqCurve.PRESETS.getValue("Bass Boost")
        assertEquals(8f, EqCurve.dbAt(c, 30f))
        assertEquals(1f, EqCurve.dbAt(c, 20_000f))
        assertEquals(5f, EqCurve.dbAt(c, 230f), 0.001f)
        val mid = EqCurve.dbAt(c, 117.5f) // ≈ середина 60..230 в log
        assertTrue(mid in 6.4f..6.6f, "mid=$mid")
    }

    @Test fun mapsToTypicalDeviceBands() {
        // Типовий Equalizer на Android: 5 смуг, центри в мГц, діапазон ±15 дБ
        val centers = intArrayOf(60_000, 230_000, 910_000, 3_600_000, 14_000_000)
        val lv = EqCurve.levelsForDevice(EqCurve.PRESETS.getValue("Lo-fi"), centers, -1500, 1500)
        assertEquals(listOf<Short>(300, 200, -100, -400, -800), lv.toList())
        // Вузький діапазон пристрою — обрізаємо
        val clipped = EqCurve.levelsForDevice(floatArrayOf(12f, 0f, 0f, 0f, -12f), centers, -1000, 1000)
        assertEquals(1000.toShort(), clipped.first()); assertEquals((-1000).toShort(), clipped.last())
    }
}

class SpectrumAnalyzerTest {

    private fun sine(freq: Double, rate: Int, n: Int, amp: Double = 0.8) =
        ShortArray(n * 2) { i -> (sin(2 * PI * freq * (i / 2) / rate) * amp * 32767).toInt().toShort() }

    @Test fun fftFindsSinePeak() {
        val n = 1024; val re = FloatArray(n) { sin(2 * PI * 64 * it / n).toFloat() }; val im = FloatArray(n)
        SpectrumAnalyzer.fft(re, im)
        val mags = (0 until n / 2).map { kotlin.math.hypot(re[it], im[it]) }
        assertEquals(64, mags.indices.maxBy { mags[it] })
    }

    @Test fun lowToneLightsLowBands_highToneHighBands() {
        val a = SpectrumAnalyzer()
        a.pushPcm16(sine(120.0, 44100, 4096), channels = 2)
        val low = a.bands(true).copyOf()
        assertTrue(low.take(8).max() > 0.6f, "low=${low.toList()}")
        assertTrue(low.takeLast(10).max() < 0.4f)

        val b = SpectrumAnalyzer()
        b.pushPcm16(sine(9000.0, 44100, 4096), channels = 2)
        val high = b.bands(true)
        assertTrue(high.indices.maxBy { high[it] } > 30, "high=${high.toList()}")
    }

    @Test fun silenceAndDecay() {
        val a = SpectrumAnalyzer()
        a.pushPcm16(ShortArray(4096), channels = 2)
        assertTrue(a.bands(true).all { it == 0f })
        a.pushPcm16(sine(200.0, 44100, 4096), 2)
        val peak = a.bands(true).max()
        repeat(10) { a.bands(false) }
        assertTrue(a.bands(false).max() < peak * 0.25f)
    }
}

class CodecTest {

    private val rss = """
        <?xml version="1.0" encoding="UTF-8"?>
        <rss version="2.0" xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">
          <channel>
            <title>Indie Hour (test feed)</title>
            <itunes:image href="https://x.com/cover.jpg"/>
            <image><url>https://x.com/other.jpg</url><title>ignored</title></image>
            <item>
              <title>Ep. 112 · Bedroom pop on a budget</title>
              <guid>e112</guid>
              <itunes:duration>1:00</itunes:duration>
              <enclosure url="https://x.com/e112.mp3" length="483840" type="audio/mpeg"/>
            </item>
            <item><title>No audio</title></item>
            <item>
              <title>Ep. 111</title>
              <itunes:duration>3120</itunes:duration>
              <enclosure url="https://x.com/e111.mp3" length="1" type="audio/mpeg"/>
            </item>
          </channel>
        </rss>
    """.trimIndent()

    @Test fun parsesRss() {
        val f = RssParser.parse("https://x.com/feed.rss", rss.byteInputStream())
        assertEquals("Indie Hour (test feed)", f.title)
        assertEquals("https://x.com/cover.jpg", f.imageUrl)
        assertEquals(2, f.episodes.size)
        assertEquals(60, f.episodes[0].durationSec)
        assertEquals(483840, f.episodes[0].lengthBytes)
        assertEquals("https://x.com/e111.mp3", f.episodes[1].guid)
        assertEquals(3723, RssParser.parseDuration("1:02:03"))
        assertNull(RssParser.parseDuration("abc"))
    }

    @Test fun rejectsDoctype() {
        val evil = """<?xml version="1.0"?><!DOCTYPE r [<!ENTITY x SYSTEM "file:///etc/passwd">]><rss><channel><title>&x;</title></channel></rss>"""
        val failed = runCatching { RssParser.parse("u", evil.byteInputStream()) }.isFailure
        assertTrue(failed)
    }

    @Test fun catalogAndState() {
        val json = """{"version":1,"tracks":[{"id":"neon-heart","title":"Neon Heart","artist":"Mira Vale","license":"CC0",
            "mood":["Chill"],"durationSec":45,"sizeBytes":720000,"format":"MP3","url":"https://x/neon-heart.mp3","cover":"https://x/c.jpg","extra":1}],
            "featured":[{"id":"f1","title":"Live Energy","subtitle":"18 tracks","mood":"Live"}]}"""
        val c = CatalogCodec.decode(json)
        assertEquals("Neon Heart", c.tracks.single().title)
        assertEquals("Live", c.featured.single().mood)

        val st = AppState(onboarded = true, library = listOf(Track("t1", "Neon Heart", "Mira Vale", 204_000, 4_700_000, "MP3")))
        assertEquals(st, AppStateCodec.decodeOrDefault(AppStateCodec.encode(st)))
        assertEquals(AppState(), AppStateCodec.decodeOrDefault("{broken"))
    }
}
