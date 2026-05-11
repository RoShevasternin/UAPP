package com.robuxe.robuxtracker.freerobux.adsmodule.adscode.imagead;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robuxe.robuxtracker.freerobux.R;
import com.robuxe.robuxtracker.freerobux.adsmodule.MyApplication;
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdConfig;
import com.robuxe.robuxtracker.freerobux.adsmodule.model.CustomAdsConfig;
import com.robuxe.robuxtracker.freerobux.adsmodule.model.RemoteConfigModel;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

public class Ad_Image {

    public static int backCount = 1;
    private static Ad_Image mInstance;
    private static int clickCount = 1;
    ;
    private static int afterXClicksIntAds, afterBac;
    private static int interRedirectLinkIndex = 0;
    private static int adIndex = 0;
    private static int headlineIndex = 0;
    private static int descriptionIndex = 0;
    private static int buttonIndex = 0;
    private static int imageIndex = 0;
    private static int bannerHeadlineIndex = 0;
    private static int bannerDescriptionIndex = 0;
    private static int bannerButtonIndex = 0;
    private static int bannerImageIndex = 0;
    private static int bannerLogoIndex = 0;
    Activity context;

    public Ad_Image(Activity context) {
        this.context = context;
        afterXClicksIntAds = AdConfig.afterpagesintads;
        afterBac = AdConfig.afterpagesbackads;


    }

    public static Ad_Image getInstance(Activity context) {
        if (mInstance == null) {
            mInstance = new Ad_Image(context);
        }
        return mInstance;
    }

    public static String getRandomRedirectLink(List<String> links) {
        if (links == null || links.isEmpty()) {
            return "No links available";
        }
        Random random = new Random();
        int randomIndex = random.nextInt(links.size());
        return links.get(randomIndex);
    }

    public static void showCustomBanner(final Context activity, FrameLayout frmbanner) {

        RemoteConfigModel remoteConfigModel2 = MyApplication.ad_preferences.getRemoteConfig();
        frmbanner.setVisibility(0);

        if (AdConfig.banner_ads_type_show.equalsIgnoreCase("layout")) {
            View view = LayoutInflater.from(activity).inflate(R.layout.a_custom_native_banner, (ViewGroup) null, false);

            CustomAdsConfig bannerAds = remoteConfigModel2.getCustomAdsConfig();

            // Calculate sequential positions for layout banner
            int headlinePos = bannerHeadlineIndex % bannerAds.getHeadline().size();
            int descPos = bannerDescriptionIndex % bannerAds.getDescription().size();
            int buttonPos = bannerButtonIndex % bannerAds.getButtonText().size();
            int logoPos = bannerLogoIndex % bannerAds.getRoundImage().size();

            // Increment counters
            bannerHeadlineIndex++;
            bannerDescriptionIndex++;
            bannerButtonIndex++;
            bannerLogoIndex++;

            // Set views with sequential positions
            Glide.with(activity).load(bannerAds.getRoundImage().get(logoPos)).into((ImageView) view.findViewById(R.id.img_logo));
            ((TextView) view.findViewById(R.id.tv_appname)).setText(bannerAds.getHeadline().get(headlinePos));
            ((TextView) view.findViewById(R.id.tv_desc)).setText(bannerAds.getDescription().get(descPos));
            TextView btn_install = (TextView) view.findViewById(R.id.btn_install);
            btn_install.setText(bannerAds.getButtonText().get(buttonPos));

            view.findViewById(R.id.btn_install).setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    CustomTabLinkOpen.openLink(activity, getRandomRedirectLink(remoteConfigModel2.getCustomLinks().getBannerRedirectLink()));
                }
            });

            if (frmbanner != null) {
                frmbanner.removeAllViews();
            }
            frmbanner.addView(view);
            return;
        }

        // Image-only banner (else case)
        View view2 = LayoutInflater.from(activity).inflate(R.layout.a_custom_native_banner_image, (ViewGroup) null, false);
        CustomAdsConfig bannerAds2 = remoteConfigModel2.getCustomAdsConfig();

        // Calculate sequential position for banner image
        int bannerImgPos = bannerImageIndex % bannerAds2.getBannerImage().size();
        bannerImageIndex++;

        Glide.with(activity).load(bannerAds2.getBannerImage().get(bannerImgPos)).centerCrop().into((ImageView) view2.findViewById(R.id.iv_banner_image));

        ((ImageView) view2.findViewById(R.id.iv_banner_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabLinkOpen.openLink(activity, getRandomRedirectLink(remoteConfigModel2.getCustomLinks().getBannerRedirectLink()));
            }
        });

        if (frmbanner != null) {
            frmbanner.removeAllViews();
        }
        frmbanner.addView(view2);
    }


    public static void showCustomBigNative(final Context activity, FrameLayout linearLayout, String type) {
        String imageUrl;
        RemoteConfigModel remoteConfigModel2 = MyApplication.ad_preferences.getRemoteConfig();
        linearLayout.setVisibility(0);
        View view = type.equalsIgnoreCase("big") ?
                LayoutInflater.from(activity).inflate(R.layout.a_custom_native_big, null, false) :
                LayoutInflater.from(activity).inflate(R.layout.custom_small, null, false);

        if (remoteConfigModel2 == null) {
            throw new AssertionError();
        }

        CustomAdsConfig nativeAds = remoteConfigModel2.getCustomAdsConfig();

        // Calculate position for each element separately
        int headlinePos = headlineIndex % nativeAds.getHeadline().size();
        int descPos = descriptionIndex % nativeAds.getDescription().size();
        int buttonPos = buttonIndex % nativeAds.getButtonText().size();
        int imagePos = imageIndex % nativeAds.getNativeImageLarge().size();

        // Increment all counters for next call
        headlineIndex++;
        descriptionIndex++;
        buttonIndex++;
        imageIndex++;

        // Set image based on type
        if (type.equalsIgnoreCase("big")) {
            imageUrl = nativeAds.getNativeImageLarge().get(imagePos);
        } else if (type.equalsIgnoreCase("medium")) {
            imageUrl = nativeAds.getNativeImageMedium().get(imagePos);
        } else {
            imageUrl = nativeAds.getNativeImageSmall().get(imagePos);
        }

        // Set all views with their respective position indices
        imageLoading(imageUrl, (ImageView) view.findViewById(R.id.img_banner));
        ((TextView) view.findViewById(R.id.btn_install)).setText(nativeAds.getButtonText().get(buttonPos));
        Glide.with(activity).load(nativeAds.getRoundImage().get(imagePos)).into((ImageView) view.findViewById(R.id.img_logo));
        ((TextView) view.findViewById(R.id.tv_appname)).setText(nativeAds.getHeadline().get(headlinePos));
        ((TextView) view.findViewById(R.id.tv_desc)).setText(nativeAds.getDescription().get(descPos));

        ((LinearLayout) view.findViewById(R.id.ln_mainview)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabLinkOpen.openLink(activity, getRandomRedirectLink(MyApplication.ad_preferences.getRemoteConfig().getCustomLinks().getNativeRedirectLink()));
            }
        });
        view.findViewById(R.id.btn_install).setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                CustomTabLinkOpen.openLink(activity, getRandomRedirectLink(MyApplication.ad_preferences.getRemoteConfig().getCustomLinks().getNativeRedirectLink()));
            }
        });

        if (linearLayout != null) {
            linearLayout.removeAllViews();
        }
        linearLayout.addView(view);
    }


    public static void imageLoading(String imageUrl, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        if (imageUrl != null && imageUrl.toLowerCase().endsWith(".gif")) {
            Glide.with(imageView.getContext()).asGif().load(imageUrl).into(imageView);
        } else {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(imageView.getContext()).load(imageUrl).into(imageView);
        }
    }

    public static void startSpecialActivity(final Activity context, final MyCallback callback, final boolean shouldFinish) {

        if (afterXClicksIntAds == 0) {
            if (callback != null) callback.onAdCompleted();
            if (shouldFinish) context.finish();
        } else {
            clickCount++;
            if (afterXClicksIntAds == 1 || (clickCount % afterXClicksIntAds) == 1) {
                if (MyApplication.ad_preferences.getRemoteConfig().getNext_double_on_off()) {
                    try {
                        Ads_Interstitial.showAds_full(context, new Ads_Interstitial.OnFinishAds() {
                            @Override
                            public void onFinishAds(boolean b) {
                                Ads_Interstitial.showAds_full(context, new Ads_Interstitial.OnFinishAds() {
                                    @Override
                                    public void onFinishAds(boolean b) {
                                        if (callback != null) callback.onAdCompleted();
                                        if (shouldFinish) context.finish();
                                        handleAdRedirect(context, b);
                                    }
                                }, new boolean[0]);
                            }
                        }, new boolean[0]);
                    } catch (Exception e) {
                        Log.e("Error", "Error");
                    }
                } else {
                    Ads_Interstitial.showAds_full(context, new Ads_Interstitial.OnFinishAds() {
                        @Override
                        public void onFinishAds(boolean b) {
                            if (callback != null) callback.onAdCompleted();
                            if (shouldFinish) context.finish();
                            handleAdRedirect(context, b);
                        }
                    }, new boolean[0]);
                }
            } else {
                if (callback != null) callback.onAdCompleted();
                if (shouldFinish) context.finish();
            }
        }
    }

    public static void startSpecialActivityOne(final Activity context, final MyCallback callback, final boolean shouldFinish) {

        if (afterXClicksIntAds == 0) {
            if (callback != null) callback.onAdCompleted();
            if (shouldFinish) context.finish();
        } else {
            clickCount++;
            if (afterXClicksIntAds == 1 || (clickCount % afterXClicksIntAds) == 1) {
                try {
                    Ads_Interstitial.showAds_full(context, new Ads_Interstitial.OnFinishAds() {
                        @Override
                        public void onFinishAds(boolean b) {
                            if (callback != null) callback.onAdCompleted();
                            if (shouldFinish) context.finish();
                            handleAdRedirect(context, b);
                        }
                    }, new boolean[0]);
                } catch (Exception e) {
                    Log.e("Error", "Error");
                }
            } else {
                if (callback != null) callback.onAdCompleted();
                if (shouldFinish) context.finish();
            }
        }
    }

//    private static void handleAdRedirect(Activity context, boolean adShown) {
//        RemoteConfigModel remoteConfigModel = MyApplication.ad_preferences.getRemoteConfig();
//
//        if (adShown && remoteConfigModel.getIs_ad_show()) {
//            CustomTabLinkOpen.openLink(context, getRandomRedirectLink(MyApplication.ad_preferences.getRemoteConfig().getCustomLinks().getInterRedirectLink()));
//        }
//    }

    private static void handleAdRedirect(Activity context, boolean adShown) {
        RemoteConfigModel remoteConfigModel = MyApplication.ad_preferences.getRemoteConfig();

        if (adShown && remoteConfigModel.getIs_ad_show()) {
            // Get the redirect links list
            List<String> redirectLinks = remoteConfigModel.getCustomLinks().getInterRedirectLink();

            // Calculate sequential position
            int linkPos = interRedirectLinkIndex % redirectLinks.size();
            interRedirectLinkIndex++;

            // Open the link at current position (sequential, not random)
            CustomTabLinkOpen.openLink(context, redirectLinks.get(linkPos));
        }
    }

    public static void startBackActivity(final Activity context, final MyCallback callback, final boolean shouldFinish) {
        if (afterBac == 0) {
            if (callback != null) callback.onAdCompleted();
            if (shouldFinish) context.finish();
            return; // Add return to prevent further execution
        } else {
            backCount++;
            if (afterBac == 1 || (backCount % afterBac) == 1) {
                if (MyApplication.ad_preferences.getRemoteConfig().getBack_double_on_off()) {
                    Ads_Interstitial.showAds_full(context, new Ads_Interstitial.OnFinishAds() {
                        @Override
                        public void onFinishAds(boolean b) {
                            Ads_Interstitial.showAds_full(context, new Ads_Interstitial.OnFinishAds() {
                                @Override
                                public void onFinishAds(boolean b) {
                                    if (callback != null)
                                        callback.onAdCompleted(); // Add null check
                                    if (shouldFinish) {
                                        context.finish();
                                    }
                                    handleAdRedirect(context, b);
                                }
                            }, new boolean[0]);
                        }
                    }, new boolean[0]);
                } else {
                    Ads_Interstitial.showAds_full(context, new Ads_Interstitial.OnFinishAds() {
                        @Override
                        public void onFinishAds(boolean b) {
                            if (callback != null) callback.onAdCompleted(); // Add null check
                            if (shouldFinish) {
                                context.finish();
                            }
                            handleAdRedirect(context, b);
                        }
                    }, new boolean[0]);
                }
            } else {
                if (callback != null) callback.onAdCompleted();
                if (shouldFinish) context.finish();
            }
        }
    }

    public static void startBackOneActivity(final Activity context, final MyCallback callback, final boolean shouldFinish) {
        if (afterBac == 0) {
            if (callback != null) callback.onAdCompleted();
            if (shouldFinish) context.finish();
            return; // Add return to prevent further execution
        } else {
            backCount++;
            if (afterBac == 1 || (backCount % afterBac) == 1) {
                Ads_Interstitial.showAds_full(context, new Ads_Interstitial.OnFinishAds() {
                    @Override
                    public void onFinishAds(boolean b) {
                        if (callback != null) callback.onAdCompleted(); // Add null check
                        if (shouldFinish) {
                            context.finish();
                        }
                        handleAdRedirect(context, b);
                    }
                }, new boolean[0]);
            } else {
                if (callback != null) callback.onAdCompleted();
                if (shouldFinish) context.finish();
            }
        }
    }

}
