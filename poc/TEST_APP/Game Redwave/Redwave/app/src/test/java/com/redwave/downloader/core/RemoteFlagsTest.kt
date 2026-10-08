package com.redwave.downloader.core

import com.redwave.downloader.core.config.RemoteFlags
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Remote Config redwave_config: розбір JSON і безпечні значення за замовчуванням. */
class RemoteFlagsTest {

    @Test fun emptyOrBrokenGivesSafeDefault() {
        for (j in listOf(null, "", "   ", "{", "not json", "[]")) {
            val f = RemoteFlags.parse(j)
            assertFalse(f.enabledUrlAd, "реклама вимкнена для: $j")
            assertFalse(f.homeRequired, "Maybe later є для: $j")
            assertTrue(f.isUninstall, "видаляти можна для: $j")
            assertFalse(f.isEnableAdmob, "AdMob вимкнено для: $j")
        }
    }

    @Test fun fullJson() {
        val f = RemoteFlags.parse("""{"enabled_url_ad": true, "url": "https://example.com/p", "home_required": true, "is_uninstall": false}""")
        assertTrue(f.enabledUrlAd); assertTrue(f.homeRequired); assertFalse(f.isUninstall)
        assertEquals("https://example.com/p", f.url)
        assertTrue(f.adActive)
    }

    @Test fun partialJsonKeepsOtherDefaults() {
        val f = RemoteFlags.parse("""{"home_required": true, "extra_field": 1}""")
        assertTrue(f.homeRequired); assertFalse(f.enabledUrlAd); assertTrue(f.isUninstall)
    }

    @Test fun adNeedsHttpsUrl() {
        assertFalse(RemoteFlags.parse("""{"enabled_url_ad": true}""").adActive)
        assertFalse(RemoteFlags.parse("""{"enabled_url_ad": true, "url": "http://insecure.example"}""").adActive)
        assertFalse(RemoteFlags.parse("""{"enabled_url_ad": false, "url": "https://example.com"}""").adActive)
    }

    /** Рівно те, що стоїть у Firebase Console (VELDAN, 08.10.2026). */
    @Test fun consoleJson() {
        val f = RemoteFlags.parse("""{
          "enabled_url_ad": false,
          "url": "https://google.com",
          "home_required": false,
          "is_uninstall": true,
          "is_enable_admob": true
        }""")
        assertFalse(f.enabledUrlAd); assertFalse(f.adActive); assertFalse(f.homeRequired)
        assertTrue(f.isUninstall); assertTrue(f.isEnableAdmob)
        assertEquals("https://google.com", f.url)
    }

    @Test fun oldEnabledKeyIgnored() {
        assertFalse(RemoteFlags.parse("""{"enabled": true, "url": "https://example.com"}""").adActive)
    }

    @Test fun admobFlag() {
        assertTrue(RemoteFlags.parse("""{"is_enable_admob": true}""").isEnableAdmob)
        assertFalse(RemoteFlags.parse("""{"is_enable_admob": false}""").isEnableAdmob)
        assertFalse(RemoteFlags.parse("""{"enabled_url_ad": true, "url": "https://example.com"}""").isEnableAdmob)
    }

    @Test fun privacyUrlFallback() {
        assertEquals(RemoteFlags.DEFAULT_PRIVACY_URL, RemoteFlags.parse("{}").privacy)
        assertEquals("https://x.example/pp", RemoteFlags.parse("""{"privacy_url": "https://x.example/pp"}""").privacy)
    }
}
