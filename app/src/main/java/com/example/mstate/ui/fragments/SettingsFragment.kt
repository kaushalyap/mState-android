package com.example.mstate.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.mstate.R
import com.example.mstate.ui.activities.MainActivity
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

const val CONTACT_PICKER_RESULT: Int = 1

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mainActivity: MainActivity
    private lateinit var prefEmergencyContact: Preference
    private lateinit var callPermissionsRequester: PermissionsRequester
    private lateinit var smsPermissionsRequester: PermissionsRequester

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        init()
    }

    private fun init() {
        setupPreferenceListeners()
        mainActivity = activity as MainActivity
        setEmergencyContactSummary()
    }

    private fun setEmergencyContactSummary() {
        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val savedNumber =
            sharedPreferences.getString(context?.getString(R.string.pref_emergency_contact), "")
                .toString()

        prefEmergencyContact =
            (findPreference(getString(R.string.pref_emergency_contact)) ?: return)

        prefEmergencyContact.summaryProvider =
            Preference.SummaryProvider<Preference> {
                if (savedNumber == "")
                    "Who to contact when drunk"
                else
                    savedNumber
            }
    }


    private fun setupPreferenceListeners() {

        setSharedPreferenceChangeListeners()

        prefEmergencyContact = (findPreference(getString(R.string.pref_emergency_contact))
            ?: return)
        prefEmergencyContact.setOnPreferenceClickListener {
            val contactPickerIntent = Intent(Intent.ACTION_PICK)
            contactPickerIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT)
            true
        }

    }

    private fun setSharedPreferenceChangeListeners() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
                when {
                    key.equals(getString(R.string.pref_sms)) -> {
                        if (prefs.getBoolean(getString(R.string.pref_sms), false))
                            smsPermissionsRequester.launch()
                    }
                    key.equals(getString(R.string.pref_call)) -> {
                        if (prefs.getBoolean(getString(R.string.pref_call), false))
                            callPermissionsRequester.launch()
                    }
                }
            }
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONTACT_PICKER_RESULT && resultCode == Activity.RESULT_OK) {

            val contactUri = data?.data
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val cursor = contactUri?.let {
                context?.contentResolver?.query(
                    it, projection,
                    null, null, null
                )
            }

            if (cursor != null && cursor.moveToFirst()) {
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val number = cursor.getString(numberIndex)
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
                with(sharedPref.edit()) {
                    putString(getString(R.string.pref_emergency_contact), number)
                    apply()
                }

                prefEmergencyContact.summaryProvider =
                    Preference.SummaryProvider<Preference> {
                        number
                    }
            }
            cursor?.close()
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

}