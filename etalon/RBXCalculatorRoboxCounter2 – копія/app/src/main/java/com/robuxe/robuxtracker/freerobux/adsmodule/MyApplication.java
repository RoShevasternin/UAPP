package com.robuxe.robuxtracker.freerobux.adsmodule;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.ADPref;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdConfig;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.admob.AppOpenManager_Admob;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;
import java.util.List;

public class MyApplication extends Application implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    public static ADPref ad_preferences;
    public static boolean isAdShowing = false;
    public AppOpenManager_Admob admob_open;
    private Activity currentActivity;
    private boolean isInBackground;

    public static boolean isNetworkAvailable(Activity context) {
        if (context == null) return false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isConnect(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ad_preferences = ADPref.INSTANCE.getInstance(this);
        registerActivityLifecycleCallbacks(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_START) {
                onEnterForeground();
            } else if (event == Lifecycle.Event.ON_STOP) {
                onEnterBackground();
            }
        });

        List<String> testDeviceIds = Arrays.asList("DC9D89C4136E2884F2744F6798E55D66", "C67582940C4A9E90FC842B5ABB1AD660");
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build();
        MobileAds.setRequestConfiguration(configuration);

        MobileAds.initialize(this, initializationStatus -> {
            try {
                if (currentActivity != null) {
//                    SpeedInter_Admob.getInstance(currentActivity).loadInterstitialAd();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        initAppOpenAdManager();



    }

    public void initAppOpenAdManager() {
        if (admob_open == null) {
            admob_open = new AppOpenManager_Admob(this);
        }
    }

    private void onEnterForeground() {
        if (isInBackground &&
                currentActivity != null &&
                isNetworkAvailable(currentActivity) &&
                isConnect(currentActivity) &&
                !isAdShowing &&
                AdConfig.cat_appopen != null) {

            if ("admob".equals(AdConfig.cat_appopen)) {
                if (admob_open != null && !admob_open.isShowingAd) {
                    admob_open.showAdIfAvailable(currentActivity);
                }
            }
        }
        isInBackground = false;
    }

    private void onEnterBackground() {
        isInBackground = true;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (this.currentActivity == activity) {
            this.currentActivity = null;
        }
    }
}
