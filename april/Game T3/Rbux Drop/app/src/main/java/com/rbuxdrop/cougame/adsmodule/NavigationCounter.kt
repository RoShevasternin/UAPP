package com.rbuxdrop.cougame.adsmodule

// Відстежує лічильники навігації для Custom Interstitial реклами
//
// Front навігація (перехід вперед):
//   - лічильник зберігається в SharedPreferences
//   - якщо reset_on_app_restart = false → не скидається при перезапуску
//
// Back навігація (кнопка назад):
//   - лічильник тільки в пам'яті
//   - завжди скидається при перезапуску додатку

class NavigationCounter(private val adPref: AdPref) {

    // Back лічильник — тільки в пам'яті (скидається при перезапуску)
    private var _backCount = 0

    // ── Front навігація ───────────────────────────────────────────────────────

    var frontCount: Int
        get()      = adPref.frontNavCount
        set(value) { adPref.frontNavCount = value }

    fun incrementFront() { frontCount++ }
    fun resetFront()     { adPref.resetFrontNav() }

    // ── Back навігація ────────────────────────────────────────────────────────

    val backCount: Int get() = _backCount

    fun incrementBack() { _backCount++ }
    fun resetBack()     { _backCount = 0 }

    // ── При старті додатку ────────────────────────────────────────────────────
    // Викликається один раз в App.onCreate()
    // Скидає front лічильник якщо reset_on_app_restart = true

    fun applyRestartReset() {
        val cfg = AdConfig.customInterstitial() ?: return
        if (cfg.frontNavigation.resetOnAppRestart) {
            resetFront()
        }
        // back лічильник завжди в пам'яті — він і так вже 0
    }
}