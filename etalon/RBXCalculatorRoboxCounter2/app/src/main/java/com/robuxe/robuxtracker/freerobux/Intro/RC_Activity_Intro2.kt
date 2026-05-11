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

class RC_Activity_Intro2 : AppCompatActivity() {

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
        setContentView(R.layout.rc_activity_intro2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AdUtil.getInstance(this).loadNative(findViewById(R.id.frameContainerBottom), "small")

        val btnBack = findViewById<ImageView>(R.id.ivback)
        btnBack.setOnClickListener { v -> onBackPressedDispatcher.onBackPressed() }
        val neonun = findViewById<ImageView>(R.id.neonun)
        val girlun = findViewById<ImageView>(R.id.girlun)
        val boyun = findViewById<ImageView>(R.id.boyun)
        val hoodiun = findViewById<ImageView>(R.id.hoodiun)
        val shortun = findViewById<ImageView>(R.id.shortun)
        val nenoun = findViewById<ImageView>(R.id.nenoun)
        val btnNext = findViewById<TextView>(R.id.btnNext)


        neonun.setOnClickListener {
            selectedOption = "neon"
            neonun.setImageResource(R.drawable.djvidesel)
            girlun.setImageResource(R.drawable.urbenstkterun)
            boyun.setImageResource(R.drawable.pinktrendun)
            hoodiun.setImageResource(R.drawable.hoodiqueenun)
            shortun.setImageResource(R.drawable.basketballun)
            nenoun.setImageResource(R.drawable.nenochillun)
        }
        girlun.setOnClickListener {
            selectedOption = "girl"
            neonun.setImageResource(R.drawable.djvideun)
            girlun.setImageResource(R.drawable.urbansel)
            boyun.setImageResource(R.drawable.pinktrendun)
            hoodiun.setImageResource(R.drawable.hoodiqueenun)
            shortun.setImageResource(R.drawable.basketballun)
            nenoun.setImageResource(R.drawable.nenochillun)
        }
        boyun.setOnClickListener {
            selectedOption = "boy"
            neonun.setImageResource(R.drawable.djvideun)
            girlun.setImageResource(R.drawable.urbenstkterun)
            boyun.setImageResource(R.drawable.pinktrendersel)
            hoodiun.setImageResource(R.drawable.hoodiqueenun)
            shortun.setImageResource(R.drawable.basketballun)
            nenoun.setImageResource(R.drawable.nenochillun)
        }
        hoodiun.setOnClickListener {
            selectedOption = "hoodie"
            neonun.setImageResource(R.drawable.djvideun)
            girlun.setImageResource(R.drawable.urbenstkterun)
            boyun.setImageResource(R.drawable.pinktrendun)
            hoodiun.setImageResource(R.drawable.hoodiequeensel)
            shortun.setImageResource(R.drawable.basketballun)
            nenoun.setImageResource(R.drawable.nenochillun)
        }

        shortun.setOnClickListener {
            selectedOption = "short"
            neonun.setImageResource(R.drawable.djvideun)
            girlun.setImageResource(R.drawable.urbenstkterun)
            boyun.setImageResource(R.drawable.pinktrendun)
            hoodiun.setImageResource(R.drawable.hoodiqueenun)
            shortun.setImageResource(R.drawable.basketballsel)
            nenoun.setImageResource(R.drawable.nenochillun)
        }

        nenoun.setOnClickListener {
            selectedOption = "neno"
            neonun.setImageResource(R.drawable.djvideun)
            girlun.setImageResource(R.drawable.urbenstkterun)
            boyun.setImageResource(R.drawable.pinktrendun)
            hoodiun.setImageResource(R.drawable.hoodiqueenun)
            shortun.setImageResource(R.drawable.basketballun)
            nenoun.setImageResource(R.drawable.nenochillsel)
        }
        btnNext.setOnClickListener {

            if (selectedOption == null) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            } else {
                RC_UtilsClass.startSpecialActivity(
                    this,
                    Intent(this, RC_Activity_Intro3::class.java),
                    false
                )
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Your custom back button logic here
                AdUtil.getInstance(this@RC_Activity_Intro2).loadBack(object : MyCallback {
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

        findViewById<ImageView>(R.id.neonun).setImageResource(R.drawable.djvideun)
        findViewById<ImageView>(R.id.girlun).setImageResource(R.drawable.urbenstkterun)
        findViewById<ImageView>(R.id.boyun).setImageResource(R.drawable.pinktrendun)
        findViewById<ImageView>(R.id.hoodiun).setImageResource(R.drawable.hoodiqueenun)
        findViewById<ImageView>(R.id.shortun).setImageResource(R.drawable.basketballun)
        findViewById<ImageView>(R.id.nenoun).setImageResource(R.drawable.nenochillun)
    }
}