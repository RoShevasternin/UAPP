package com.driftglass.home.core

import com.driftglass.home.core.config.AppFlags
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppFlagsTest {

    @Test fun default_noAds_roleOptional_uninstallAllowed() {
        val f = AppFlags.DEFAULT
        assertFalse(f.adActive); assertFalse(f.homeRequired); assertTrue(f.isUninstall); assertFalse(f.isAdMode)
    }

    @Test fun broken_or_empty_json_is_default() {
        assertEquals(AppFlags.DEFAULT, AppFlags.parse(null))
        assertEquals(AppFlags.DEFAULT, AppFlags.parse(""))
        assertEquals(AppFlags.DEFAULT, AppFlags.parse("{oops"))
    }

    @Test fun adMode_preset() {
        val f = AppFlags.AD_MODE
        assertTrue(f.isAdMode); assertTrue(f.adActive); assertTrue(f.homeRequired); assertFalse(f.isUninstall)
    }

    @Test fun roundTrip_and_redwave_field_names() {
        assertEquals(AppFlags.AD_MODE, AppFlags.parse(AppFlags.encode(AppFlags.AD_MODE)))
        val f = AppFlags.parse("""{"enabled_url_ad": true, "url": "https://example.com", "home_required": false, "is_uninstall": true, "is_enable_admob": true}""")
        assertTrue(f.adActive); assertFalse(f.homeRequired); assertFalse(f.isAdMode)
    }

    @Test fun url_must_be_https() {
        assertFalse(AppFlags(enabledUrlAd = true, url = "http://example.com").adActive)
        assertFalse(AppFlags(enabledUrlAd = true, url = "").adActive)
    }
}
