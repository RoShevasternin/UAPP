package com.vidtools.framefall.core

import com.vidtools.framefall.core.link.Commons
import com.vidtools.framefall.core.link.LinkResolver
import com.vidtools.framefall.core.link.ResolvedLink
import com.vidtools.framefall.core.link.chipLabel
import com.vidtools.framefall.core.link.isDownloadable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/** Що за лінк: прямі файли, хмари, відкриті архіви, стріми, заборонені сервіси. */
class LinkResolverTest {
    private fun r(s: String) = LinkResolver.resolve(s)

    @Test fun directFiles() {
        val a = assertIs<ResolvedLink.DirectVideo>(r("Watch this https://cdn.example.com/clips/wedding-teaser.mp4!"))
        assertEquals("mp4", a.ext); assertEquals("wedding-teaser.mp4", a.fileName); assertEquals("MP4", a.chipLabel)
        assertIs<ResolvedLink.DirectVideo>(r("https://cdn.example.com/a.webm"))
        assertIs<ResolvedLink.DirectVideo>(r("www.example.com/a.MOV"))
        assertIs<ResolvedLink.DirectVideo>(r("https://cdn.example.com/sound.m4a"))
        // archive.org/download/… з файлом — теж прямий
        assertIs<ResolvedLink.DirectVideo>(r("https://archive.org/download/ElephantsDream/ed_hd.mp4"))
    }

    @Test fun blockedServicesFirst() {
        listOf(
            "https://youtu.be/aqz-KE-bpKQ" to "YouTube",
            "https://www.instagram.com/reel/C9xY2kLs8aB/" to "Instagram",
            "https://vm.tiktok.com/x/video.mp4" to "TikTok",
            "https://x.com/user/status/1" to "X",
            "https://vimeo.com/1084537" to "Vimeo",
            "https://fb.watch/abc" to "Facebook",
            "https://v.redd.it/xyz" to "Reddit",
        ).forEach { (url, svc) -> assertEquals(svc, assertIs<ResolvedLink.Blocked>(r(url)).service) }
    }

    @Test fun clouds() {
        val d = assertIs<ResolvedLink.GoogleDrive>(r("https://drive.google.com/file/d/1Xk7pLwQ0mVbN3sR9aT2uYc4dEf5gH6iJ/view?usp=sharing"))
        assertEquals("1Xk7pLwQ0mVbN3sR9aT2uYc4dEf5gH6iJ", d.fileId)
        val db = assertIs<ResolvedLink.Dropbox>(r("https://www.dropbox.com/scl/fi/a1b2c3/wedding-teaser.mp4?rlkey=x9&dl=0"))
        assertTrue(db.downloadUrl.endsWith("rlkey=x9&dl=1"))
        assertIs<ResolvedLink.OneDrive>(r("https://1drv.ms/v/s!abc"))
    }

    @Test fun openArchives() {
        assertEquals("ElephantsDream", assertIs<ResolvedLink.ArchiveItem>(r("https://archive.org/details/ElephantsDream")).identifier)
        assertEquals("jsc2022m000270_Orion_First_Imagery_Timelapse_and_Original_221117",
            assertIs<ResolvedLink.NasaItem>(r("https://images.nasa.gov/details/jsc2022m000270_Orion_First_Imagery_Timelapse_and_Original_221117")).nasaId)
        val c = assertIs<ResolvedLink.CommonsFile>(r("https://commons.wikimedia.org/wiki/File:Caminandes_3_-_Llamigos_-_Blender_Foundation.webm"))
        assertEquals("webm", c.ext)
        assertTrue(c.directUrl.startsWith("https://upload.wikimedia.org/wikipedia/commons/"))
        assertTrue(c.isDownloadable)
    }

    @Test fun commonsPathUsesMd5OfName() {
        // Приклад з документації MediaWiki: Example.jpg → /a/a9/Example.jpg
        assertEquals("https://upload.wikimedia.org/wikipedia/commons/a/a9/Example.jpg", Commons.directUrl("Example.jpg"))
    }

    @Test fun streamsAndPages() {
        assertEquals("HLS", assertIs<ResolvedLink.Stream>(r("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8")).kind)
        assertEquals("DASH", assertIs<ResolvedLink.Stream>(r("https://cdn.x.com/a/manifest.mpd")).kind)
        assertIs<ResolvedLink.WebPage>(r("https://www.nasa.gov/missions/artemis/"))
        assertIs<ResolvedLink.Invalid>(r("no link here"))
        assertIs<ResolvedLink.Invalid>(r("ftp://x.com/a.mp4"))
    }

    @Test fun titles() {
        assertEquals("Elephants Dream 1080p", LinkResolver.titleFromFileName("elephants_dream-1080p.mp4"))
        assertEquals("Saved video", LinkResolver.titleFromFileName(""))
    }
}
