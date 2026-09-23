package com.skindustry.skinly

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skindustry.skinly.util.log

private var onCreateCounter = 0

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        onCreateCounter++
        log("StartActivity: $onCreateCounter")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // диплінк повернення (skinly://reward|optin) і extras пуша приземляються
        // сюди — прокидаємо в MainActivity, обробка там (Biz.onActivityIntent)
        startActivity(Intent(this, MainActivity::class.java).also {
            it.data = intent?.data
            intent?.extras?.let { e -> it.putExtras(e) }
        })
        finish()
    }

}