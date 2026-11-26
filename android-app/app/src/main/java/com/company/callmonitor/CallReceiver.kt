package com.company.callmonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.CallLog
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fires on every phone state change (RINGING / OFFHOOK / IDLE).
 * When the state returns to IDLE, the call has just ended, so we
 * read the most recent entry from the phone's own Call Log and
 * push it to Firestore. Android's call log already tells us the
 * type: INCOMING (answered), MISSED, or OUTGOING.
 */
class CallReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "CallMonitor"
        // Change this if you want a different top-level Firestore collection name
        const val COLLECTION = "calls"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        if (state != TelephonyManager.EXTRA_STATE_IDLE) return

        // Small delay isn't needed here because CallLog is usually written
        // by the system before IDLE broadcast fires, but on some OEM phones
        // it can lag slightly. If you notice missed entries, wrap this in
        // a Handler().postDelayed(..., 1500) instead of calling directly.
        readLatestCallAndUpload(context)
    }

    private fun readLatestCallAndUpload(context: Context) {
        val cursor: Cursor? = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC LIMIT 1"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getString(it.getColumnIndexOrThrow(CallLog.Calls._ID))
                val number = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)) ?: "Unknown"
                val typeCode = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                val dateMillis = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DATE))
                val durationSec = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DURATION))

                val callType = when (typeCode) {
                    CallLog.Calls.MISSED_TYPE -> "missed"
                    CallLog.Calls.INCOMING_TYPE -> "incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "outgoing"
                    CallLog.Calls.REJECTED_TYPE -> "missed"
                    else -> "other"
                }

                // Default status when a call first lands in the system.
                // Your team changes this later from the dashboard as they work the call.
                val defaultStatus = when (callType) {
                    "missed" -> "missed"
                    "incoming" -> "open"
                    "outgoing" -> "closed"
                    else -> "open"
                }

                val callerName = lookupContactName(context, number) ?: number

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val data = hashMapOf(
                    "callLogId" to id,
                    "number" to number,
                    "callerName" to callerName,
                    "type" to callType,
                    "status" to defaultStatus,
                    "durationSeconds" to durationSec,
                    "timestamp" to dateMillis,
                    "timestampReadable" to sdf.format(Date(dateMillis)),
                    "notes" to ""
                )

                // Use the call log's own ID as the Firestore document ID.
                // This makes the write idempotent -- if this receiver ever
                // fires twice for the same call, it just overwrites the
                // same document instead of creating a duplicate.
                FirebaseFirestore.getInstance()
                    .collection(COLLECTION)
                    .document(id)
                    .set(data)
                    .addOnSuccessListener { Log.d(TAG, "Uploaded call from $callerName") }
                    .addOnFailureListener { e -> Log.e(TAG, "Upload failed", e) }
            }
        }
    }

    private fun lookupContactName(context: Context, phoneNumber: String): String? {
        return try {
            val uri = android.net.Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                android.net.Uri.encode(phoneNumber)
            )
            context.contentResolver.query(
                uri,
                arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
                null, null, null
            )?.use { c ->
                if (c.moveToFirst()) c.getString(0) else null
            }
        } catch (e: Exception) {
            null
        }
    }
}
