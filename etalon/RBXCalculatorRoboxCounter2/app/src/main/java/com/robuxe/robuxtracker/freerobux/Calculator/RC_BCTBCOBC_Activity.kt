package com.robuxe.robuxtracker.freerobux.Calculator

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityConvertrBinding

class RC_BCTBCOBC_Activity : AppCompatActivity() {

    private lateinit var binding: RcActivityConvertrBinding
    private var membershipType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }

        binding = RcActivityConvertrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val windowController = androidx.core.view.WindowInsetsControllerCompat(window, window.decorView)
        windowController.isAppearanceLightStatusBars = false // false for dark background (light icons)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        AdUtil.getInstance(this@RC_BCTBCOBC_Activity).loadNative(binding.frmNative, "big")

        membershipType = intent.getStringExtra("bc_tbc_obc")
        setupUIByType()

        binding.tvConvertNow.setOnClickListener { calculateDailyCost() }
        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_BCTBCOBC_Activity).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }

    private fun setupUIByType() {
        if (membershipType == null) return
        binding.tvSubHeader.text = getString(R.string.this_amount_of_dollars_will_convert_to)
        binding.tvType.text = getString(R.string.rbx)
        when {
            membershipType.equals(getString(R.string.BCDailyRBXCounter), ignoreCase = true) -> {
                binding.tvTitle.text = getString(R.string.BCDailyRBXCounter)
                binding.tvHeader.text = getString(R.string.bc_to_rbx_cost)
            }
            membershipType.equals(getString(R.string.TBCDailyRBXCounter), ignoreCase = true) -> {
                binding.tvTitle.text = getString(R.string.TBCDailyRBXCounter)
                binding.tvHeader.text = getString(R.string.tbc_to_rbx_cost)
            }
            else -> {
                binding.tvTitle.text = getString(R.string.OBCDailyRBXCounter)
                binding.tvHeader.text = getString(R.string.obc_to_rbx_cost)
            }
        }
    }

    private fun calculateDailyCost() {
        val inputText = binding.etAmount.text.toString().trim()
        if (TextUtils.isEmpty(inputText)) {
            binding.tvConvertValue.text = ""
            return
        }
        val inputValue: Double = try {
            inputText.toDouble()
        } catch (e: NumberFormatException) {
            binding.tvConvertValue.text = ""
            return
        }
        val multiplier = getMultiplierByType()
        val result = inputValue * multiplier
        binding.tvConvertValue.text = result.toString()
    }

    private fun getMultiplierByType(): Double {
        return when {
            membershipType.equals(getString(R.string.BCDailyRBXCounter), ignoreCase = true) -> 15.0
            membershipType.equals(getString(R.string.TBCDailyRBXCounter), ignoreCase = true) -> 35.0
            else -> 60.0 // OBC
        }
    }
}
