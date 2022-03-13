package com.example.mstate.models

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.UserCallback
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Telephony {
    private val firestoreService = FirestoreService()
    private val auth = Firebase.auth

    fun makeCall(activity: Activity) {
        firestoreService.readUser(object : UserCallback {
            override fun onPostExecute(dRef: String) {}

            override fun onPostExecute(user: AppUser) {
                if (user.guardian?.mobileNo != null) {
                    val intent = Intent(
                        Intent.ACTION_CALL,
                        Uri.parse("tel:${user.guardian.mobileNo}")
                    )
                    activity.startActivity(intent)
                } else {
                    Log.d(TAG, "Emergency contact is empty!")

                }
            }
        }, auth.currentUser?.uid.toString())
    }

    fun sendSMS(context: Context) {
        firestoreService.readUser(object : UserCallback {
            override fun onPostExecute(dRef: String) {}

            override fun onPostExecute(user: AppUser) {
                val smsManager = SmsManager.getDefault()

                if (user.guardian?.mobileNo != null) {
                    val emergencyContactNo = user.guardian.mobileNo
                    try {
                        smsManager.sendTextMessage(
                            emergencyContactNo,
                            null,
                            MESSAGE_BODY,
                            null,
                            null
                        )
                        Log.d(TAG, "SMS sent!")
                    } catch (ex: Exception) {
                        Log.e(TAG, ex.toString())
                    }
                }
            }
        }, auth.currentUser?.uid.toString())
    }


    companion object {
        const val TAG: String = "Telephony"
        const val MESSAGE_BODY: String =
            "Depression detected!, Please take care of the sender. ~ Sent by mState app"
    }
}