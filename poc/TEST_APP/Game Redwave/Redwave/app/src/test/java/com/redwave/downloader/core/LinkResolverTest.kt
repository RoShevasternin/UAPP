package com.redwave.downloader.core

import com.redwave.downloader.core.link.GoogleDrive
import com.redwave.downloader.core.link.LinkResolver
import com.redwave.downloader.core.link.ResolvedLink
import com.redwave.downloader.core.link.chipLabel
import com.redwave.downloader.core.link.isDownloadable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/** Ті самі кейси, що в прототипі (панель «Буфер обміну») + краї. */
class LinkResolverTest {

    private fun r(s: String?) = LinkResolver.resolve(s)

    @Test fun directMp3() {
        val x = assertIs<ResolvedLink.DirectAudio>(r("https://cdn.lofi-loops.net/free/night-drive.mp3"))
        assertEquals("mp3", x.ext); assertEquals("night-drive.mp3", x.fileName); assertEquals("MP3", x.chipLabel)
    }

    @Test fun allAudioExtensions() {
        for (e in listOf("mp3", "m4a", "aac", "ogg", "opus", "flac", "wav", "MP3", "Flac")) {
            assertIs<ResolvedLink.DirectAudio>(r("https://x.com/a/track.$e"), e)
        }
    }

    @Test fun linkInsideText_trailingPunctuation_andWww() {
        val x = assertIs<ResolvedLink.DirectAudio>(r("check this www.example.com/song.flac!"))
        assertEquals("https://www.example.com/song.flac", x.url)
        assertEquals("example.com", x.host)
        assertIs<ResolvedLink.DirectAudio>(r("(https://a.io/x.mp3)."))
    }

    @Test fun googleDrive() {
        val x = assertIs<ResolvedLink.GoogleDrive>(r("https://drive.google.com/file/d/1A2b3C4d5E6f7G8hRehearsal/view?usp=sharing"))
        assertEquals("1A2b3C4d5E6f7G8hRehearsal", x.fileId)
        assertEquals("https://drive.google.com/uc?export=download&id=1A2b3C4d5E6f7G8hRehearsal", x.downloadUrl)
        assertEquals("DRIVE", x.chipLabel)
        assertTrue(GoogleDrive.confirmedUrl(x.fileId).contains("confirm=t"))
        assertIs<ResolvedLink.GoogleDrive>(r("https://drive.google.com/open?id=abc_DEF-123"))
    }

    @Test fun dropboxSetsDl1_keepsOtherParams() {
        val x = assertIs<ResolvedLink.Dropbox>(r("https://www.dropbox.com/s/k2x9q7/band-demo-v3.wav?dl=0"))
        assertEquals("https://www.dropbox.com/s/k2x9q7/band-demo-v3.wav?dl=1", x.downloadUrl)
        assertEquals("band-demo-v3.wav", x.fileName)
        val y = assertIs<ResolvedLink.Dropbox>(r("https://www.dropbox.com/scl/fi/abc/demo.mp3?rlkey=xyz&st=1&dl=0"))
        assertEquals("https://www.dropbox.com/scl/fi/abc/demo.mp3?rlkey=xyz&st=1&dl=1", y.downloadUrl)
        val z = assertIs<ResolvedLink.Dropbox>(r("https://dropbox.com/s/q/a.mp3"))
        assertTrue(z.downloadUrl.endsWith("?dl=1"))
    }

    @Test fun oneDrive() { assertIs<ResolvedLink.OneDrive>(r("https://1drv.ms/u/s!abc")) }

    @Test fun blockedServices_evenWithMp3InPath() {
        val cases = mapOf(
            "https://youtu.be/q9Xb3rT_lofi" to "YouTube",
            "https://www.youtube.com/watch?v=1" to "YouTube",
            "https://music.youtube.com/watch?v=1" to "YouTube",
            "https://m.youtube.com/watch?v=1" to "YouTube",
            "https://open.spotify.com/track/4uLU6hMCjMI75M1A2tKUQC" to "Spotify",
            "https://soundcloud.com/a/b" to "SoundCloud",
            "https://music.apple.com/us/album/x" to "Apple Music",
            "https://www.deezer.com/track/1" to "Deezer",
            "https://vk.com/audio1" to "VK",
            "https://www.tiktok.com/@a/video/1" to "TikTok",
            "https://music.yandex.ru/album/1" to "Yandex Music",
            "https://youtube.com/fake.mp3" to "YouTube",
        )
        for ((url, service) in cases) {
            val x = assertIs<ResolvedLink.Blocked>(r(url), url)
            assertEquals(service, x.service, url)
            assertFalse(x.isDownloadable)
        }
    }

    @Test fun podcastFeeds() {
        assertIs<ResolvedLink.PodcastFeed>(r("https://feeds.indiehour.fm/indie-hour.rss"))
        assertIs<ResolvedLink.PodcastFeed>(r("https://example.com/podcast/feed/"))
        assertIs<ResolvedLink.PodcastFeed>(r("https://feed.example.com/show"))
        assertIs<ResolvedLink.PodcastFeed>(r("https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/indie-hour.rss"))
    }

    @Test fun webPageAndInvalid() {
        assertIs<ResolvedLink.WebPage>(r("https://blog.example.com/best-lofi-2026"))
        assertIs<ResolvedLink.WebPage>(r("https://github.com/RoShevasternin/UAPP"))
        assertEquals(ResolvedLink.Invalid, r("hello"))
        assertEquals(ResolvedLink.Invalid, r(""))
        assertEquals(ResolvedLink.Invalid, r(null))
        assertEquals(ResolvedLink.Invalid, r("ftp://x.com/a.mp3"))
    }

    @Test fun spacesInUrlDoNotCrash() {
        // URL у тексті закінчується на пробілі — далі вже не URL
        assertIs<ResolvedLink.DirectAudio>(r("https://x.com/my%20song.mp3 is great"))
    }

    @Test fun titleFromFileName() {
        assertEquals("Night Drive Remix", LinkResolver.titleFromFileName("night-drive-remix.mp3"))
        assertEquals("Untitled Demo Track", LinkResolver.titleFromFileName("untitled_demo-track.mp3"))
        assertEquals("Shared audio", LinkResolver.titleFromFileName(""))
        assertEquals("Shared audio", LinkResolver.titleFromFileName(null))
    }
}
