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

public class AppOpenManager_Admob {

    private static final String TAG = "AppOpenAd_Admob";

    public boolean isShowingAd = false;
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private long loadTime = 0;

    public AppOpenManager_Admob(Context context) {
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

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = new Date().getTime() - loadTime;
        return dateDifference < (numHours * 3600000);
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void showAdIfAvailable(@NonNull final Activity activity) {
        if (!isAdAvailable()) {
            Log.d(TAG, "Ad not available yet, loading new ad...");
            loadAd(activity);
            return;
        }

        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed");
                appOpenAd = null;
                isShowingAd = false;
                loadAd(activity);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                Log.e(TAG, "Ad failed to show: " + adError.getMessage());
                appOpenAd = null;
                isShowingAd = false;
                loadAd(activity);
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
