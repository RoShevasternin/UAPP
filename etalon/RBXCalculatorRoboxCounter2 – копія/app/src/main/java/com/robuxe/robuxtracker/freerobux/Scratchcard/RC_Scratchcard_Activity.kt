package com.robuxe.robuxtracker.freerobux.Scratchcard

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityScratchcardBinding
import java.util.*

class RC_Scratchcard_Activity : AppCompatActivity() {

    private lateinit var binding: RcActivityScratchcardBinding
    private var scratchView: RC_ScratchView? = null
    private var diamondsWon = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }

        binding = RcActivityScratchcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val windowController = androidx.core.view.WindowInsetsControllerCompat(window, window.decorView)
        windowController.isAppearanceLightStatusBars = false // false for dark background (light icons)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AdUtil.getInstance(this@RC_Scratchcard_Activity).loadNative(binding.frmNative, "big")

        scratchView = binding.rrScratchView

        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvTotaldiamond.text = RC_AppPrefrence.getInstance(this).totalDiamonds.toString()

        updateRandomDiamondText()

        val winDialog = RC_UtilsClass.createDialog(this, R.layout.rc_dialog_win_diamonds)
        val retryDialog = RC_UtilsClass.createDialog(this, R.layout.rc_dialog_retry)

        scratchView?.setRevealListener(object : RC_ScratchView.IRevealListener {
            override fun onRevealed(rrScratchView: RC_ScratchView) {
                rrScratchView.clear()
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!isFinishing && !isDestroyed) {
                        if (diamondsWon > 0) {
                            showWinDialog(winDialog)
                        } else {
                            showRetryDialog(retryDialog)
                        }
                    }
                }, 800)
            }

            override fun onRevealPercentChangedListener(rrScratchView: RC_ScratchView, percent: Float) {
                Log.d("Scratch", "Revealed: $percent")
            }
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_Scratchcard_Activity).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }

    private fun refreshScratchCard() {
        scratchView?.mask()
        scratchView?.invalidate()
        updateRandomDiamondText()
    }

    private fun showWinDialog(dialog: Dialog) {
        if (isFinishing || isDestroyed) return
        dialog.show()
        val title = dialog.findViewById<TextView>(R.id.tv_subtitle)
        val btnOkay = dialog.findViewById<TextView>(R.id.btnOkay)

        title.text = "You Won $diamondsWon RBX!"
        btnOkay.text = "Add to Wallet"

        btnOkay.setOnClickListener {
            AdUtil.getInstance(this@RC_Scratchcard_Activity).loadInter(object : MyCallback {
                override fun onAdCompleted() {
                    if (!isFinishing && !isDestroyed) {
                        dialog.dismiss()
                        refreshScratchCard()
                        RC_AppPrefrence.getInstance(this@RC_Scratchcard_Activity).addDiamonds(diamondsWon)
                        binding.tvTotaldiamond.text = RC_AppPrefrence.getInstance(this@RC_Scratchcard_Activity).totalDiamonds.toString()
                    }
                }
            })
        }
    }

    private fun showRetryDialog(dialog: Dialog) {
        if (isFinishing || isDestroyed) return
        dialog.show()
        scratchView?.mask()

        dialog.findViewById<View>(R.id.btnRetry).setOnClickListener {
            AdUtil.getInstance(this@RC_Scratchcard_Activity).loadInter(object : MyCallback {
                override fun onAdCompleted() {
                    if (!isFinishing && !isDestroyed) {
                        dialog.dismiss()
                        refreshScratchCard()
                    }
                }
            })
        }
    }

    private fun updateRandomDiamondText() {
        diamondsWon = getRandomDiamonds()
        binding.textWinDiamond.post {
            if (diamondsWon > 0) {
                binding.textWinDiamond.text = "You won $diamondsWon RBX!"
            } else {
                binding.textWinDiamond.text = "Sorry, no RBX this time.\nTry again!"
            }
        }
    }

    private fun getRandomDiamonds(): Int {
        val random = Random()
        return if (random.nextBoolean()) random.nextInt(30) + 1 else 0
    }
}
