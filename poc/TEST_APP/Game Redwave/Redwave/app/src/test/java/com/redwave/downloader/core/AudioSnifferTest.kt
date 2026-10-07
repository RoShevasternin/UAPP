package com.redwave.downloader.core

import com.redwave.downloader.core.link.AudioSniffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AudioSnifferTest {

    private fun bytes(vararg v: Int) = ByteArray(v.size) { v[it].toByte() }
    private fun ascii(s: String, pad: Int = 16) = (s.toByteArray(Charsets.ISO_8859_1) + ByteArray(pad))

    @Test fun magic() {
        assertEquals("mp3", AudioSniffer.byMagic(ascii("ID3\u0004")))
        assertEquals("mp3", AudioSniffer.byMagic(bytes(0xFF, 0xFB, 0x90, 0x64)))
        assertEquals("aac", AudioSniffer.byMagic(bytes(0xFF, 0xF1, 0x50, 0x80)))
        assertEquals("flac", AudioSniffer.byMagic(ascii("fLaC")))
        assertEquals("ogg", AudioSniffer.byMagic(ascii("OggS\u0000\u0002")))
        assertEquals("opus", AudioSniffer.byMagic(ascii("OggS\u0000\u0002xxxxxxxxxxxxxxxxxxxxxxOpusHead")))
        assertEquals("wav", AudioSniffer.byMagic(ascii("RIFF$\u0000\u0000\u0000WAVEfmt ")))
        assertEquals("m4a", AudioSniffer.byMagic(ascii("\u0000\u0000\u0000 ftypM4A ")))
        assertEquals("html", AudioSniffer.byMagic(ascii("  <!DOCTYPE html><html>")))
        assertNull(AudioSniffer.byMagic(ascii("PK\u0003\u0004")))
    }

    @Test fun rawGithubOctetStream_isAcceptedByMagic() {
        val d = AudioSniffer.decide("application/octet-stream", null, "neon-heart.mp3", ascii("ID3\u0003"))
        assertTrue(d.isAudio); assertEquals("mp3", d.ext); assertEquals("magic bytes", d.reason)
    }

    @Test fun octetStream_noMagic_butExtension() {
        val d = AudioSniffer.decide("application/octet-stream", null, "track.flac", null)
        assertTrue(d.isAudio); assertEquals("flac", d.ext)
    }

    @Test fun htmlPage_rejected_evenIfNamedMp3() {
        val d = AudioSniffer.decide("application/octet-stream", null, "song.mp3", ascii("<!doctype html><html>"))
        assertFalse(d.isAudio)
        assertFalse(AudioSniffer.decide("text/html; charset=utf-8", null, "page", null).isAudio)
    }

    @Test fun audioContentType() {
        val d = AudioSniffer.decide("audio/mpeg", null, null, null)
        assertTrue(d.isAudio); assertEquals("mp3", d.ext)
        assertEquals("m4a", AudioSniffer.decide("audio/mp4", null, null, null).ext)
    }

    @Test fun contentDisposition() {
        assertEquals("Rehearsal 14 Oct.m4a", AudioSniffer.fileNameFromDisposition("attachment; filename=\"Rehearsal 14 Oct.m4a\""))
        assertEquals("Пісня.mp3", AudioSniffer.fileNameFromDisposition("attachment; filename=\"x.mp3\"; filename*=UTF-8''%D0%9F%D1%96%D1%81%D0%BD%D1%8F.mp3"))
        assertEquals("a.wav", AudioSniffer.fileNameFromDisposition("attachment; filename=a.wav"))
        // raw.githubusercontent.com кладе в filename шлях від кореня репо
        assertEquals("neon-heart.mp3", AudioSniffer.fileNameFromDisposition("attachment; filename=poc/TEST_APP/Game Redwave/test-media/neon-heart.mp3"))
        assertNull(AudioSniffer.fileNameFromDisposition(null))
        val d = AudioSniffer.decide("application/octet-stream", "attachment; filename=\"demo.ogg\"", "uc", null)
        assertTrue(d.isAudio); assertEquals("ogg", d.ext); assertEquals("demo.ogg", d.fileName)
    }
}
