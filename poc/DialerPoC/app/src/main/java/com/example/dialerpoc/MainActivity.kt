package com.example.dialerpoc

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.TelecomManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

/**
 * Onboarding for the one thing this PoC needs: the dialer role.
 *
 * The role is not a permission — there is no runtime dialog and no appops entry. The system
 * shows its own "make this your Phone app?" sheet, and the answer has to be re-read with
 * isRoleHeld() afterwards rather than taken from the result code.
 */
class MainActivity : AppCompatActivity() {

    private val roleRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // The result code is unreliable across OEM builds; only the state counts.
            render()
        }

    private val permissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            render()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnRole).setOnClickListener { requestDialerRole() }
        findViewById<Button>(R.id.btnRestore).setOnClickListener { openDefaultAppsSettings() }
        // Opens the call screen with no call behind it, so the layout can be checked
        // without ringing the phone. Both copies of the button do the same thing.
        val preview = View.OnClickListener { startActivity(CallActivity.intentFor(this)) }
        findViewById<Button>(R.id.btnPreview).setOnClickListener(preview)
        findViewById<Button>(R.id.btnPreviewReady).setOnClickListener(preview)

        requestMissingPermissions()
    }

    override fun onResume() {
        super.onResume()
        // The user may have changed the default phone app in Settings while we were away.
        render()
    }

    // ──────────────────────────── the role ────────────────────────────

    private fun isDefaultDialer(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSystemService<RoleManager>()?.isRoleHeld(RoleManager.ROLE_DIALER) == true
        } else {
            packageName == getSystemService<TelecomManager>()?.defaultDialerPackage
        }

    private fun requestDialerRole() {
        if (isDefaultDialer()) return

        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rm = getSystemService<RoleManager>()
            // Availability is not a given: a tablet with no telephony has no dialer role.
            if (rm == null || !rm.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                Toast.makeText(this, R.string.no_role, Toast.LENGTH_LONG).show()
                return
            }
            rm.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        } else {
            @Suppress("DEPRECATION")
            Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
        }

        runCatching { roleRequest.launch(intent) }.onFailure {
            Toast.makeText(this, R.string.no_role, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * The way back. There is no "give the role up" intent, so we open the system's
     * default-apps screen — worth a button of its own, because while this PoC is the
     * phone app it is the only thing that can answer a call.
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

    /** CALL_PHONE and READ_PHONE_STATE are ordinary runtime permissions, asked once. */
    private fun requestMissingPermissions() {
        val missing = REQUIRED.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) permissions.launch(missing.toTypedArray())
    }

    private fun render() {
        val isDefault = isDefaultDialer()

        findViewById<View>(R.id.onboarding).visibility = if (isDefault) View.GONE else View.VISIBLE
        findViewById<View>(R.id.controls).visibility = if (isDefault) View.VISIBLE else View.GONE

        findViewById<TextView>(R.id.statusText).setText(
            if (isDefault) R.string.status_default else R.string.status_not_default
        )
        findViewById<TextView>(R.id.statusSub).setText(
            if (isDefault) R.string.status_ready else R.string.status_idle
        )
    }

    private companion object {
        val REQUIRED = arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE)
    }
}
