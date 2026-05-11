package com.rsbuxs.rcounbux.adsmodule

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdView
import com.rsbuxs.rcounbux.App
import com.rsbuxs.rcounbux.R

// Головний клас для показу реклами
//
// Створюється один раз в MainActivity або GdxGame:
//   val adManager = AdManager(this)
//
// Використання з LibGDX:
//   game.activity.adManager.showBanner(container)
//   game.activity.adManager.onFrontNavigation()
//   game.activity.adManager.onBackNavigation { finish() }

class AdManager(private val activity: Activity) {

    private var admobInterstitial: InterstitialAd? = null
    private val counter get() = App.navigationCounter

    // Debounce — не показуємо дві реклами підряд швидше ніж 500мс
    private var lastAdShownMs: Long = 0
    private fun isDebouncing() = System.currentTimeMillis() - lastAdShownMs < 500L
    private fun markAdShown()  { lastAdShownMs = System.currentTimeMillis() }

    init {
        preloadAdmobInterstitial()
    }

    // ── Banner ────────────────────────────────────────────────────────────────

    fun showBanner(container: FrameLayout) {
        activity.runOnUiThread {
            when (AdConfig.getProvider(AdType.BANNER)) {
                AdProvider.ADMOB   -> showAdmobBanner(container)
                AdProvider.CUSTOM  -> showCustomBanner(container)
                AdProvider.NA      -> container.visibility = View.GONE
            }
        }
    }

    private fun showAdmobBanner(container: FrameLayout) {
        val unitId = AdConfig.admobBannerId().takeIf { it.isNotEmpty() }
            ?: run { container.visibility = View.GONE; return }

        container.visibility = View.VISIBLE
        container.removeAllViews()

        val adView = AdView(activity).apply {
            adUnitId = unitId
            setAdSize(AdSize.BANNER)
        }
        container.addView(adView)
        adView.loadAd(AdRequest.Builder().build())
    }

    private fun showCustomBanner(container: FrameLayout) {
        val images = AdConfig.customBannerImages()
        if (images.isEmpty()) { container.visibility = View.GONE; return }

        val item = images.random()

        container.visibility = View.VISIBLE
        container.removeAllViews()

        val imageView = ImageView(activity).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                (50 * activity.resources.displayMetrics.density).toInt()
            )
            setOnClickListener { BrowserUtil.open(activity, item.targetUrl) }
        }
        Glide.with(activity.applicationContext).load(item.url).centerCrop().into(imageView)
        container.addView(imageView)
    }

    // ── Native ────────────────────────────────────────────────────────────────

    fun showNative(container: FrameLayout) {
        activity.runOnUiThread {
            when (AdConfig.getProvider(AdType.NATIVE)) {
                AdProvider.ADMOB   -> showAdmobNative(container)
                AdProvider.CUSTOM  -> showCustomNative(container)
                AdProvider.NA      -> container.visibility = View.GONE
            }
        }
    }

    private fun showAdmobNative(container: FrameLayout) {
        val unitId = AdConfig.admobNativeId().takeIf { it.isNotEmpty() }
            ?: run { container.visibility = View.GONE; return }

        container.visibility = View.VISIBLE
        container.removeAllViews()

        val adLoader = com.google.android.gms.ads.AdLoader.Builder(activity, unitId)
            .forNativeAd { nativeAd ->
                val adView = LayoutInflater.from(activity).inflate(R.layout.a_ad_unified_big, container, false) as NativeAdView
                populateAdmobNativeView(nativeAd, adView)
                container.removeAllViews()
                container.addView(adView)
            }
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateAdmobNativeView(
        nativeAd: com.google.android.gms.ads.nativead.NativeAd,
        adView: NativeAdView
    ) {
        adView.headlineView     = adView.findViewById(R.id.ad_headline)
        adView.bodyView         = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView         = adView.findViewById(R.id.ad_app_icon)
        adView.mediaView        = adView.findViewById(R.id.ad_media)

        (adView.headlineView     as? android.widget.TextView)?.text = nativeAd.headline
        (adView.bodyView         as? android.widget.TextView)?.text = nativeAd.body
        (adView.callToActionView as? android.widget.Button)?.text   = nativeAd.callToAction
        nativeAd.icon?.let { (adView.iconView as? ImageView)?.setImageDrawable(it.drawable) }
        adView.setNativeAd(nativeAd)
    }

    private fun showCustomNative(container: FrameLayout) {
        val assets = AdConfig.customNativeAssets()
        if (assets.isEmpty()) { container.visibility = View.GONE; return }

        val item = assets.random()

        container.visibility = View.VISIBLE
        container.removeAllViews()

        val view = LayoutInflater.from(activity).inflate(R.layout.a_custom_native_big, container, false)

        view.findViewById<android.widget.TextView>(R.id.tv_appname)?.text  = item.headline
        view.findViewById<android.widget.TextView>(R.id.tv_desc)?.text     = item.description
        view.findViewById<android.widget.TextView>(R.id.btn_install)?.text = item.cta

        Glide.with(activity.applicationContext)
            .load(item.image)
            .into(view.findViewById(R.id.img_banner))

        Glide.with(activity.applicationContext)
            .load(item.icon)
            .circleCrop()
            .into(view.findViewById(R.id.img_icon))

        view.setOnClickListener { BrowserUtil.open(activity, item.targetUrl) }
        view.findViewById<View>(R.id.btn_install)?.setOnClickListener {
            BrowserUtil.open(activity, item.targetUrl)
        }

        container.addView(view)
    }

    // ── Interstitial ──────────────────────────────────────────────────────────

    fun onFrontNavigation(onComplete: () -> Unit = {}) {
        if (isDebouncing()) { onComplete(); return }

        when (AdConfig.getProvider(AdType.INTERSTITIAL)) {
            AdProvider.ADMOB   -> showAdmobInterstitial(onComplete)
            AdProvider.CUSTOM  -> handleCustomFrontNav(onComplete)
            AdProvider.NA      -> onComplete()
        }
    }

    fun onBackNavigation(onComplete: () -> Unit = {}) {
        if (isDebouncing()) { onComplete(); return }

        when (AdConfig.getProvider(AdType.INTERSTITIAL)) {
            AdProvider.ADMOB   -> showAdmobInterstitial(onComplete)
            AdProvider.CUSTOM  -> handleCustomBackNav(onComplete)
            AdProvider.NA      -> onComplete()
        }
    }

    // ── Custom Interstitial логіка ────────────────────────────────────────────

    private fun handleCustomFrontNav(onComplete: () -> Unit) {
        val cfg = AdConfig.customInterstitial()
        if (cfg == null || !cfg.frontNavigation.enabled) { onComplete(); return }

        counter.incrementFront()

        if (counter.frontCount >= cfg.frontNavigation.frequency) {
            counter.resetFront()
            markAdShown()
            AdConfig.isFullscreenAdShowing = true
            BrowserUtil.open(activity, cfg.targetUrl)
        }
        onComplete()
    }

    private fun handleCustomBackNav(onComplete: () -> Unit) {
        val cfg = AdConfig.customInterstitial()
        if (cfg == null || !cfg.backNavigation.enabled) { onComplete(); return }

        counter.incrementBack()

        if (counter.backCount >= cfg.backNavigation.frequency) {
            counter.resetBack()
            markAdShown()
            AdConfig.isFullscreenAdShowing = true
            BrowserUtil.open(activity, cfg.targetUrl)
        }
        onComplete()
    }

    // ── AdMob Interstitial ────────────────────────────────────────────────────

    private fun preloadAdmobInterstitial() {
        if (AdConfig.getProvider(AdType.INTERSTITIAL) != AdProvider.ADMOB) return
        val unitId = AdConfig.admobInterstitialId().takeIf { it.isNotEmpty() } ?: return

        InterstitialAd.load(
            activity,
            unitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd)       { admobInterstitial = ad }
                override fun onAdFailedToLoad(e: LoadAdError)     { admobInterstitial = null }
            }
        )
    }

    private fun showAdmobInterstitial(onComplete: () -> Unit) {
        val ad = admobInterstitial
        if (ad == null) { onComplete(); preloadAdmobInterstitial(); return }

        AdConfig.isFullscreenAdShowing = true

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                AdConfig.isFullscreenAdShowing = false

                admobInterstitial = null
                markAdShown()
                onComplete()
                preloadAdmobInterstitial()
            }
            override fun onAdFailedToShowFullScreenContent(
                error: com.google.android.gms.ads.AdError
            ) {
                AdConfig.isFullscreenAdShowing = false
                onComplete()
            }
        }
        ad.show(activity)
    }
}