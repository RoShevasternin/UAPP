package com.robuxe.robuxtracker.freerobux.Scratchcard

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.Utils.RC_AppPrefrence
import com.robuxe.robuxtracker.freerobux.Utils.RC_UtilsClass
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil

class RC_Spin_Activity : AppCompatActivity() {

    companion object {
        private val REWARDS = intArrayOf(1200, 900, 100, 300, 0, 400, 600, 1000, 700, 800, 500, 200)
        private const val SEGMENTS = 12
    }

    private lateinit var spinButton: TextView
    private lateinit var winDialog: Dialog
    private lateinit var wheelImage: ImageView
    private lateinit var tvTotalDiamonds: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }

        setContentView(R.layout.rc_activity_spin)
        
        val windowController = androidx.core.view.WindowInsetsControllerCompat(window, window.decorView)
        windowController.isAppearanceLightStatusBars = false // false for dark background (light icons)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        AdUtil.getInstance(this).loadNative(findViewById(R.id.frm_native), "small")

        wheelImage = findViewById(R.id.s_wheel)
        tvTotalDiamonds = findViewById(R.id.tv_totaldiamond)
        spinButton = findViewById(R.id.tvSpin)

        winDialog = RC_UtilsClass.createDialog(this, R.layout.rc_dialog_win_diamonds)

        wheelImage.post {
            wheelImage.pivotX = wheelImage.width / 2f
            wheelImage.pivotY = wheelImage.height / 2f
        }

        spinButton.setOnClickListener { startSpin() }
        findViewById<View>(R.id.ivback).setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        updateSpinUI()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_Spin_Activity).loadBack { finish() }
            }
        })
    }

    private fun startSpin() {
        spinButton.isEnabled = false
        spinButton.alpha = 0.5f

        val start = wheelImage.rotation
        val random = (Math.random() * 360f).toFloat()
        val end = start + (360f * 5f) + random

        val animator = ObjectAnimator.ofFloat(wheelImage, View.ROTATION, start, end)
        animator.duration = 5000
        animator.interpolator = DecelerateInterpolator()

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (isFinishing || isDestroyed) return
                
                val finalRotation = wheelImage.rotation % 360f
                wheelImage.rotation = finalRotation

                val coins = getRewardFromRotation(finalRotation)

                if (coins > 0) {
                    RC_AppPrefrence.getInstance(this@RC_Spin_Activity).addDiamonds(coins)
                    showWinDialog(winDialog, coins)
                } else {
                    showLossDialog()
                }

                updateSpinUI()
                spinButton.isEnabled = true
                spinButton.alpha = 1f
            }
        })
        animator.start()
    }

    private fun getRewardFromRotation(rotationDegrees: Float): Int {
        var normalized = rotationDegrees % 360f
        if (normalized < 0) normalized += 360f

        val segmentSize = 360f / SEGMENTS
        val pointerAngle = (360f - normalized) % 360f
        val index = ((pointerAngle + segmentSize / 2f) / segmentSize).toInt() % SEGMENTS

        return REWARDS[index]
    }

    private fun showWinDialog(dialog: Dialog, coins: Int) {
        if (isFinishing || isDestroyed) return
        dialog.show()

        val title = dialog.findViewById<TextView>(R.id.title)
        val subtitle = dialog.findViewById<TextView>(R.id.tv_subtitle)
        val frmOk = dialog.findViewById<TextView>(R.id.btnOkay)

        title.text = "Congratulations!"
        subtitle.text = "$coins RBX Coins are added to your balance."

        frmOk.setOnClickListener {
            AdUtil.getInstance(this).loadInter {
                if (!isFinishing && !isDestroyed) {
                    dialog.dismiss()
                    updateSpinUI()
                }
            }
        }
    }

    private fun showLossDialog() {
        if (isFinishing || isDestroyed) return
        val dialog = RC_UtilsClass.createDialog(this, R.layout.rc_dialog_retry)
        dialog.show()

        val frmOk = dialog.findViewById<TextView>(R.id.btnRetry)
        frmOk.setOnClickListener {
            AdUtil.getInstance(this).loadInter {
                if (!isFinishing && !isDestroyed) {
                    dialog.dismiss()
                    updateSpinUI()
                }
            }
        }
    }

    private fun updateSpinUI() {
        tvTotalDiamonds.text = RC_AppPrefrence.getInstance(this).totalDiamonds.toString()
    }

    override fun onResume() {
        super.onResume()
        updateSpinUI()
    }
}
