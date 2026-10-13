package com.vidtools.framefall.core

import com.vidtools.framefall.core.model.AppEvent
import com.vidtools.framefall.core.model.AppState
import com.vidtools.framefall.core.model.AppStateCodec
import com.vidtools.framefall.core.model.EventKind
import com.vidtools.framefall.core.model.unseenEvents
import com.vidtools.framefall.core.model.withEvent
import kotlin.test.Test
import kotlin.test.assertEquals

/** Центр подій: порядок, ліміт, дубль лінка з буфера, непереглянуті, збереження в JSON. */
class EventsTest {
    private fun ev(id: String, kind: EventKind, at: Long, url: String? = null) = AppEvent(id, kind, id, url = url, at = at)

    @Test fun newestFirstAndCapped() {
        var s = AppState()
        repeat(40) { s = s.withEvent(ev("e$it", EventKind.DOWNLOADED, it.toLong())) }
        assertEquals(30, s.events.size)
        assertEquals("e39", s.events.first().id)
    }

    @Test fun sameClipLinkMovesUpInsteadOfDuplicating() {
        val s = AppState()
            .withEvent(ev("a", EventKind.CLIP_LINK, 1, "https://x/a.mp4"))
            .withEvent(ev("b", EventKind.DOWNLOADED, 2))
            .withEvent(ev("c", EventKind.CLIP_LINK, 3, "https://x/a.mp4"))
        assertEquals(listOf("c", "b"), s.events.map { it.id })
    }

    @Test fun unseenCountsAfterSeenAt() {
        val s = AppState(eventsSeenAt = 5).withEvent(ev("old", EventKind.FAILED, 4)).withEvent(ev("new", EventKind.FAILED, 6))
        assertEquals(1, s.unseenEvents)
    }

    @Test fun survivesJsonRoundTrip() {
        val s = AppState().withEvent(ev("a", EventKind.CLIP_LINK, 1, "https://x/a.mp4"))
        assertEquals(s.events, AppStateCodec.decodeOrDefault(AppStateCodec.encode(s)).events)
    }
}
