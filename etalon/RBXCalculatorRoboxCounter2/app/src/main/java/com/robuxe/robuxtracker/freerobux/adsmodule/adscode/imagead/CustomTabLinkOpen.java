package com.robuxe.robuxtracker.freerobux.adsmodule.adscode.imagead;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsIntent;

import com.robuxe.robuxtracker.freerobux.adsmodule.MyApplication;


public class CustomTabLinkOpen {
    public static void openLink(Context context, String url) {

        try {
            if (!url.equals("No links available") && !url.isEmpty()) {
                MyApplication.isAdShowing = true;
                Intent intent = new Intent("android.intent.action.VIEW");
                Bundle bundle = new Bundle();
                bundle.putBinder(CustomTabsIntent.EXTRA_SESSION, null);
                intent.putExtras(bundle);
                intent.putExtra(CustomTabsIntent.EXTRA_ENABLE_INSTANT_APPS, true);
                intent.setPackage("com.android.chrome");
                intent.setData(Uri.parse(url));
                context.startActivity(intent, null);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getAvailableBrowserPackage(Context context, String preferredBrowserPackage) {
        PackageManager packageManager = context.getPackageManager();
        if (isPackageInstalled(preferredBrowserPackage, packageManager)) {
            return preferredBrowserPackage;
        }
        String[] otherBrowsers = {"org.mozilla.firefox", "com.microsoft.emmx", "com.opera.browser", "com.sec.android.app.sbrowser"};
        for (String browser : otherBrowsers) {
            if (isPackageInstalled(browser, packageManager)) {
                return browser;
            }
        }
        return null;
    }

    private static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}