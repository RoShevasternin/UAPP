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
import com.robuxe.robuxtracker.freerobux.RC_Home_Activity
import com.robuxe.robuxtracker.freerobux.Utils.RC_UtilsClass
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil

class RC_Activity_Intro4 : AppCompatActivity() {
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
        setContentView(R.layout.rc_activity_intro4)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AdUtil.getInstance(this).loadNative(findViewById(R.id.frameContainerBottom), "small")

        val btnBack = findViewById<ImageView>(R.id.ivback)
        val ninja = findViewById<ImageView>(R.id.ninja)
        val wareaolf = findViewById<ImageView>(R.id.wareaolf)
        val knight = findViewById<ImageView>(R.id.knight)
        val pirate = findViewById<ImageView>(R.id.pirate)
        val superhero = findViewById<ImageView>(R.id.superhero)
        val villan = findViewById<ImageView>(R.id.villan)
        val hardcour = findViewById<ImageView>(R.id.hardcour)
        val elder = findViewById<ImageView>(R.id.elder)
        val vamprire = findViewById<ImageView>(R.id.vamprire)
        val btnNext = findViewById<TextView>(R.id.btnNext)



        ninja.setOnClickListener {
            selectedOption = "Ninja"
            ninja.setImageResource(R.drawable.anims_1)
            wareaolf.setImageResource(R.drawable.animicon2un)
            knight.setImageResource(R.drawable.animicon3un)
            pirate.setImageResource(R.drawable.animicon4un)
            superhero.setImageResource(R.drawable.animicon5un)
            villan.setImageResource(R.drawable.animicon6un)
            hardcour.setImageResource(R.drawable.animicon7un)
            elder.setImageResource(R.drawable.animicon8un)
            vamprire.setImageResource(R.drawable.animicon9un)
        }
        wareaolf.setOnClickListener {
            selectedOption = "Warewolf"
            ninja.setImageResource(R.drawable.animicon1un)
            wareaolf.setImageResource(R.drawable.anims_2)
            knight.setImageResource(R.drawable.animicon3un)
            pirate.setImageResource(R.drawable.animicon4un)
            superhero.setImageResource(R.drawable.animicon5un)
            villan.setImageResource(R.drawable.animicon6un)
            hardcour.setImageResource(R.drawable.animicon7un)
            elder.setImageResource(R.drawable.animicon8un)
            vamprire.setImageResource(R.drawable.animicon9un)
        }
        knight.setOnClickListener {
            selectedOption = "Knight"
            ninja.setImageResource(R.drawable.animicon1un)
            wareaolf.setImageResource(R.drawable.animicon2un)
            knight.setImageResource(R.drawable.anims_3)
            pirate.setImageResource(R.drawable.animicon4un)
            superhero.setImageResource(R.drawable.animicon5un)
            villan.setImageResource(R.drawable.animicon6un)
            hardcour.setImageResource(R.drawable.animicon7un)
            elder.setImageResource(R.drawable.animicon8un)
            vamprire.setImageResource(R.drawable.animicon9un)
        }
        pirate.setOnClickListener {
            selectedOption = "Pirate"
            ninja.setImageResource(R.drawable.animicon1un)
            wareaolf.setImageResource(R.drawable.animicon2un)
            knight.setImageResource(R.drawable.animicon3un)
            pirate.setImageResource(R.drawable.anims_4)
            superhero.setImageResource(R.drawable.animicon5un)
            villan.setImageResource(R.drawable.animicon6un)
            hardcour.setImageResource(R.drawable.animicon7un)
            elder.setImageResource(R.drawable.animicon8un)
            vamprire.setImageResource(R.drawable.animicon9un)
        }

        superhero.setOnClickListener {
            selectedOption = "Super Hero"
            ninja.setImageResource(R.drawable.animicon1un)
            wareaolf.setImageResource(R.drawable.animicon2un)
            knight.setImageResource(R.drawable.animicon3un)
            pirate.setImageResource(R.drawable.animicon4un)
            superhero.setImageResource(R.drawable.anims_5)
            villan.setImageResource(R.drawable.animicon6un)
            hardcour.setImageResource(R.drawable.animicon7un)
            elder.setImageResource(R.drawable.animicon8un)
            vamprire.setImageResource(R.drawable.animicon9un)
        }

        villan.setOnClickListener {
            selectedOption = "Villain"
            ninja.setImageResource(R.drawable.animicon1un)
            wareaolf.setImageResource(R.drawable.animicon2un)
            knight.setImageResource(R.drawable.animicon3un)
            pirate.setImageResource(R.drawable.animicon4un)
            superhero.setImageResource(R.drawable.animicon5un)
            villan.setImageResource(R.drawable.anims_6)
            hardcour.setImageResource(R.drawable.animicon7un)
            elder.setImageResource(R.drawable.animicon8un)
            vamprire.setImageResource(R.drawable.animicon9un)
        }
        hardcour.setOnClickListener {
            selectedOption = "Hard Cour"
            ninja.setImageResource(R.drawable.animicon1un)
            wareaolf.setImageResource(R.drawable.animicon2un)
            knight.setImageResource(R.drawable.animicon3un)
            pirate.setImageResource(R.drawable.animicon4un)
            superhero.setImageResource(R.drawable.animicon5un)
            villan.setImageResource(R.drawable.animicon6un)
            hardcour.setImageResource(R.drawable.anims_7)
            elder.setImageResource(R.drawable.animicon8un)
            vamprire.setImageResource(R.drawable.animicon9un)
        }
        elder.setOnClickListener {
            selectedOption = "Elder"
            ninja.setImageResource(R.drawable.animicon1un)
            wareaolf.setImageResource(R.drawable.animicon2un)
            knight.setImageResource(R.drawable.animicon3un)
            pirate.setImageResource(R.drawable.animicon4un)
            superhero.setImageResource(R.drawable.animicon5un)
            villan.setImageResource(R.drawable.animicon6un)
            hardcour.setImageResource(R.drawable.animicon7un)
            elder.setImageResource(R.drawable.anims_8)
            vamprire.setImageResource(R.drawable.animicon9un)
        }
        vamprire.setOnClickListener {
            selectedOption = "Vampire"
            ninja.setImageResource(R.drawable.animicon1un)
            wareaolf.setImageResource(R.drawable.animicon2un)
            knight.setImageResource(R.drawable.animicon3un)
            pirate.setImageResource(R.drawable.animicon4un)
            superhero.setImageResource(R.drawable.animicon5un)
            villan.setImageResource(R.drawable.animicon6un)
            hardcour.setImageResource(R.drawable.animicon7un)
            elder.setImageResource(R.drawable.animicon8un)
            vamprire.setImageResource(R.drawable.anims_9)
        }


        btnNext.setOnClickListener {

            if (selectedOption == null) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            } else {
                RC_UtilsClass.startSpecialActivity(
                    this,
                    Intent(this, RC_Home_Activity::class.java),
                    false
                )
            }
        }

        btnBack.setOnClickListener { v -> onBackPressedDispatcher.onBackPressed() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_Activity_Intro4).loadBack(object : MyCallback {
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

        findViewById<ImageView>(R.id.ninja).setImageResource(R.drawable.animicon1un)
        findViewById<ImageView>(R.id.wareaolf).setImageResource(R.drawable.animicon2un)
        findViewById<ImageView>(R.id.knight).setImageResource(R.drawable.animicon3un)
        findViewById<ImageView>(R.id.pirate).setImageResource(R.drawable.animicon4un)
        findViewById<ImageView>(R.id.superhero).setImageResource(R.drawable.animicon5un)
        findViewById<ImageView>(R.id.villan).setImageResource(R.drawable.animicon6un)
        findViewById<ImageView>(R.id.hardcour).setImageResource(R.drawable.animicon7un)
        findViewById<ImageView>(R.id.elder).setImageResource(R.drawable.animicon8un)
        findViewById<ImageView>(R.id.vamprire).setImageResource(R.drawable.animicon9un)
    }
}