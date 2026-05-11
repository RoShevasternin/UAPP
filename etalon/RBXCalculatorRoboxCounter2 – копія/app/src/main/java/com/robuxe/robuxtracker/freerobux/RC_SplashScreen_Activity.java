package com.robuxe.robuxtracker.freerobux;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.robuxe.robuxtracker.freerobux.Intro.RC_Activity_Intro1;
import com.robuxe.robuxtracker.freerobux.adsmodule.MyApplication;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdConfig;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.admob.AppOpenManager_Admob1;
import com.robuxe.robuxtracker.freerobux.adsmodule.model.RemoteConfigModel;


public class RC_SplashScreen_Activity extends AppCompatActivity {

    FirebaseRemoteConfig mFirebaseRemoteConfig;
    Dialog noInternetDialog;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EdgeToEdge.enable(this);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Status bar background color
            window.setStatusBarColor(Color.parseColor("#111111"));
            window.setNavigationBarColor(Color.parseColor("#000000"));
        }

        setContentView(R.layout.rc_activity_splash);

        androidx.core.view.WindowInsetsControllerCompat windowController =
                new androidx.core.view.WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowController.setAppearanceLightStatusBars(false); // false for dark background (light icons)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        checkInternetAndStart();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });


    }

    private void checkInternetAndStart() {
        if (isInternetAvailable()) {
            fetchAndProceedToMain();
        } else {
            showNoInternetDialog();
        }
    }

    private void showNoInternetDialog() {
        if (isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed())) return;
        if (noInternetDialog != null && noInternetDialog.isShowing()) return;

        noInternetDialog = new Dialog(this);
        noInternetDialog.setContentView(R.layout.rc_dialog_no_internet);
        noInternetDialog.setCancelable(false);
        noInternetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // ⚡ Set proper width & height
        if (noInternetDialog.getWindow() != null) {
            noInternetDialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,   // full width
                    ViewGroup.LayoutParams.WRAP_CONTENT    // wrap height
            );
        }

        TextView tryAgain = noInternetDialog.findViewById(R.id.btnRetry);
        tryAgain.setOnClickListener(v -> {
            if (isInternetAvailable()) {
                if (!isFinishing() && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !isDestroyed())) {
                    noInternetDialog.dismiss();
                    fetchAndProceedToMain();
                }
            } else {
                Toast.makeText(this, "Still no internet!", Toast.LENGTH_SHORT).show();
            }
        });

        noInternetDialog.show();
    }


    private void fetchAndProceedToMain() {
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            boolean showAppOpenInSplash = false;

            if (task.isSuccessful()) {
                String json = mFirebaseRemoteConfig.getString("RBXApp");
                Log.d("AppOpenAd_Admob1", "Firebase fetch successful" + json);

                try {
                    Gson gson = new Gson();
                    RemoteConfigModel config = gson.fromJson(json, RemoteConfigModel.class);
                    MyApplication.ad_preferences.saveRemoteConfig(config);


                    AdConfig.cat_banner = config.getBanner_ad_category_id();
                    AdConfig.cat_native = config.getNative_ad_category_id();
                    AdConfig.cat_inter = config.getInt_ad_category_id();
                    AdConfig.cat_reward = config.getReward_ad_category_id();
                    AdConfig.cat_appopen = config.getApp_open_ad_category_id();
                    AdConfig.isadon = true;//config.getIs_ad_show();

                    AdConfig.admob_appopen_id = config.getAdmob().getApp_open_ads_id();
                    AdConfig.admob_banner_id = config.getAdmob().getBanner_ads_id();
                    AdConfig.admob_native_id = config.getAdmob().getNative_ads_id();
                    AdConfig.admob_inter_id = config.getAdmob().getInt_ads_id();
                    AdConfig.admob_reward_id = config.getAdmob().getRewarded_ads_id();

                    AdConfig.banner_ads_type_show = config.getBannerad_type();
                    AdConfig.Privacypolicy = config.getPrivacypolicy();
                    AdConfig.afterpagesintads = config.getAfter_x_pages_int_ads();
                    AdConfig.afterpagesbackads = config.getBack_count();


                    AdConfig.nativeadsoff = config.getNativeadsoff();
                    AdConfig.banneradsoff = config.getBackadsoff();
                    AdConfig.interadsoff = config.getInteradsoff();
                    AdConfig.backadsoff = config.getBackadsoff();
                    AdConfig.intro_on_off = config.getIntro_on_off();

                    showAppOpenInSplash = config.getIs_splash_show();

                    // Тимчасово для тестування кастомної реклами
                    AdConfig.cat_native = "custom";
                    AdConfig.cat_banner = "custom";
                    AdConfig.cat_inter = "custom";
                    AdConfig.isadon = true;
                    AdConfig.nativeadsoff = true;
                    AdConfig.banneradsoff = true;
                    AdConfig.interadsoff = true;
                    AdConfig.backadsoff = true;

                } catch (Exception e) {
                    Log.e("AppOpenAd_Admob1", "Error parsing RemoteConfig", e);
                }
            } else {
                Log.e("AppOpenAd_Admob1", "Firebase fetch failed");
            }

            if (showAppOpenInSplash) { //showAppOpenInSplash && MyApplication.ad_preferences.getRemoteConfig().getIs_ad_show()) {
                if (AdConfig.cat_appopen.equals("admob")) {
                    Log.d("AppOpenAd_Admob1", "issplashon is true, load and show AppOpenAd");
                    showAppOpenAdThenMoveNext();
                } else {
                    Log.d("AppOpenAd_Admob1", "issplashon is false, go to next screen directly");
                    moveToNextScreen();
                }
            } else {
                Log.d("AppOpenAd_Admob1", "issplashon is false, go to next screen directly");
                moveToNextScreen();
            }
        });
    }

    private void showAppOpenAdThenMoveNext() {
        AppOpenManager_Admob1 appOpenManager = new AppOpenManager_Admob1(this);

        Handler handler = new Handler();
        Runnable timeoutRunnable = () -> {
            Log.d("AppOpenAd_Admob1", "Timeout reached. Ad not shown. Moving to next screen.");
            moveToNextScreen();
        };
        handler.postDelayed(timeoutRunnable, 10000);

        appOpenManager.loadAdWithCallback(this, () -> {
            handler.removeCallbacks(timeoutRunnable);

            if (appOpenManager.isAdAvailable()) {
                Log.d("AppOpenAd_Admob1", "Ad loaded. Showing ad.");
                appOpenManager.showAdIfAvailable(RC_SplashScreen_Activity.this, () -> {
                    Log.d("AppOpenAd_Admob1", "Ad finished. Moving to next screen.");
                    moveToNextScreen();
                });
            } else {
                Log.d("AppOpenAd_Admob1", "Ad failed. Moving to next screen.");
                moveToNextScreen();
            }
        });
    }

    private void moveToNextScreen() {
        Log.d("AppOpenAd_Admob1", "Navigating to Intro screen");
        if (!isFinishing()) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (AdConfig.intro_on_off) {
                    startActivity(new Intent(RC_SplashScreen_Activity.this, RC_Activity_Intro1.class));

                } else {
                    startActivity(new Intent(RC_SplashScreen_Activity.this, RC_Home_Activity.class));

                }
            }, 3000);

        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

}
