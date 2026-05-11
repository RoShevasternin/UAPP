package com.robuxe.robuxtracker.freerobux.adsmodule.adscode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.FrameLayout;

import com.robuxe.robuxtracker.freerobux.adsmodule.MyApplication;
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.admob.Ad_Admob;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.admob.SpeedInter_Admob;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.imagead.Ad_Image;
import com.robuxe.robuxtracker.freerobux.adsmodule.model.RemoteConfigModel;


@SuppressLint("WrongConstant")
public class AdUtil {
    public static RemoteConfigModel remoteConfigModel = MyApplication.ad_preferences.getRemoteConfig();
    private static AdUtil mInstance;
    Activity activity;

    public AdUtil(Activity context) {
        this.activity = context;

    }

    public static boolean isNetworkAvailable(Activity context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                throw new NullPointerException("ConnectivityManager is null");
            }
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnected();
            }
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isConnect(Context context) {

        try {
            Object systemService = context.getSystemService("connectivity");
            ConnectivityManager connectivityManager = (ConnectivityManager) systemService;
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return networkCapabilities.hasCapability(16);
        } catch (Exception unused) {
            return false;
        }
    }


    public static AdUtil getInstance(Activity context) {
        if (mInstance == null) {
            mInstance = new AdUtil(context);
        } else {
            mInstance.activity = context;
        }
        return mInstance;
    }

    public void loadBanner(FrameLayout ad_container) {
        if (AdConfig.banneradsoff) {
            if (AdConfig.isadon) {
                if (isNetworkAvailable(activity) && isConnect(activity)) {
                    String adbanner = AdConfig.cat_banner;

                    if (adbanner.equals("admob")) {
                        Ad_Admob.getInstance(activity).loadBannerAd(ad_container);
                    } else if (adbanner.equals("custom")) {
                        Ad_Image.getInstance(activity).showCustomBanner(activity, ad_container);
                    } else {
                        ad_container.setVisibility(View.GONE);
                    }
                } else {
                    ad_container.setVisibility(View.GONE);
                }
            } else {
                ad_container.setVisibility(View.GONE);
            }
        } else {
            ad_container.setVisibility(View.GONE);
        }

    }


    public void loadNative(FrameLayout frameLayout, String type) {
        if (AdConfig.nativeadsoff) {
            if (AdConfig.isadon) {
                if (isNetworkAvailable(activity) && isConnect(activity)) {
                    String adnative = AdConfig.cat_native;
                    if (adnative.equals("admob")) {
                        Ad_Admob.getInstance(activity).loadNativeAd(frameLayout, type);
                    } else if (adnative.equals("custom")) {
                        Ad_Image.getInstance(activity).showCustomBigNative(activity, frameLayout, type);
                    } else {
                        frameLayout.setVisibility(View.GONE);
                    }
                } else {
                    frameLayout.setVisibility(View.GONE);
                }
            } else {
                frameLayout.setVisibility(View.GONE);
            }
        } else {
            frameLayout.setVisibility(View.GONE);
        }
    }

    public void loadInter(MyCallback callback) {
        if (AdConfig.interadsoff) {
            if (AdConfig.isadon) {
                if (isNetworkAvailable(activity) && isConnect(activity)) {
                    String adinter = AdConfig.cat_inter;
                    if (adinter.equals("admob")) {
                        SpeedInter_Admob.getInstance(activity).showInterstitialAd(callback);
                    } else if (adinter.equals("custom")) {
                        Ad_Image.getInstance(activity).startSpecialActivity(activity, callback, false);
                    } else {
                        callback.onAdCompleted();
                    }
                } else {
                    callback.onAdCompleted();
                }
            } else {
                callback.onAdCompleted();
            }
        } else {
            callback.onAdCompleted();
        }

    }

    public void loadBack(MyCallback callback) {
        if (AdConfig.backadsoff) {
            if (AdConfig.isadon) {
                if (isNetworkAvailable(activity) && isConnect(activity)) {
                    String adinter = AdConfig.cat_inter;
                    if (adinter.equals("admob")) {
                        SpeedInter_Admob.getInstance(activity).showBackAd(callback);
                    } else if (adinter.equals("custom")) {
                        Ad_Image.getInstance(activity).startBackActivity(activity, callback, true);
                    } else {
                        callback.onAdCompleted();
                    }
                } else {
                    callback.onAdCompleted();
                }
            } else {
                callback.onAdCompleted();
            }
        } else {
            callback.onAdCompleted();
        }


    }

}