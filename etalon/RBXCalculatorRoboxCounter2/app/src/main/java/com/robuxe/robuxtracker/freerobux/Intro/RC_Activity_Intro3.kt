package com.robuxe.robuxtracker.freerobux.Intro

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.Utils.RC_UtilsClass
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil

class RC_Activity_Intro3 : AppCompatActivity() {

    private var selectedOption: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            // Status bar background color
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }

// Force WHITE status bar icons/text (light icons)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        setContentView(R.layout.rc_activity_intro3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AdUtil.getInstance(this).loadNative(findViewById(R.id.frameContainerBottom), "small")

        val btnBack = findViewById<ImageView>(R.id.ivback)
        btnBack.setOnClickListener { v -> onBackPressedDispatcher.onBackPressed() }
        val img1 = findViewById<ImageView>(R.id.neonun)
        val img2 = findViewById<ImageView>(R.id.girlun)
        val img3 = findViewById<ImageView>(R.id.boyun)
        val img4 = findViewById<ImageView>(R.id.hoodiun)
        val img5 = findViewById<ImageView>(R.id.shortun)
        val img6 = findViewById<ImageView>(R.id.nenoun)
        val btnNext = findViewById<TextView>(R.id.btnNext)

        img1.setOnClickListener {
            selectedOption = "neon"
            img1.setImageResource(R.drawable.skin1sel)
            img2.setImageResource(R.drawable.skin2un)
            img3.setImageResource(R.drawable.skin3un)
            img4.setImageResource(R.drawable.skin4un)
            img5.setImageResource(R.drawable.skin5un)
            img6.setImageResource(R.drawable.skin6un)
        }
        img2.setOnClickListener {
            selectedOption = "girl"
            img1.setImageResource(R.drawable.skin1un)
            img2.setImageResource(R.drawable.skin2sel)
            img3.setImageResource(R.drawable.skin3un)
            img4.setImageResource(R.drawable.skin4un)
            img5.setImageResource(R.drawable.skin5un)
            img6.setImageResource(R.drawable.skin6un)
        }
        img3.setOnClickListener {
            selectedOption = "boy"
            img1.setImageResource(R.drawable.skin1un)
            img2.setImageResource(R.drawable.skin2un)
            img3.setImageResource(R.drawable.skin3sel)
            img4.setImageResource(R.drawable.skin4un)
            img5.setImageResource(R.drawable.skin5un)
            img6.setImageResource(R.drawable.skin6un)
        }
        img4.setOnClickListener {
            selectedOption = "hoodie"
            img1.setImageResource(R.drawable.skin1un)
            img2.setImageResource(R.drawable.skin2un)
            img3.setImageResource(R.drawable.skin3un)
            img4.setImageResource(R.drawable.skin4sel)
            img5.setImageResource(R.drawable.skin5un)
            img6.setImageResource(R.drawable.skin6un)
        }

        img5.setOnClickListener {
            selectedOption = "short"
            img1.setImageResource(R.drawable.skin1un)
            img2.setImageResource(R.drawable.skin2un)
            img3.setImageResource(R.drawable.skin3un)
            img4.setImageResource(R.drawable.skin4un)
            img5.setImageResource(R.drawable.skin5sel)
            img6.setImageResource(R.drawable.skin6un)
        }

        img6.setOnClickListener {
            selectedOption = "neno"
            img1.setImageResource(R.drawable.skin1un)
            img2.setImageResource(R.drawable.skin2un)
            img3.setImageResource(R.drawable.skin3un)
            img4.setImageResource(R.drawable.skin4un)
            img5.setImageResource(R.drawable.skin5un)
            img6.setImageResource(R.drawable.skin6sel)
        }

        btnNext.setOnClickListener {

            if (selectedOption == null) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            } else {
                RC_UtilsClass.startSpecialActivity(
                    this,
                    Intent(this, RC_Activity_Intro4::class.java),
                    false
                )
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Your custom back button logic here

                // Your custom back button logic here
                AdUtil.getInstance(this@RC_Activity_Intro3).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        resetSelection()
    }

    private fun resetSelection() {
        selectedOption = null

        findViewById<ImageView>(R.id.neonun).setImageResource(R.drawable.skin1un)
        findViewById<ImageView>(R.id.girlun).setImageResource(R.drawable.skin2un)
        findViewById<ImageView>(R.id.boyun).setImageResource(R.drawable.skin3un)
        findViewById<ImageView>(R.id.hoodiun).setImageResource(R.drawable.skin4un)
        findViewById<ImageView>(R.id.shortun).setImageResource(R.drawable.skin5un)
        findViewById<ImageView>(R.id.nenoun).setImageResource(R.drawable.skin6un)
    }
}