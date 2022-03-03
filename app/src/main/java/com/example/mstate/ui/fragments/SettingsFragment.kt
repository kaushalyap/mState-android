package com.example.mstate.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.mstate.R
import com.example.mstate.models.Settings
import com.example.mstate.services.FirestoreService
import com.example.mstate.ui.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest


class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mainActivity: MainActivity
    private lateinit var prefEmergencyContact: Preference
    private lateinit var callPermissionsRequester: PermissionsRequester
    private lateinit var smsPermissionsRequester: PermissionsRequester
    private lateinit var firestoreService: FirestoreService
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        init()
    }

    private fun init() {
        mainActivity = activity as MainActivity
        setSharedPreferenceChangeListeners()
    }

    private fun setSharedPreferenceChangeListeners() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
                when {
                    key.equals(getString(R.string.pref_sms)) -> {
                        if (prefs.getBoolean(getString(R.string.pref_sms), false)) {
                            smsPermissionsRequester.launch()
                            with(sharedPref.edit()) {
                                putBoolean(getString(R.string.pref_sms), true)
                                apply()
                            }
                        } else {
                            with(sharedPref.edit()) {
                                putBoolean(getString(R.string.pref_sms), false)
                                apply()
                            }
                        }
                    }
                    key.equals(getString(R.string.pref_call)) -> {
                        if (prefs.getBoolean(getString(R.string.pref_call), false)) {
                            callPermissionsRequester.launch()
                            with(sharedPref.edit()) {
                                putBoolean(getString(R.string.pref_call), true)
                                apply()
                            }
                        } else {
                            with(sharedPref.edit()) {
                                putBoolean(getString(R.string.pref_call), false)
                                apply()
                            }
                        }
                    }
                }
            }
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }


    // pref_emergency_contact

    @SuppressLint("LogConditional")
    private fun saveSettingsOnExit() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val docRef = sharedPref.getString(getString(R.string.pref_user_doc_ref), null)
        val callOn = sharedPref.getBoolean(getString(R.string.pref_call), false)
        val smsOn = sharedPref.getBoolean(getString(R.string.pref_sms), false)

        Log.d(TAG, "callOn : $callOn, smsOn : $smsOn")
        val settings = Settings(smsOn, callOn)


        firestoreService = FirestoreService()

        if (docRef != null) {
            firestoreService.updateSettings(docRef, settings)
        } else {
            firebaseAuth.signOut()
            findNavController().navigate(R.id.action_global_signIn)
        }
    }


    private fun sendSMS() {}
    private fun makeCall() {}

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callPermissionsRequester = constructPermissionsRequest(
            Manifest.permission.CALL_PHONE,
            onShowRationale = ::onCallShowRationale,
            onPermissionDenied = ::onCallDenied,
            onNeverAskAgain = ::onCallNeverAskAgain,
            requiresPermission = ::makeCall
        )
        smsPermissionsRequester = constructPermissionsRequest(
            Manifest.permission.SEND_SMS,
            onShowRationale = ::onSmsShowRationale,
            onPermissionDenied = ::onSmsDenied,
            onNeverAskAgain = ::onSmsNeverAskAgain,
            requiresPermission = ::sendSMS
        )
    }

    private fun onSmsNeverAskAgain() {
        Toast.makeText(
            context,
            R.string.permission_sms_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onSmsDenied() {
        Toast.makeText(context, R.string.permission_sms_denied, Toast.LENGTH_SHORT).show()
    }

    private fun onSmsShowRationale(request: PermissionRequest) {
        mainActivity.showPermissionRationaleDialog(R.string.permission_call_rationale, request)
    }

    private fun onCallNeverAskAgain() {
        Toast.makeText(
            context,
            R.string.permission_call_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onCallDenied() {
        Toast.makeText(context, R.string.permission_call_denied, Toast.LENGTH_SHORT).show()
    }

    private fun onCallShowRationale(request: PermissionRequest) {
        mainActivity.showPermissionRationaleDialog(R.string.permission_call_rationale, request)
    }

    override fun onDestroy() {
        super.onDestroy()
        saveSettingsOnExit()
    }

    companion object {
        const val TAG: String = "SettingsFragment"
    }
}