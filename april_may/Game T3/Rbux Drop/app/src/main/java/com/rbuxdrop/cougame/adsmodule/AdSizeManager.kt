package com.rbuxdrop.cougame.adsmodule

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdSizeManager {

    // ── Pixel values ──────────────────────────────────────────────────────────

    var bannerHeightPx: Int = 0
        set(value) { field = value; update() }

    var nativeHeightPx: Int = 0
        set(value) { field = value; update() }

    val adBottomHeightPx get() = bannerHeightPx + nativeHeightPx

    // ── Flows (px) ────────────────────────────────────────────────────────────

    private val _bannerFlow      = MutableStateFlow(0)
    private val _nativeFlow      = MutableStateFlow(0)
    private val _adBottomFlow    = MutableStateFlow(0)

    val bannerFlow   : StateFlow<Int> = _bannerFlow.asStateFlow()
    val nativeFlow   : StateFlow<Int> = _nativeFlow.asStateFlow()
    val adBottomFlow : StateFlow<Int> = _adBottomFlow.asStateFlow()

    private fun update() {
        _bannerFlow.value   = bannerHeightPx
        _nativeFlow.value   = nativeHeightPx
        _adBottomFlow.value = adBottomHeightPx
    }
}