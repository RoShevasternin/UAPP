package com.vidtools.framefall.core

import com.vidtools.framefall.core.link.VideoSniffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Чи справді за лінком відео: Content-Type, магія перших байтів, Content-Disposition. */
class VideoSnifferTest {
    private fun bytes(vararg parts: Any): ByteArray = parts.flatMap { p ->
        when (p) { is String -> p.toByteArray(Charsets.ISO_8859_1).toList(); is Int -> listOf(p.toByte()); else -> emptyList() }
    }.toByteArray()

    @Test fun magic() {
        assertEquals("mp4", VideoSniffer.byMagic(bytes(0, 0, 0, 32, "ftypisom")))
        assertEquals("mov", VideoSniffer.byMagic(bytes(0, 0, 0, 20, "ftypqt  ")))
        assertEquals("m4a", VideoSniffer.byMagic(bytes(0, 0, 0, 32, "ftypM4A ")))
        assertEquals("webm", VideoSniffer.byMagic(bytes(0x1A, 0x45, 0xDF, 0xA3, "....webm")))
        assertEquals("mkv", VideoSniffer.byMagic(bytes(0x1A, 0x45, 0xDF, 0xA3, "....matroska")))
        assertEquals("hls", VideoSniffer.byMagic(bytes("#EXTM3U\n#EXT-X-VERSION:3")))
        assertEquals("html", VideoSniffer.byMagic(bytes("<!DOCTYPE html><html>")))
        assertEquals("ogg", VideoSniffer.byMagic(bytes("OggS")))
    }

    @Test fun octetStreamTrustsMagic() {
        val d = VideoSniffer.decide("application/octet-stream", null, "uc", bytes(0, 0, 0, 32, "ftypmp42"))
        assertTrue(d.ok); assertEquals("mp4", d.ext)
    }

    @Test fun webPageAndStreamsAreRejected() {
        val page = VideoSniffer.decide("text/html; charset=utf-8", null, "watch", bytes("<html>"))
        assertFalse(page.ok); assertEquals(VideoSniffer.Problem.WEB_PAGE, page.problem)
        val hls = VideoSniffer.decide("application/vnd.apple.mpegurl", null, "x.m3u8", bytes("#EXTM3U"))
        assertFalse(hls.ok); assertEquals(VideoSniffer.Problem.STREAM, hls.problem)
        val theora = VideoSniffer.decide("video/ogg", null, "a.ogv", bytes("OggS"))
        assertFalse(theora.ok); assertEquals(VideoSniffer.Problem.UNSUPPORTED_CODEC, theora.problem)
    }

    @Test fun dispositionName() {
        assertEquals("Wedding teaser.mp4", VideoSniffer.fileNameFromDisposition("attachment; filename*=UTF-8''Wedding%20teaser.mp4"))
        assertEquals("a.mp4", VideoSniffer.fileNameFromDisposition("attachment; filename=\"dir/a.mp4\""))
        val d = VideoSniffer.decide("video/mp4", "attachment; filename=\"clip.mov\"", "uc", null)
        assertTrue(d.ok); assertEquals("clip.mov", d.fileName)
    }
}
