package com.example.mstate.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.mstate.R
import com.example.mstate.models.Settings
import com.example.mstate.services.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

@SuppressLint("LogConditional")
class SettingsFragment : PreferenceFragmentCompat(),
    PreferenceManager.OnPreferenceTreeClickListener {

    private lateinit var callPermissionsRequester: PermissionsRequester
    private lateinit var smsPermissionsRequester: PermissionsRequester
    private lateinit var firestoreService: FirestoreService
    private lateinit var auth: FirebaseAuth

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        init()
    }

    private fun init() {
        auth = Firebase.auth
        setSharedPreferenceChangeListeners()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            resources.getString(R.string.pref_disclaimer) -> findNavController().navigate(R.id.action_settings_to_disclaimer)
            resources.getString(R.string.pref_about) -> findNavController().navigate(R.id.action_settings_to_about)
            resources.getString(R.string.pref_sign_out) -> {
                auth.signOut()
                findNavController().navigate(R.id.action_global_signIn)
            }
        }
        return true
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


    private fun saveSettingsOnExit() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val callOn = sharedPref.getBoolean(getString(R.string.pref_call), false)
        val smsOn = sharedPref.getBoolean(getString(R.string.pref_sms), false)

        Log.d(TAG, "callOn : $callOn, smsOn : $smsOn")
        val settings = Settings(smsOn, callOn)

        firestoreService = FirestoreService()

        firestoreService.updateSettings(auth.uid.toString(), settings)
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
        showPermissionRationaleDialog(R.string.permission_call_rationale, request)
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
        showPermissionRationaleDialog(R.string.permission_call_rationale, request)
    }

    private fun showPermissionRationaleDialog(
        @StringRes messageResId: Int,
        request: PermissionRequest
    ) {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.allow) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.deny) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        saveSettingsOnExit()
    }

    companion object {
        const val TAG: String = "SettingsFragment"
    }


}