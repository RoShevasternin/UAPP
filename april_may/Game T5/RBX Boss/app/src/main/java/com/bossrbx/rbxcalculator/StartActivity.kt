package com.bossrbx.rbxcalculator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bossrbx.rbxcalculator.util.log

private var onCreateCounter = 0

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        onCreateCounter++
        log("StartActivity: $onCreateCounter")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // правка 7: диплінк повернення (rbxcalculator://reward) приземляється
        // сюди — прокидаємо data в MainActivity, обробка там (Biz.onActivityIntent).
        // extras — route/gate_pl з пушів етапу 2.
        startActivity(Intent(this, MainActivity::class.java).also {
            it.data = intent?.data
            intent?.extras?.let { e -> it.putExtras(e) }
        })
        finish()
    }

}