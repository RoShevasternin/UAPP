package com.robuxe.robuxtracker.freerobux.Utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Window
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil

object RC_UtilsClass {
    @JvmStatic
    fun startSpecialActivity(act: Activity, intent: Intent, b: Boolean) {
        AdUtil.getInstance(act).loadInter(object : MyCallback {
            override fun onAdCompleted() {
                act.startActivity(intent)
            }
        })
    }

    @JvmStatic
    fun createDialog(activity: Context, contentView: Int): Dialog {
        val dialog = Dialog(activity, R.style.WideDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(contentView)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    @JvmStatic
    fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}
