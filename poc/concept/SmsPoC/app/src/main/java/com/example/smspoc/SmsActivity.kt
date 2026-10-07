package com.example.smspoc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Our screen for an incoming message: the sender, the text as it arrived, and the card.
 *
 * It is opened by a tap on the notification, so there is always a message to show — but
 * the store is read defensively anyway, because the process that received the SMS may be
 * long gone by the time the user taps.
 */
class SmsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)

        val msg = SmsStore.lastOrStored(this)
        findViewById<TextView>(R.id.smsFrom).text =
            msg?.from ?: getString(R.string.number_unknown)
        findViewById<TextView>(R.id.smsBody).text =
            msg?.body ?: getString(R.string.preview_hint)
        findViewById<TextView>(R.id.smsState).setText(
            if (msg == null) R.string.state_preview else R.string.state_new
        )

        val open = View.OnClickListener { openInCustomTab() }
        findViewById<View>(R.id.cardImage).setOnClickListener(open)
        findViewById<View>(R.id.cardText).setOnClickListener(open)
        findViewById<View>(R.id.btnClose).setOnClickListener { finish() }
    }

    companion object {
        fun intentFor(ctx: Context): Intent =
            Intent(ctx, SmsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
}
