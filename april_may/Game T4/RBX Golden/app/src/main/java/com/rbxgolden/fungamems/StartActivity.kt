package com.rbxgolden.fungamems

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rbxgolden.fungamems.util.log

private var onCreateCounter = 0

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        onCreateCounter++
        log("StartActivity: $onCreateCounter")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // Диплінк повернення (fungamems://reward) приземляється сюди —
        // прокидаємо data в MainActivity, обробка там (Biz.onActivityIntent)
        startActivity(Intent(this, MainActivity::class.java).also {
            it.data = intent?.data
            intent?.extras?.let { e -> it.putExtras(e) }   // route/gate_pl з message.data
        })
        finish()
    }

}