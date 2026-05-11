package com.robuxe.robuxtracker.freerobux

import android.content.Intent
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
import com.robuxe.robuxtracker.freerobux.Calculator.RC_Cal_RBX_Activity
import com.robuxe.robuxtracker.freerobux.Calculator.RC_Convertr_Activity
import com.robuxe.robuxtracker.freerobux.Memee.RC_Activity_Meme
import com.robuxe.robuxtracker.freerobux.Morefun.RC_Accessoris_Activity
import com.robuxe.robuxtracker.freerobux.Morefun.RC_AnimationType_Activity
import com.robuxe.robuxtracker.freerobux.Morefun.RC_Cloth_Activity
import com.robuxe.robuxtracker.freerobux.Morefun.RC_Data_Activity
import com.robuxe.robuxtracker.freerobux.Morefun.RC_FaceBody_Activity
import com.robuxe.robuxtracker.freerobux.Quiz.RC_Quiz_Activity
import com.robuxe.robuxtracker.freerobux.Scratchcard.RC_Redeem_Activity
import com.robuxe.robuxtracker.freerobux.Scratchcard.RC_Scratchcard_Activity
import com.robuxe.robuxtracker.freerobux.Scratchcard.RC_Spin_Activity
import com.robuxe.robuxtracker.freerobux.Utils.RC_AppPrefrence
import com.robuxe.robuxtracker.freerobux.Utils.RC_UtilsClass
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdConfig
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityHomeBinding
import java.util.*

class RC_Home_Activity : AppCompatActivity() {

    private lateinit var binding: RcActivityHomeBinding

    companion object {
        private const val DAY_MILLIS = 24 * 60 * 60 * 1000L
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

        binding = RcActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val windowController = androidx.core.view.WindowInsetsControllerCompat(window, window.decorView)
        windowController.isAppearanceLightStatusBars = false // false for dark background (light icons)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val requestPermissionLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { _ -> }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        updateCoins()

        AdUtil.getInstance(this).loadNative(binding.frmNative1, "big")
        AdUtil.getInstance(this).loadNative(binding.frmNative2, "big")
        AdUtil.getInstance(this).loadBanner(binding.frmBanner)

        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.h1.setOnClickListener { allIntent(Intent(this, RC_Cal_RBX_Activity::class.java), false) }
        binding.meme.setOnClickListener { allIntent(Intent(this, RC_Activity_Meme::class.java), false) }
        binding.setting.setOnClickListener { allIntent(Intent(this, RC_Setting_Activity::class.java), false) }
        binding.scratch.setOnClickListener { allIntent(Intent(this, RC_Scratchcard_Activity::class.java), false) }
        binding.spin.setOnClickListener { allIntent(Intent(this, RC_Spin_Activity::class.java), false) }
        binding.cal2.setOnClickListener {
            allIntent(Intent(this, RC_Convertr_Activity::class.java).apply { putExtra("rbx_to_dlr", true) }, false)
        }
        binding.cal3.setOnClickListener {
            allIntent(Intent(this, RC_Convertr_Activity::class.java).apply { putExtra("rbx_to_dlr", false) }, false)
        }
        binding.redeem.setOnClickListener { allIntent(Intent(this, RC_Redeem_Activity::class.java), false) }
        binding.anima.setOnClickListener { allIntent(Intent(this, RC_AnimationType_Activity::class.java), false) }
        binding.access.setOnClickListener { allIntent(Intent(this, RC_Accessoris_Activity::class.java), false) }
        binding.clh.setOnClickListener { allIntent(Intent(this, RC_Cloth_Activity::class.java), false) }
        binding.head.setOnClickListener { allIntent(Intent(this, RC_FaceBody_Activity::class.java), false) }
        binding.quiz.setOnClickListener { allIntent(Intent(this, RC_Quiz_Activity::class.java), false) }
        binding.allChar.setOnClickListener {
            allIntent(Intent(this, RC_Data_Activity::class.java).apply { putExtra("which", "char") }, false)
        }
        binding.daily.setOnClickListener { handleDailyReward() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (AdConfig.intro_on_off) {
                    AdUtil.getInstance(this@RC_Home_Activity).loadBack { finish() }
                } else {
                    finishAffinity()
                }
            }
        })
    }

    private fun allIntent(intent: Intent, b: Boolean) {
        RC_UtilsClass.startSpecialActivity(this, intent, b)
    }

    private fun handleDailyReward() {
        val currentTime = System.currentTimeMillis()
        val lastClaim = RC_AppPrefrence.getInstance(this).lastDailyTime

        if (currentTime - lastClaim >= DAY_MILLIS) {
            val reward = Random().nextInt(20) + 1
            showWinDialog(reward)
            RC_AppPrefrence.getInstance(this).lastDailyTime = currentTime
        } else {
            if (isFinishing || isDestroyed) return
            val dialog = RC_UtilsClass.createDialog(this, R.layout.rc_dialog_dailyretry)
            dialog.show()
            dialog.findViewById<View>(R.id.btnOkay).setOnClickListener {
                AdUtil.getInstance(this).loadInter { if (!isFinishing && !isDestroyed) dialog.dismiss() }
            }
        }
    }

    private fun showWinDialog(coins: Int) {
        if (isFinishing || isDestroyed) return
        val dialog = RC_UtilsClass.createDialog(this, R.layout.rc_dialog_win_diamonds)
        dialog.show()

        dialog.findViewById<TextView>(R.id.title).text = "Congratulations !"
        dialog.findViewById<TextView>(R.id.tv_subtitle).text = "$coins RBX Coins are added to your virtual balance."

        dialog.findViewById<View>(R.id.btnOkay).setOnClickListener {
            AdUtil.getInstance(this).loadInter {
                if (!isFinishing && !isDestroyed) {
                    dialog.dismiss()
                    RC_AppPrefrence.getInstance(this).addDiamonds(coins)
                    updateCoins()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateCoins()
    }

    private fun updateCoins() {
        binding.tvTotaldiamond.text = RC_AppPrefrence.getInstance(this).totalDiamonds.toString()
    }
}
