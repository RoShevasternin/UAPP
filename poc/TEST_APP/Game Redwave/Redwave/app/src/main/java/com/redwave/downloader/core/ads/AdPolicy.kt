package com.redwave.downloader.core.ads

// ═════════════════════════════════════════════════════════════════════════════
//  AdPolicy — коли можна показати повноекранну рекламу (рішення VELDAN 07.10.2026).
//
//    App Open:
//      • при відкритті апки (холодний старт з іконки, іконка Redwave на нашому лаунчері);
//      • при поверненні в апку, якщо людини не було НЕ довше за RETURN_WINDOW_MS (1 год);
//      • не частіше за раз на APP_OPEN_GAP_MS (3 хв) від БУДЬ-ЯКОЇ повноекранної реклами.
//    Interstitial:
//      • після кожних INTERSTITIAL_EVERY (2) завантажених треків;
//      • не раніше INTERSTITIAL_GAP_MS після попередньої повноекранної реклами — інакше чекає.
//
//  Де саме (лише в застосунку, не на лаунчері й не на онбордингу) — вирішує GDXGame.
//  Чистий Kotlin: час передається параметром, стан — AdState (зберігає Android-бік).
// ═════════════════════════════════════════════════════════════════════════════
data class AdState(
    /** Коли закрили останню повноекранну рекламу (будь-яку), мс epoch; 0 — ще не було. */
    val lastFullscreenAt: Long = 0L,
    /** Скільки треків завантажено після останнього інтерстішала. */
    val downloadsSinceInterstitial: Int = 0,
)

object AdPolicy {

    const val APP_OPEN_GAP_MS     = 3 * 60_000L
    const val RETURN_WINDOW_MS    = 60 * 60_000L
    const val INTERSTITIAL_EVERY  = 2
    const val INTERSTITIAL_GAP_MS = 60_000L

    /** Скільки минуло від останньої реклами. Годинник перевели назад → вважаємо, що давно. */
    private fun sinceLast(s: AdState, now: Long): Long =
        if (s.lastFullscreenAt <= 0L || now < s.lastFullscreenAt) Long.MAX_VALUE else now - s.lastFullscreenAt

    /** App Open при відкритті апки. */
    fun canAppOpen(s: AdState, now: Long): Boolean = sinceLast(s, now) >= APP_OPEN_GAP_MS

    /** App Open при поверненні: не було awayMs мс. Довше за годину — не показуємо. */
    fun canAppOpenOnReturn(s: AdState, now: Long, awayMs: Long): Boolean =
        awayMs in 0..RETURN_WINDOW_MS && canAppOpen(s, now)

    fun interstitialDue(s: AdState): Boolean = s.downloadsSinceInterstitial >= INTERSTITIAL_EVERY

    fun canInterstitial(s: AdState, now: Long): Boolean =
        interstitialDue(s) && sinceLast(s, now) >= INTERSTITIAL_GAP_MS

    fun onTrackDownloaded(s: AdState): AdState = s.copy(downloadsSinceInterstitial = s.downloadsSinceInterstitial + 1)

    /** Рекламу закрили (або вона впала посеред показу — людина її все одно бачила). */
    fun onAppOpenShown(s: AdState, now: Long): AdState = s.copy(lastFullscreenAt = now)

    fun onInterstitialShown(s: AdState, now: Long): AdState = AdState(lastFullscreenAt = now, downloadsSinceInterstitial = 0)
}
