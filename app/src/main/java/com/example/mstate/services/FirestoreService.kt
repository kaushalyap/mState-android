package com.example.mstate.services

import android.annotation.SuppressLint
import android.util.Log
import com.example.mstate.models.AppUser
import com.example.mstate.models.Guardian
import com.example.mstate.models.HistoryItem
import com.example.mstate.models.Settings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



@SuppressLint("LogConditional")
class FirestoreService {

    private val db = Firebase.firestore

    fun addUser(callback: UserCallback, appUser: AppUser) {
        db.collection(USER_COLLECTION)
            .whereEqualTo("email", appUser.email).get()
            .addOnCompleteListener { task ->
                var docRef = ""
                val userExists: Int

                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.exists()) {
                            Log.d(TAG, document.id + " => " + document.data)
                            docRef = document.id
                        }
                    }
                    userExists = if (docRef.isNotEmpty()) 1 else 0

                    if (userExists == 0) {
                        db.collection(USER_COLLECTION)
                            .document(appUser.uid.toString())
                            .set(appUser)
                            .addOnSuccessListener {
                                Log.d(TAG, "User document added!")
                                callback.onPostExecute(docRef)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding User document", e)
                            }
                    } else
                        Log.d(TAG, "User already exists!")
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

    fun readUser(callbackReadUser: UserCallback, uid: String) {
        db.collection(USER_COLLECTION).document(uid).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val document = task.result
                    val name = document.getString("name")
                    val email = document.getString("email")
                    val address = document.getString("address")
                    val mobileNo = document.getString("mobileNo")
                    val isProfileComplete = document.getBoolean("profileComplete")
                    val guardianName = document.getString("guardian.fullName").toString()
                    val guardianMobile = document.getString("guardian.mobileNo").toString()
                    val smsOn = document.getBoolean("settings.smsOn")
                    val callOn = document.getBoolean("settings.callOn")

                    val guardian = Guardian(guardianName, guardianMobile)
                    val settings = smsOn?.let { callOn?.let { it1 -> Settings(it, it1) } }
                    val user = isProfileComplete?.let {
                        AppUser(
                            null,
                            name,
                            email!!,
                            it,
                            address,
                            mobileNo,
                            guardian,
                            settings
                        )
                    }
                    user?.let { callbackReadUser.onPostExecute(it) }
                    Log.d(TAG, "profileComplete = $isProfileComplete")
                } else {
                    Log.d(TAG, "Reading user failed: ${task.exception}")
                }

            }
    }

    fun updateUser(uid: String, appUser: AppUser) {
        db.collection(USER_COLLECTION)
            .document(uid)
            .update(
                "name", appUser.name,
                "address", appUser.address,
                "mobileNo", appUser.mobileNo,
                "guardian.fullName", appUser.guardian?.fullName,
                "guardian.mobileNo", appUser.guardian?.mobileNo,
                "settings.smsOn", appUser.settings?.smsOn,
                "settings.callOn", appUser.settings?.callOn
            )
            .addOnSuccessListener { Log.d(TAG, "User DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating User document", e) }
    }

    fun updateSettings(uid: String, settings: Settings) {
        db.collection(USER_COLLECTION)
            .document(uid)
            .update(
                "settings.smsOn", settings.smsOn,
                "settings.callOn", settings.callOn
            )
            .addOnSuccessListener { Log.d(TAG, "User DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating User document", e) }
    }

    fun addHistoryItem(uid: String, item: HistoryItem) {
        Log.d(TAG, "${item.timestamp} ${item.questionnaireType} ${item.score}")
        db.collection(USER_COLLECTION).document(uid)
            .collection(HISTORY_COLLECTION).add(item)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "History document added!")
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    fun readHistory(callback: HistoryCallback, uid: String) {
        db.collection(USER_COLLECTION).document(uid).collection(HISTORY_COLLECTION)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val histories: MutableList<HistoryItem> = mutableListOf()
                    for (document in task.result) {
                        if (document.exists()) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            histories.add(
                                HistoryItem(
                                    document.getTimestamp(TIMESTAMP_FIELD),
                                    document.getString(QUESTION_TYPE_FIELD).toString(),
                                    document.getDouble(SCORE_FIELD)?.toInt()
                                )
                            )
                        }
                        Log.d(TAG, "$histories")
                    }

                    callback.onPostExecute(histories)
                } else
                    Log.d(TAG, "Reading histories failed: ${task.exception}")
            }
    }

    fun readLastThreeHistories(callback: HistoryCallback, uid: String) {
        db.collection(USER_COLLECTION).document(uid).collection(HISTORY_COLLECTION)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnCompleteListener { task ->
                val histories: MutableList<HistoryItem> = mutableListOf()
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.exists()) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            histories.add(
                                HistoryItem(
                                    document.getTimestamp(TIMESTAMP_FIELD),
                                    document.getString(QUESTION_TYPE_FIELD).toString(),
                                    document.getDouble(SCORE_FIELD)?.toInt()
                                )
                            )
                            Log.d(TAG, "$histories")
                        } else
                            Log.d(TAG, "No any history  documents!")
                    }
                    callback.onPostExecute(histories)
                } else
                    Log.d(TAG, "Reading last three histories failed: ${task.exception}")
            }
    }

    companion object {
        const val TAG: String = "FirebaseService"
        const val USER_COLLECTION: String = "Users"
        const val HISTORY_COLLECTION: String = "History"
        const val TIMESTAMP_FIELD: String = "timestamp"
        const val QUESTION_TYPE_FIELD: String = "questionnaireType"
        const val SCORE_FIELD: String = "score"
    }
}