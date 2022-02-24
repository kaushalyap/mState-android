package com.example.mstate.models

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.mstate.R

const val TAG: String = "AutomatedResponse"

class AutomatedResponse {

    private var responded = booleanArrayOf(false, false, false, false)

    fun respond(activity: Activity, context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val smsOn = sharedPreferences.getBoolean(
            context.resources.getString(R.string.pref_sms),
            false
        )
        Log.d(TAG, "SMS ON : $smsOn")
        val callOn = sharedPreferences.getBoolean(
            context.resources.getString(R.string.pref_call),
            false
        )
        Log.d(TAG, "Call ON : $callOn")

        val telephony = Telephony()
        if (smsOn) {
            telephony.sendSMS(context)
            responded[1] = true
            Log.d(TAG, "SMS response : ${responded[1]}")
        }
        if (callOn) {
            telephony.makeCall(activity)
            responded[2] = true
            Log.d(TAG, "Call response : ${responded[2]}")
        }
    }
}