package com.vidtools.framefall.core

import com.vidtools.framefall.core.clip.ClipGate
import com.vidtools.framefall.core.link.ResolvedLink
import com.vidtools.framefall.core.model.AppState
import com.vidtools.framefall.core.model.AppStateCodec
import com.vidtools.framefall.core.model.CatalogCodec
import com.vidtools.framefall.core.model.Video
import java.io.File
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
        assertNotNull(g.onClipText(100L, "https://cdn.example.com/a.mp4"))
        // той самий timestamp → не читаємо (немає тосту Android 12+)
        assertEquals(false, g.shouldRead(100L, hasText = true))
        assertEquals(false, g.shouldRead(200L, hasText = false))
        // новий кліп, але заблокований сервіс або стрім → картки немає
        assertNull(g.onClipText(300L, "https://youtu.be/x"))
        assertNull(g.onClipText(310L, "https://cdn.example.com/live.m3u8"))
        // закрита картка не повертається
        g.dismiss("https://cdn.example.com/b.mp4")
        assertNull(g.onClipText(400L, "https://cdn.example.com/b.mp4"))
        assertIs<ResolvedLink.ArchiveItem>(g.onClipText(500L, "https://archive.org/details/Sintel"))
    }

    @Test fun api24_noTimestamp_dedupByText() {
        val g = ClipGate()
        assertTrue(g.shouldRead(null, true))
        assertNotNull(g.onClipText(null, "https://cdn.example.com/a.mp4"))
        assertNull(g.onClipText(null, "https://cdn.example.com/a.mp4"))
    }

    @Test fun dismissedListIsBounded() {
        val g = ClipGate()
        repeat(80) { g.dismiss("https://cdn.example.com/$it.mp4") }
        assertEquals(ClipGate.DISMISSED_LIMIT, g.dismissed.size)
        assertEquals("https://cdn.example.com/79.mp4", g.dismissed.last())
    }
}

class StateTest {

    @Test fun stateRoundTripAndBrokenJson() {
        val v = Video("1", "Sintel", "Blender Foundation", 888_000, 77_400_000, "MP4", 564, 240, watchedMs = 60_000, lastWatchedAt = 5)
        val s = AppState(onboarded = true, library = listOf(v), homeBg = "/data/homebg/1.mp4")
        assertEquals(s, AppStateCodec.decodeOrDefault(AppStateCodec.encode(s)))
        assertEquals(AppState(), AppStateCodec.decodeOrDefault("{broken"))
        assertTrue(v.inProgress)
        assertTrue(v.copy(watchedMs = 880_000).inProgress.not())
    }

    /** Справжній assets/catalog.json: читається, у кожного фільму є файл і мініатюра. */
    @Test fun bundledCatalog() {
        val file = listOf("src/main/assets/catalog.json", "app/src/main/assets/catalog.json").map(::File).first { it.exists() }
        val c = CatalogCodec.decode(file.readText())
        assertTrue(c.films.size >= 8)
        assertNotNull(c.films.firstOrNull { it.id == c.featured })
        c.films.forEach { f ->
            assertNotNull(f.defaultFile, f.id)
            assertTrue(f.files.all { it.url.startsWith("https://") && it.sizeBytes > 0 }, f.id)
            assertTrue(File(file.parentFile, "textures/thumbs/${f.thumb}.jpg").exists(), f.thumb)
        }
    }
}
