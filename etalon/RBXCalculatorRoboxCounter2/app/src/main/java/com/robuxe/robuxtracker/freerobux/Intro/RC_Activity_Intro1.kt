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
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil

class RC_Activity_Intro1 : AppCompatActivity() {

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

        setContentView(R.layout.rc_activity_intro1)
        
        val windowController = androidx.core.view.WindowInsetsControllerCompat(window, window.decorView)
        windowController.isAppearanceLightStatusBars = false // false for dark background (light icons)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AdUtil.getInstance(this).loadNative(findViewById(R.id.frameContainerBottom), "small")

        val neonun = findViewById<ImageView>(R.id.neonun)
        val girlun = findViewById<ImageView>(R.id.girlun)
        val boyun = findViewById<ImageView>(R.id.boyun)
        val hoodiun = findViewById<ImageView>(R.id.hoodiun)
        val shortun = findViewById<ImageView>(R.id.shortun)
        val nenoun = findViewById<ImageView>(R.id.nenoun)
        val btnNext = findViewById<TextView>(R.id.btnNext)
        val btnBack = findViewById<ImageView>(R.id.ivback)
        btnBack.setOnClickListener { v -> onBackPressedDispatcher.onBackPressed() }
        neonun.setOnClickListener {
            selectedOption = "neon"
            neonun.setImageResource(R.drawable.neonsel)
            girlun.setImageResource(R.drawable.girlsun)
            boyun.setImageResource(R.drawable.boyun)
            hoodiun.setImageResource(R.drawable.hodiun)
            shortun.setImageResource(R.drawable.shortun)
            nenoun.setImageResource(R.drawable.nenoun)
        }
        girlun.setOnClickListener {
            selectedOption = "girl"
            neonun.setImageResource(R.drawable.neonun)
            girlun.setImageResource(R.drawable.girlssel)
            boyun.setImageResource(R.drawable.boyun)
            hoodiun.setImageResource(R.drawable.hodiun)
            shortun.setImageResource(R.drawable.shortun)
            nenoun.setImageResource(R.drawable.nenoun)
        }
        boyun.setOnClickListener {
            selectedOption = "boy"
            neonun.setImageResource(R.drawable.neonun)
            girlun.setImageResource(R.drawable.girlsun)
            boyun.setImageResource(R.drawable.boyssel)
            hoodiun.setImageResource(R.drawable.hodiun)
            shortun.setImageResource(R.drawable.shortun)
            nenoun.setImageResource(R.drawable.nenoun)
        }
        hoodiun.setOnClickListener {
            selectedOption = "hoodie"
            neonun.setImageResource(R.drawable.neonun)
            girlun.setImageResource(R.drawable.girlsun)
            boyun.setImageResource(R.drawable.boyun)
            hoodiun.setImageResource(R.drawable.hoodiesel)
            shortun.setImageResource(R.drawable.shortun)
            nenoun.setImageResource(R.drawable.nenoun)
        }

        shortun.setOnClickListener {
            selectedOption = "short"
            neonun.setImageResource(R.drawable.neonun)
            girlun.setImageResource(R.drawable.girlsun)
            boyun.setImageResource(R.drawable.boyun)
            hoodiun.setImageResource(R.drawable.hodiun)
            shortun.setImageResource(R.drawable.shortsel)
            nenoun.setImageResource(R.drawable.nenoun)
        }

        nenoun.setOnClickListener {
            selectedOption = "neno"
            neonun.setImageResource(R.drawable.neonun)
            girlun.setImageResource(R.drawable.girlsun)
            boyun.setImageResource(R.drawable.boyun)
            hoodiun.setImageResource(R.drawable.hodiun)
            shortun.setImageResource(R.drawable.shortun)
            nenoun.setImageResource(R.drawable.nenosel)
        }

        val requestPermissionLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            RC_UtilsClass.startSpecialActivity(
                this,
                Intent(this, RC_Activity_Intro2::class.java),
                false
            )
        }

        btnNext.setOnClickListener {
            if (selectedOption == null) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (androidx.core.content.ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        RC_UtilsClass.startSpecialActivity(
                            this,
                            Intent(this, RC_Activity_Intro2::class.java),
                            false
                        )
                    }
                } else {
                    RC_UtilsClass.startSpecialActivity(
                        this,
                        Intent(this, RC_Activity_Intro2::class.java),
                        false
                    )
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        resetSelection()
    }

    private fun resetSelection() {
        selectedOption = null

        findViewById<ImageView>(R.id.neonun).setImageResource(R.drawable.neonun)
        findViewById<ImageView>(R.id.girlun).setImageResource(R.drawable.girlsun)
        findViewById<ImageView>(R.id.boyun).setImageResource(R.drawable.boyun)
        findViewById<ImageView>(R.id.hoodiun).setImageResource(R.drawable.hodiun)
        findViewById<ImageView>(R.id.shortun).setImageResource(R.drawable.shortun)
        findViewById<ImageView>(R.id.nenoun).setImageResource(R.drawable.nenoun)
    }
}