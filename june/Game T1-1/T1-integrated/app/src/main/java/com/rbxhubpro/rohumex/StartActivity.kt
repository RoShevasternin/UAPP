package com.rbxhubpro.rohumex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rbxhubpro.rohumex.util.log

private var onCreateCounter = 0

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        onCreateCounter++
        log("StartActivity: $onCreateCounter")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // правка 7: диплинк возврата (rohumexapp://reward) приземляется сюда —
        // пробрасываем data в MainActivity, обработка там (claimWebCoins)
        startActivity(Intent(this, MainActivity::class.java).also {
            it.data = intent?.data
            intent?.extras?.let { e -> it.putExtras(e) }   // route/gate_pl з message.data
        })
        finish()
    }

}