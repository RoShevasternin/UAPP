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
            assertFalse(f.enabled, "реклама вимкнена для: $j")
            assertFalse(f.homeRequired, "Maybe later є для: $j")
            assertTrue(f.isUninstall, "видаляти можна для: $j")
        }
    }

    @Test fun fullJson() {
        val f = RemoteFlags.parse("""{"enabled": true, "url": "https://example.com/p", "home_required": true, "is_uninstall": false}""")
        assertTrue(f.enabled); assertTrue(f.homeRequired); assertFalse(f.isUninstall)
        assertEquals("https://example.com/p", f.url)
        assertTrue(f.adActive)
    }

    @Test fun partialJsonKeepsOtherDefaults() {
        val f = RemoteFlags.parse("""{"home_required": true, "extra_field": 1}""")
        assertTrue(f.homeRequired); assertFalse(f.enabled); assertTrue(f.isUninstall)
    }

    @Test fun adNeedsHttpsUrl() {
        assertFalse(RemoteFlags.parse("""{"enabled": true}""").adActive)
        assertFalse(RemoteFlags.parse("""{"enabled": true, "url": "http://insecure.example"}""").adActive)
        assertFalse(RemoteFlags.parse("""{"enabled": false, "url": "https://example.com"}""").adActive)
    }

    @Test fun privacyUrlFallback() {
        assertEquals(RemoteFlags.DEFAULT_PRIVACY_URL, RemoteFlags.parse("{}").privacy)
        assertEquals("https://x.example/pp", RemoteFlags.parse("""{"privacy_url": "https://x.example/pp"}""").privacy)
    }
}
