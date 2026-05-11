package com.robuxe.robuxtracker.freerobux.adsmodule.adscode.admob;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.robuxe.robuxtracker.freerobux.R;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdConfig;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.imagead.Ad_Image;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

public class Ad_Admob {
    private static Dialog progressDialog;

    private static Ad_Admob mInstance;
    Activity context;

    public Ad_Admob(Activity context) {
        this.context = context;
    }

    public static Ad_Admob getInstance(Activity context) {
        if (mInstance == null) {
            mInstance = new Ad_Admob(context);
        }
        return mInstance;
    }


    public static void showLoading(Activity activity) {
        if (activity == null || activity.isFinishing()) return;

        activity.runOnUiThread(() -> {

            // ✅ ALWAYS DISMISS OLD FIRST
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            progressDialog = new Dialog(activity);
            progressDialog.setContentView(R.layout.a_custom_progress_layout); // your layout
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            progressDialog.show();
        });
    }


    public static void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

//    public void loadBannerAd(FrameLayout ad_container) {
//
//        ad_container.removeAllViews();
//
//        AdView adView = new AdView(context);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId(AdConfig.admob_banner_id);
//
//        ad_container.addView(adView);
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);
//
//        adView.setAdListener(new AdListener() {
//
//            @Override
//            public void onAdLoaded() {
//                ad_container.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
//                ad_container.removeAllViews();
//                Log.e("TAGAdmob", "" + adError.getMessage());
//                // ✅ Fallback Image Banner
//                Ad_Image.getInstance((Activity) context).showCustomBanner((Activity) context, ad_container);
//            }
//        });
//    }

    public void loadBannerAd(FrameLayout ad_container) {

        ad_container.removeAllViews();

        AdView adView = new AdView(context);
        adView.setAdUnitId(AdConfig.admob_banner_id);

        // ✅ Adaptive banner size
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                context,
                getAdWidth(ad_container)
        );
        adView.setAdSize(adSize);

        ad_container.addView(adView);

        // ✅ Collapsible banner request
        Bundle extras = new Bundle();
        extras.putString("collapsible", "bottom"); // or "top"

        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();

        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                ad_container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                ad_container.removeAllViews();
                Log.e("TAGAdmob", adError.getMessage());

                // ✅ Image fallback banner
                Ad_Image.getInstance((Activity) context)
                        .showCustomBanner((Activity) context, ad_container);
            }
        });
    }
    private int getAdWidth(FrameLayout ad_container) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        float density = outMetrics.density;
        int adWidthPixels = ad_container.getWidth();

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        return (int) (adWidthPixels / density);
    }

    public void loadNativeAd(FrameLayout adPlaceholder, String type) {

        AdLoader.Builder builder = new AdLoader.Builder(context, AdConfig.admob_native_id);

        builder.forNativeAd(nativeAd -> {
            adPlaceholder.setVisibility(View.VISIBLE);

            populateNativeAdView(context, nativeAd, adPlaceholder, type);

        }).withNativeAdOptions(
                new NativeAdOptions.Builder().build()
        );

        AdLoader adLoader = builder.withAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                adPlaceholder.setVisibility(View.GONE);
                Ad_Image.getInstance((Activity) context).showCustomBigNative((Activity) context, adPlaceholder, type);
                Log.e("TAGAdmob", "" + loadAdError.getMessage());
            }
        }).build();
        adPlaceholder.setVisibility(View.GONE);

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAdView(Context context, NativeAd nativeAd, FrameLayout frameLayout, String type) {
        NativeAdView adView = null;
        if (type.equalsIgnoreCase("big")) {
            adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.a_ad_unified_big, frameLayout, false);
        } else if (type.equalsIgnoreCase("small")) {
            adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.a_ad_unified_small, frameLayout, false);
        }

        if (adView != null) {
            frameLayout.removeAllViews();
            frameLayout.addView(adView);
        }


        adView.setMediaView(adView.findViewById(R.id.ad_media));


        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));


        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());


        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }


        adView.setNativeAd(nativeAd);
    }


}
