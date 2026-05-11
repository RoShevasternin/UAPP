package com.robuxe.robuxtracker.freerobux.Calculator

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.Utils.RC_UtilsClass
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityCalDailyBinding

class RC_Cal_Daily_Activity : AppCompatActivity() {

    private lateinit var binding: RcActivityCalDailyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }


        binding = RcActivityCalDailyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AdUtil.getInstance(this@RC_Cal_Daily_Activity).loadNative(binding.frmNative, "big")

        binding.bc.setOnClickListener {
            allIntent(RC_BCTBCOBC_Activity::class.java, getString(R.string.BCDailyRBXCounter))
        }
        binding.tbc.setOnClickListener {
            allIntent(RC_BCTBCOBC_Activity::class.java, getString(R.string.TBCDailyRBXCounter))
        }
        binding.obc.setOnClickListener {
            allIntent(RC_BCTBCOBC_Activity::class.java, getString(R.string.OBCDailyRBXCounter))
        }

        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_Cal_Daily_Activity).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }

    private fun allIntent(aClass: Class<*>, STR: String) {
        RC_UtilsClass.startSpecialActivity(
            this@RC_Cal_Daily_Activity,
            Intent(this@RC_Cal_Daily_Activity, aClass).putExtra("bc_tbc_obc", STR),
            false
        )
    }
}
