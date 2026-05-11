package com.robuxe.robuxtracker.freerobux.Quiz

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
import com.robuxe.robuxtracker.freerobux.Utils.RC_AppPrefrence
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityQuizResultBinding

class RC_QuizResult_Activity : AppCompatActivity() {
    private lateinit var binding: RcActivityQuizResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }


        binding = RcActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AdUtil.getInstance(this@RC_QuizResult_Activity).loadNative(binding.frmNative, "small")

        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val correct = intent.getIntExtra("correct", 0)
        val coins = intent.getIntExtra("coins", 0)
        binding.tvDescription.text = "Your $correct Answers Are Correct."
        binding.tvWin.text = coins.toString()
        binding.add.setOnClickListener {
            RC_AppPrefrence.getInstance(this@RC_QuizResult_Activity).addDiamonds(coins)
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_QuizResult_Activity).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }
}
