package com.redwave.downloader.core

import com.redwave.downloader.core.ads.AdPolicy
import com.redwave.downloader.core.ads.AdState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Частота повноекранної реклами: App Open раз на 3 хв, повернення в межах години, інтерстішал на кожні 2 треки. */
class AdPolicyTest {

    private val min = 60_000L
    private val t0 = 1_800_000_000_000L

    @Test fun firstAppOpenAllowed() {
        assertTrue(AdPolicy.canAppOpen(AdState(), t0))
    }

    @Test fun appOpenNotMoreThanOncePer3Minutes() {
        val s = AdPolicy.onAppOpenShown(AdState(), t0)
        assertFalse(AdPolicy.canAppOpen(s, t0 + 2 * min))
        assertFalse(AdPolicy.canAppOpen(s, t0 + 3 * min - 1))
        assertTrue(AdPolicy.canAppOpen(s, t0 + 3 * min))
    }

    @Test fun interstitialAlsoBlocksAppOpenFor3Minutes() {
        val s = AdPolicy.onInterstitialShown(AdState(downloadsSinceInterstitial = 2), t0)
        assertFalse(AdPolicy.canAppOpen(s, t0 + min))
        assertTrue(AdPolicy.canAppOpen(s, t0 + 3 * min))
    }

    @Test fun returnOnlyWithinAnHour() {
        val s = AdState()
        assertTrue(AdPolicy.canAppOpenOnReturn(s, t0, 5_000L))
        assertTrue(AdPolicy.canAppOpenOnReturn(s, t0, 60 * min))
        assertFalse(AdPolicy.canAppOpenOnReturn(s, t0, 60 * min + 1))
        assertFalse(AdPolicy.canAppOpenOnReturn(s, t0, -1L))
    }

    @Test fun returnRespectsGap() {
        val s = AdPolicy.onAppOpenShown(AdState(), t0)
        assertFalse(AdPolicy.canAppOpenOnReturn(s, t0 + min, 30_000L))
        assertTrue(AdPolicy.canAppOpenOnReturn(s, t0 + 4 * min, 30_000L))
    }

    @Test fun interstitialEveryTwoTracks() {
        var s = AdState()
        assertFalse(AdPolicy.canInterstitial(s, t0))
        s = AdPolicy.onTrackDownloaded(s)
        assertFalse(AdPolicy.canInterstitial(s, t0))
        s = AdPolicy.onTrackDownloaded(s)
        assertTrue(AdPolicy.canInterstitial(s, t0))
        s = AdPolicy.onInterstitialShown(s, t0)
        assertEquals(0, s.downloadsSinceInterstitial)
        s = AdPolicy.onTrackDownloaded(AdPolicy.onTrackDownloaded(s))
        assertFalse(AdPolicy.canInterstitial(s, t0 + 30_000L), "щойно була реклама — чекає")
        assertTrue(AdPolicy.canInterstitial(s, t0 + min))
    }

    @Test fun clockBackwardsDoesNotBlockForever() {
        val s = AdPolicy.onAppOpenShown(AdState(), t0)
        assertTrue(AdPolicy.canAppOpen(s, t0 - 10 * min))
    }
}
