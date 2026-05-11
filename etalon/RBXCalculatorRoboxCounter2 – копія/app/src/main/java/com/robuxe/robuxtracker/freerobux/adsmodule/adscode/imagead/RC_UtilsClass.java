package com.robuxe.robuxtracker.freerobux.adsmodule.adscode.imagead;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Window;

import com.robuxe.robuxtracker.freerobux.R;
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback;
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil;


public class RC_UtilsClass {
    public static void startSpecialActivity(Activity act, Intent intent, boolean b) {
        AdUtil.getInstance(act).loadInter(new MyCallback() {
            @Override
            public void onAdCompleted() {
                act.startActivity(intent);
            }
        });
    }

    public static Dialog createDialog(Context activity, int contentView) {
        Dialog dialog = new Dialog(activity, R.style.WideDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(contentView);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
