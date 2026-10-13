package com.vidtools.framefall.core

import com.vidtools.framefall.core.config.AppFlags
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Режим Framefall з картки сервера: app_open = custom* → наша реклама, інше → безпечний DEFAULT. */
class AppFlagsTest {

    @Test fun customProvidersGiveOurMode() {
        for (p in listOf("custom", "custom_google", "custom_tiktok", "custom_facebook")) {
            val f = AppFlags.from(p, online = true, adMode = false)
            assertTrue(f.homeTabs, p); assertTrue(f.homeRequired, p); assertFalse(f.isUninstall, p)
        }
    }

    @Test fun naAdmobOrNoConfigGiveDefault() {
        for (p in listOf("na", "admob", null, "", "unknown")) {
            assertEquals(AppFlags.DEFAULT, AppFlags.from(p, online = true, adMode = false), "$p")
        }
        assertFalse(AppFlags.DEFAULT.homeTabs); assertFalse(AppFlags.DEFAULT.homeRequired); assertTrue(AppFlags.DEFAULT.isUninstall)
    }

    @Test fun offlineIsAlwaysDefault() {
        assertEquals(AppFlags.DEFAULT, AppFlags.from("custom", online = false, adMode = false))
    }

    @Test fun adModeForcesOurMode() {
        val f = AppFlags.from("na", online = false, adMode = true)
        assertTrue(f.homeTabs); assertTrue(f.homeRequired); assertFalse(f.isUninstall)
    }

    @Test fun homeTabUsesAppOpenPlacement() {
        assertEquals("app_open", AppFlags.PL_HOME_TAB)
    }
}
