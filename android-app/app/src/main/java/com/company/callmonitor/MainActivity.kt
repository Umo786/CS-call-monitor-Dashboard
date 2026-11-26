package com.company.callmonitor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * This screen only exists to ask for the permissions the app needs
 * (read call log, read phone state, read contacts). Once granted,
 * ALL the real work happens silently in CallReceiver.kt in the
 * background -- you don't need to keep this screen open.
 */
class MainActivity : ComponentActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CONTACTS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(48, 96, 48, 48)

        val status = TextView(this)
        status.textSize = 16f
        layout.addView(status)
        setContentView(layout)

        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, requiredPermissions, 100)
            status.text = "Requesting permissions...\n\nPlease allow Call Logs, Phone and Contacts access so calls can be tracked automatically."
        } else {
            status.text = "✅ Call Monitor is active.\n\nEvery call on this phone is now being logged automatically. You can minimize this app — it keeps working in the background.\n\nOpen the dashboard link on any browser to see live call status."
        }
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        recreate()
    }
}
