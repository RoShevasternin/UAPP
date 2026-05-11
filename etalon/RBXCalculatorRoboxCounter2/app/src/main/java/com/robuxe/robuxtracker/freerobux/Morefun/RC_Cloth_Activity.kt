package com.robuxe.robuxtracker.freerobux.Morefun

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
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityClothBinding

class RC_Cloth_Activity : AppCompatActivity() {

    private lateinit var binding: RcActivityClothBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }



        binding = RcActivityClothBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AdUtil.getInstance(this@RC_Cloth_Activity).loadNative(binding.frmNative, "big")
        AdUtil.getInstance(this@RC_Cloth_Activity).loadBanner(binding.frmBanner)

        binding.clt1.setOnClickListener {
            allIntent(Intent(this@RC_Cloth_Activity, RC_Data_Activity::class.java).putExtra("which", "Shoes Collection"), false)
        }
        binding.clt2.setOnClickListener {
            allIntent(Intent(this@RC_Cloth_Activity, RC_Data_Activity::class.java).putExtra("which", "Pants Collection"), false)
        }
        binding.clt3.setOnClickListener {
            allIntent(Intent(this@RC_Cloth_Activity, RC_Data_Activity::class.java).putExtra("which", "T-shirt Collection"), false)
        }
        binding.clt4.setOnClickListener {
            allIntent(Intent(this@RC_Cloth_Activity, RC_Data_Activity::class.java).putExtra("which", "Shirts Collection"), false)
        }

        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_Cloth_Activity).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }

    private fun allIntent(intent: Intent, b: Boolean) {
        RC_UtilsClass.startSpecialActivity(this@RC_Cloth_Activity, intent, b)
    }
}
