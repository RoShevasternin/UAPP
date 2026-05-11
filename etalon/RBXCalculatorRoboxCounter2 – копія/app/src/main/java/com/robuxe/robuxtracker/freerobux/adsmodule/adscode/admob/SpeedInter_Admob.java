package com.robuxe.robuxtracker.freerobux.adsmodule.adscode.admob;




import android.app.Activity;
import android.app.Dialog;

import com.robuxe.robuxtracker.freerobux.R;
import com.robuxe.robuxtracker.freerobux.adsmodule.MyApplication;
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdConfig;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
//
//public class SpeedInter_Admob {
//
//    static Activity activity;
//    private static SpeedInter_Admob mInstance;
//    private int clickCount = 0;
//    private int backCount = 0;
//    private int afterXClicksIntAds, afterBac;
//    private InterstitialAd interstitialAd, backinterstitialAd;
//    private MyCallback myCallback;
//    private static Dialog progressDialog;
//    private SpeedInter_Admob(Activity context) {
//        activity = context;
//        afterXClicksIntAds =
//                AdConfig.afterpagesintads;
//        afterBac = AdConfig.afterpagesbackads;
//    }
//
//    public static SpeedInter_Admob getInstance(Activity context) {
//        if (mInstance == null) {
//            mInstance = new SpeedInter_Admob(context);
//        }
//        return mInstance;
//    }
//
//
//    public void loadInterstitialAd() {
//
//        if (activity == null) {
//            return;
//        }
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//        InterstitialAd.load(activity, AdConfig.admob_inter_id, adRequest, new InterstitialAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull InterstitialAd ad) {
//                interstitialAd = ad;
//                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                    @Override
//                    public void onAdDismissedFullScreenContent() {
////                        MyApplication.isAdShowing = false;
//                        hideLoading();
//                        interstitialAd = null;
//                        if (myCallback != null) {
//                            myCallback.onAdCompleted();
//                        }
//                        loadInterstitialAd();
//                    }
//
//                    @Override
//                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
////                        MyApplication.isAdShowing = false;
//                        hideLoading();
//                        interstitialAd = null;
//                        if (myCallback != null) {
//                            myCallback.onAdCompleted();
//                        }
//                        loadInterstitialAd();
//                    }
//
//                    @Override
//                    public void onAdShowedFullScreenContent() {
////                        MyApplication.isAdShowing = true;
//                    }
//                });
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
//                interstitialAd = null;
//                Log.e("TAGAdmob", "" + adError.getMessage());
//                loadInterstitialAd();
//
//            }
//        });
//    }
//
//    public void showInterstitialAd(MyCallback callback) {
//        this.myCallback = callback;
//
//        if (activity == null) {
//            callback.onAdCompleted();
//            return;
//        }
//
//        clickCount++;
//        if (afterXClicksIntAds < 1) {
//            afterXClicksIntAds = 1;
//        }
//        if (afterXClicksIntAds == 1 || (clickCount % afterXClicksIntAds) == 1) {
//            if (interstitialAd != null) {
//
//                showLoading(activity);
//                interstitialAd.show(activity);
//
//            } else {
//                loadInterstitialAd();
//                // ❌ DO NOT load here
//                // ✅ fallback immediately
//                Ad_Image.getInstance(activity).startSpecialActivity(activity, callback, false);
//            }
//
//        } else {
//            loadInterstitialAd();
//            callback.onAdCompleted();
//        }
//    }
//
//    public void loadBackAd() {
//
//        if (activity == null) {
//            return;
//        }
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//        InterstitialAd.load(activity, AdConfig.admob_inter_id, adRequest, new InterstitialAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull InterstitialAd ad) {
//                backinterstitialAd = ad;
//                backinterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                    @Override
//                    public void onAdDismissedFullScreenContent() {
////                        MyApplication.isAdShowing = false;
//                        hideLoading();
//                        backinterstitialAd = null;
//                        if (myCallback != null) {
//                            myCallback.onAdCompleted();
//                        }
//                        loadBackAd();
//                    }
//
//                    @Override
//                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
////                        MyApplication.isAdShowing = false;
//                        hideLoading();
//                        backinterstitialAd = null;
//                        if (myCallback != null) {
//                            myCallback.onAdCompleted();
//                        }
//                        loadBackAd();
//                    }
//
//                    @Override
//                    public void onAdShowedFullScreenContent() {
////                        MyApplication.isAdShowing = true;
//                    }
//                });
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
//                backinterstitialAd = null;
//                Log.e("TAGAdmob", "" + adError.getMessage());
//                loadBackAd();
//            }
//        });
//    }
//
//    public void showBackAd(MyCallback callback) {
//        this.myCallback = callback;
//
//        if (activity == null) {
//            callback.onAdCompleted();
//            return;
//        }
//        backCount++;
//        if (afterBac < 1) {
//            afterBac = 1;
//        }
//        if (afterBac == 1 || (backCount % afterBac) == 1) {
//            if (backinterstitialAd != null) {
//                showLoading(activity);
//                backinterstitialAd.show(activity);
//
//            } else {
//                loadBackAd();
//                // ❌ DO NOT load here
//                // ✅ fallback immediately
//                Ad_Image.getInstance(activity).startBackActivity(activity, callback, true);
//            }
//
//        } else {
//            loadBackAd();
//            callback.onAdCompleted();
//        }
//    }
//
//
//    public static void showLoading(Activity activity) {
//        if (activity == null || activity.isFinishing()) return;
//
//        activity.runOnUiThread(() -> {
//
//            // ✅ ALWAYS DISMISS OLD FIRST
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//
//            progressDialog = new Dialog(activity);
//            progressDialog.setContentView(R.layout.a_custom_progress_layout); // your layout
//            progressDialog.setCancelable(false);
//            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//            progressDialog.show();
//        });
//    }
//
//
//    public static void hideLoading() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//            progressDialog = null;
//        }
//    }
//
//}

/* loaded from: classes2.dex */
public class SpeedInter_Admob {
    private static SpeedInter_Admob mInstance;
    private Activity activity;
    private int clickCount = 0;
    private int backClickCount = 0;

    private SpeedInter_Admob(Activity activity) {
        this.activity = activity;
    }

    public static SpeedInter_Admob getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new SpeedInter_Admob(activity);
        } else {
            mInstance.activity = activity;
        }
        return mInstance;
    }

    public void showInterstitialAd(final MyCallback callback) {
        int interAdsCount;
        if (this.activity == null) {
            callback.onAdCompleted();
            return;
        }
        this.clickCount++;
        try {
            interAdsCount = AdConfig.afterpagesintads;
        } catch (Exception e) {
            interAdsCount = 2;
        }
        if (interAdsCount == 0) {
            callback.onAdCompleted();
            return;
        }
        if (this.clickCount % interAdsCount != 0) {
            callback.onAdCompleted();
            return;
        }
        final Dialog dialog = new Dialog(this.activity);
        dialog.setContentView(R.layout.a_custom_progress_layout);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(-2, -2);
            dialog.getWindow().setGravity(17);
        }
        dialog.show();
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this.activity, AdConfig.admob_inter_id, adRequest, new InterstitialAdLoadCallback() { // from class: com.ads.demo.adsmodule.adscode.admob.SpeedInter_Admob.1
            @Override // com.google.android.gms.ads.AdLoadCallback
            public void onAdLoaded(InterstitialAd ad) {
                if (SpeedInter_Admob.this.activity == null || SpeedInter_Admob.this.activity.isFinishing()) {
                    dialog.dismiss();
                    callback.onAdCompleted();
                    return;
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog.dismiss();
                ad.setFullScreenContentCallback(new FullScreenContentCallback() { // from class: com.ads.demo.adsmodule.adscode.admob.SpeedInter_Admob.1.1
                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdDismissedFullScreenContent() {
                        callback.onAdCompleted();
                    }

                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        callback.onAdCompleted();
                    }
                });
                ad.show(SpeedInter_Admob.this.activity);
            }

            @Override // com.google.android.gms.ads.AdLoadCallback
            public void onAdFailedToLoad(LoadAdError error) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog.dismiss();
                callback.onAdCompleted();
            }
        });
    }

    public void showBackAd(final MyCallback callback) {
        int backAdsCount;
        if (this.activity == null) {
            callback.onAdCompleted();
            return;
        }
        this.backClickCount++;
        try {
            backAdsCount = MyApplication.ad_preferences.getRemoteConfig().getBack_count();
        } catch (Exception e) {
            backAdsCount = 2;
        }
        if (backAdsCount == 0) {
            callback.onAdCompleted();
            return;
        }
        if (this.backClickCount % backAdsCount != 0) {
            callback.onAdCompleted();
            return;
        }
        final Dialog dialog = new Dialog(this.activity);
        dialog.setContentView(R.layout.a_custom_progress_layout);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(-2, -2);
            dialog.getWindow().setGravity(17);
        }
        dialog.show();
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this.activity, AdConfig.admob_inter_id, adRequest, new InterstitialAdLoadCallback() { // from class: com.ads.demo.adsmodule.adscode.admob.SpeedInter_Admob.2
            @Override // com.google.android.gms.ads.AdLoadCallback
            public void onAdLoaded(InterstitialAd ad) {
                if (SpeedInter_Admob.this.activity == null || SpeedInter_Admob.this.activity.isFinishing()) {
                    dialog.dismiss();
                    callback.onAdCompleted();
                    return;
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog.dismiss();
                ad.setFullScreenContentCallback(new FullScreenContentCallback() { // from class: com.ads.demo.adsmodule.adscode.admob.SpeedInter_Admob.2.1
                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdDismissedFullScreenContent() {
                        callback.onAdCompleted();
                    }

                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        callback.onAdCompleted();
                    }
                });
                ad.show(SpeedInter_Admob.this.activity);
            }

            @Override // com.google.android.gms.ads.AdLoadCallback
            public void onAdFailedToLoad(LoadAdError error) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog.dismiss();
                callback.onAdCompleted();
            }
        });
    }

//    public static void startActivityWithAd(Activity activity, Intent intent) {
//        getInstance(activity).showInterstitialAd(new SpeedInter_Admob$$ExternalSyntheticLambda0(activity, intent));
//    }
}