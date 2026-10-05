package com.example.smspoc

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Telephony
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

/**
 * Onboarding for the two levels this PoC has.
 *
 * Level 1 — the runtime permission RECEIVE_SMS. That alone is enough for everything the
 * test is about: a copy of every incoming message, delivered to a manifest receiver even
 * when the process is dead.
 *
 * Level 2 — the SMS role. Optional, and much more expensive: the message then reaches us
 * first (SMS_DELIVER) and no other app shows it, so the inbox becomes our problem.
 */
class MainActivity : AppCompatActivity() {

    private val permissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            render()
        }

    private val roleRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // The result code is unreliable across OEM builds; only the state counts.
            render()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnPerm).setOnClickListener { requestMissingPermissions() }
        findViewById<Button>(R.id.btnRole).setOnClickListener { requestSmsRole() }
        findViewById<Button>(R.id.btnRestore).setOnClickListener { openDefaultAppsSettings() }
        findViewById<Button>(R.id.btnPreview).setOnClickListener {
            startActivity(SmsActivity.intentFor(this))
        }

        requestMissingPermissions()
    }

    override fun onResume() {
        super.onResume()
        // Both the permission and the default SMS app may have changed in Settings.
        render()
    }

    // ──────────────────────────── level 1: the permission ────────────────────────────

    private fun hasSmsPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) ==
            PackageManager.PERMISSION_GRANTED

    private fun requestMissingPermissions() {
        val wanted = buildList {
            add(Manifest.permission.RECEIVE_SMS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        val missing = wanted.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) permissions.launch(missing.toTypedArray())
    }

    // ──────────────────────────── level 2: the role ────────────────────────────

    private fun isDefaultSmsApp(): Boolean =
        packageName == Telephony.Sms.getDefaultSmsPackage(this)

    private fun requestSmsRole() {
        if (isDefaultSmsApp()) return

        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rm = getSystemService<RoleManager>()
            // Not a given: a tablet with no telephony has no SMS role to hand out.
            if (rm == null || !rm.isRoleAvailable(RoleManager.ROLE_SMS)) {
                Toast.makeText(this, R.string.no_role, Toast.LENGTH_LONG).show()
                return
            }
            rm.createRequestRoleIntent(RoleManager.ROLE_SMS)
        } else {
            @Suppress("DEPRECATION")
            Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
                .putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
        }

        runCatching { roleRequest.launch(intent) }.onFailure {
            Toast.makeText(this, R.string.no_role, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * The way back. There is no "give the role up" intent, so we open the system's
     * default-apps screen — worth a button of its own, because while this PoC is the
     * SMS app it is the only thing that shows an incoming message.
     */
    private fun openDefaultAppsSettings() {
        val candidates = listOfNotNull(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            } else null,
            Intent(Settings.ACTION_SETTINGS),
        )
        for (intent in candidates) {
            if (runCatching { startActivity(intent) }.isSuccess) return
        }
        Toast.makeText(this, R.string.no_settings, Toast.LENGTH_LONG).show()
    }

    private fun render() {
        val armed = hasSmsPermission()

        findViewById<View>(R.id.onboarding).visibility = if (armed) View.GONE else View.VISIBLE
        findViewById<View>(R.id.controls).visibility = if (armed) View.VISIBLE else View.GONE

        findViewById<TextView>(R.id.statusText).setText(
            when {
                isDefaultSmsApp() -> R.string.status_default
                armed -> R.string.status_armed
                else -> R.string.status_no_perm
            }
        )
        findViewById<TextView>(R.id.statusSub).text =
            getString(R.string.status_count, SmsStore.count(this))
    }
}
