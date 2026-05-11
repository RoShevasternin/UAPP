package com.robuxe.robuxtracker.freerobux.Scratchcard

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.Utils.RC_AppPrefrence
import com.robuxe.robuxtracker.freerobux.Utils.RC_UtilsClass
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityRedeemBinding

class RC_Redeem_Activity : AppCompatActivity() {

    private lateinit var binding: RcActivityRedeemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }


        binding = RcActivityRedeemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AdUtil.getInstance(this@RC_Redeem_Activity).loadNative(binding.frmNative, "big")
        AdUtil.getInstance(this@RC_Redeem_Activity).loadBanner(binding.frmBanner)
        updateCoins()

        binding.iv7500.setOnClickListener { handleRedeem(7500) }
        binding.iv10000.setOnClickListener { handleRedeem(10000) }
        binding.iv15000.setOnClickListener { handleRedeem(15000) }
        binding.iv20000.setOnClickListener { handleRedeem(20000) }

        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_Redeem_Activity).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }

    private fun handleRedeem(requiredCoins: Int) {
        val totalCoins = RC_AppPrefrence.getInstance(this).totalDiamonds
        if (totalCoins >= requiredCoins) {
            showWinDialog(requiredCoins, totalCoins, requiredCoins)
        } else {
            showLossDialog()
        }
    }

    private fun showWinDialog(coins: Int, totalCoins: Int, requiredCoins: Int) {
        val dialog = RC_UtilsClass.createDialog(this, R.layout.rc_dialog_win_diamonds)
        dialog.show()

        val title = dialog.findViewById<TextView>(R.id.title)
        val subtitle = dialog.findViewById<TextView>(R.id.tv_subtitle)
        val btnOkay = dialog.findViewById<TextView>(R.id.btnOkay)

        title.text = "Congratulations!"
        btnOkay.text = "Done"
        subtitle.text = "Your redeem request of $coins RBX coins has been recorded.\n\nThis is a simulator only. No real payout will be made."
        btnOkay.setOnClickListener {
            AdUtil.getInstance(this@RC_Redeem_Activity).loadInter(object : MyCallback {
                override fun onAdCompleted() {
                    RC_AppPrefrence.getInstance(this@RC_Redeem_Activity).totalDiamonds = totalCoins - requiredCoins
                    updateCoins()
                    dialog.dismiss()
                }
            })
        }
    }

    private fun showLossDialog() {
        val dialog = RC_UtilsClass.createDialog(this, R.layout.rc_dialog_dailyretry)
        dialog.show()

        val title = dialog.findViewById<TextView>(R.id.title)
        val subtitle = dialog.findViewById<TextView>(R.id.tv_subtitle)
        val frmOk = dialog.findViewById<TextView>(R.id.btnOkay)

        subtitle.visibility = View.VISIBLE
        title.text = "Redeem Failed !!!"
        subtitle.text = "Not enough RBX coins to redeem.\n\nKeep playing and enjoy the fun.\nThis is a simulated feature for entertainment only."
        frmOk.setOnClickListener {
            AdUtil.getInstance(this@RC_Redeem_Activity).loadInter(object : MyCallback {
                override fun onAdCompleted() {
                    dialog.dismiss()
                }
            })
        }
    }

    private fun updateCoins() {
        binding.tvTotaldiamond.text = RC_AppPrefrence.getInstance(this).totalDiamonds.toString()
    }

    override fun onResume() {
        super.onResume()
        updateCoins()
    }
}
