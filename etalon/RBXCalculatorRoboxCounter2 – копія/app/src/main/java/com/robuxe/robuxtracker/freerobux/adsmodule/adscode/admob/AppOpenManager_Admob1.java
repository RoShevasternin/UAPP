package com.robuxe.robuxtracker.freerobux.adsmodule.adscode.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdConfig;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class AppOpenManager_Admob1 {

    private static final String TAG = "AppOpenAd_Admob1";

    public boolean isShowingAd = false;
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private long loadTime = 0;

    public AppOpenManager_Admob1(Context context) {
        Log.d(TAG, "AppOpenManager initialized");
        loadAd(context);
    }

    public void loadAd(Context context) {
        if (isAdAvailable()) return;

        AppOpenAd.load(context,

                AdConfig.admob_appopen_id,
                new AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        loadTime = new Date().getTime();
                        Log.d("AppOpenAd", "App Open Ad Loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        Log.e("AppOpenAd", "Failed to load App Open Ad: " + error.getMessage());
                    }
                });
    }

    public void loadAdWithCallback(Context context, Runnable onAdLoadedOrFailed) {
        if (isAdAvailable()) {
            onAdLoadedOrFailed.run();
            return;
        }

        AppOpenAd.load(context,
                AdConfig.admob_appopen_id,
                new AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        loadTime = new Date().getTime();
                        Log.d(TAG, "App Open Ad Loaded via callback");
                        onAdLoadedOrFailed.run();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        Log.e(TAG, "Failed to load App Open Ad via callback: " + error.getMessage());
                        onAdLoadedOrFailed.run();
                    }
                });
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = new Date().getTime() - loadTime;
        return dateDifference < (numHours * 3600000);
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void showAdIfAvailable(@NonNull final Activity activity, @NonNull final Runnable onAdFinished) {
        if (isAdAvailable()) {
            showAd(activity, onAdFinished);
        } else {
            Log.d(TAG, "Ad not available yet, loading new ad...");
            AppOpenAd.load(activity,
                    AdConfig.admob_appopen_id,
                    new AdRequest.Builder().build(),
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd ad) {
                            appOpenAd = ad;
                            loadTime = new Date().getTime();
                            Log.d(TAG, "App Open Ad Loaded");
                            showAd(activity, onAdFinished);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError error) {
                            Log.e(TAG, "Failed to load App Open Ad: " + error.getMessage());
                            onAdFinished.run();
                        }
                    });
        }
    }


    private void showAd(Activity activity, Runnable onAdFinished) {
        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed");
                appOpenAd = null;
                isShowingAd = false;
                loadAd(activity);
                onAdFinished.run();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                Log.e(TAG, "Ad failed to show: " + adError.getMessage());
                appOpenAd = null;
                isShowingAd = false;
                loadAd(activity);
                onAdFinished.run();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed successfully");
                isShowingAd = true;
            }
        });

        Log.d(TAG, "Showing App Open Ad...");
        appOpenAd.show(activity);
    }

}
