package com.company.callmonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Manifest-registered receivers are automatically re-armed by Android
 * after a reboot once the app has been launched at least once, so this
 * class doesn't need to do anything -- it just needs to exist and be
 * declared in the manifest so the OS knows CallReceiver should keep
 * listening after the phone restarts.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // No action needed -- see class comment above.
    }
}
