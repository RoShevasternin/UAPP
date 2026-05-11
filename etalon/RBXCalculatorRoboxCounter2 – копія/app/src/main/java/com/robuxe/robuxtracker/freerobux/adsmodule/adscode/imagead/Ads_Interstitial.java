package com.robuxe.robuxtracker.freerobux.adsmodule.adscode.imagead;



import static com.robuxe.robuxtracker.freerobux.adsmodule.adscode.imagead.Ad_Image.getRandomRedirectLink;

import android.app.Activity;

import com.robuxe.robuxtracker.freerobux.adsmodule.MyApplication;
import com.robuxe.robuxtracker.freerobux.adsmodule.model.RemoteConfigModel;


public class Ads_Interstitial {
    public static OnFinishAds onFinishAds;
    public static RemoteConfigModel remoteConfigModel = MyApplication.ad_preferences.getRemoteConfig();

    public static void showAds_full(final Activity context, OnFinishAds onFinishAd, boolean... doShowAds) {
        onFinishAds = onFinishAd;
        if (!remoteConfigModel.getIs_ad_show()) {
            onFinishAds.onFinishAds(false);
            return;
        }
        showCustomInterstitial(context);
    }

    public static void showCustomInterstitial(final Activity source_class) {
        onFinishAds.onFinishAds(false);
        CustomTabLinkOpen.openLink(source_class, getRandomRedirectLink(MyApplication.ad_preferences.getRemoteConfig().getCustomLinks().getInterRedirectLink()));

    }

    public interface OnFinishAds {
        void onFinishAds(boolean z);
    }

}