package com.robuxe.robuxtracker.freerobux.Calculator

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityConvertrBinding
import java.util.Locale

class RC_Convertr_Activity : AppCompatActivity() {

    private lateinit var binding: RcActivityConvertrBinding
    private var isRbxToDollar = false
    private var formattedResult: String? = null

    companion object {
        private const val RBX_TO_DOLLAR_RATE = 0.00073
    }

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AdUtil.getInstance(this@RC_Convertr_Activity).loadNative(binding.frmNative, "small")

        isRbxToDollar = intent.getBooleanExtra("rbx_to_dlr", false)

        if (isRbxToDollar) {
            binding.tvTitle.text = getString(R.string.title_rbxtodoller)
            binding.tvHeader.text = getString(R.string.rbx_to_dollar_cost)
            binding.tvSubHeader.text = getString(R.string.buying_this_much_rbx_will_cost)
            binding.tvType.text = getString(R.string.dollars)
            binding.etAmount.hint = getString(R.string.strRBXHint)
        } else {
            binding.tvTitle.text = getString(R.string.title_dollertorbx)
            binding.tvHeader.text = getString(R.string.dollar_to_rbx_conversion)
            binding.tvSubHeader.text = getString(R.string.this_amount_of_dollars_will_convert_to)
            binding.tvType.text = getString(R.string.rbx)
            binding.etAmount.hint = getString(R.string.strDollarHint)
        }

        binding.tvConvertNow.setOnClickListener { convertAmount() }
        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_Convertr_Activity).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }

    private fun convertAmount() {
        val inputText = binding.etAmount.text.toString().trim()
        if (TextUtils.isEmpty(inputText)) {
            binding.tvConvertValue.text = ""
            Toast.makeText(this, "Enter a value", Toast.LENGTH_SHORT).show()
            return
        }
        val inputValue: Double = try {
            inputText.toDouble()
        } catch (e: NumberFormatException) {
            binding.tvConvertValue.text = ""
            return
        }
        val calculatedValue = if (isRbxToDollar) {
            inputValue * RBX_TO_DOLLAR_RATE
        } else {
            inputValue / RBX_TO_DOLLAR_RATE
        }
        formattedResult = String.format(Locale.getDefault(), "%.6f", calculatedValue)
        binding.tvConvertValue.text = formattedResult
    }
}
