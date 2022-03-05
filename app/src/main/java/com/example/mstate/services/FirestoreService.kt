package com.example.mstate.services

import android.annotation.SuppressLint
import android.util.Log
import com.example.mstate.models.AppUser
import com.example.mstate.models.HistoryItem
import com.example.mstate.models.Settings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@SuppressLint("LogConditional")
class FirestoreService {

    private val db = Firebase.firestore

    fun doesUserAlreadyExists(callback: UserCallback, email: String) {
        db.collection("Users")
            .whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docRef = document.id
                    callback.onCallback(docRef)
                    Log.d(TAG, "docRef = $docRef")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding User document", e)
            }
    }


    fun addUser(callback: UserCallback, appUser: AppUser) {
        var docRef: String
        var userExists: Int

        db.collection("Users")
            .whereEqualTo("email", appUser.email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    docRef = document.id
                    userExists = if (docRef.isNotEmpty()) 1 else 0
                    callback.onCallback(docRef)

                    if (userExists == 0) {
                        db.collection("Users")
                            .add(appUser)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "User document added!")
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot added with ID: ${documentReference.id}"
                                )

                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding User document", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding User document", e)
            }
    }

    fun updateUser(dRef: String, appUser: AppUser) {
        db.collection("Users")
            .document(dRef)
            .update(
                "guardian.fullName", appUser.guardian?.fullName,
                "guardian.mobileNo", appUser.guardian?.mobileNo,
                "settings.smsOn", appUser.settings?.smsOn,
                "settings.callOn", appUser.settings?.callOn
            )
            .addOnSuccessListener { Log.d(TAG, "User DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating User document", e) }
    }

    fun updateSettings(dRef: String, settings: Settings) {
        db.collection("Users")
            .document(dRef)
            .update(
                "settings.smsOn", settings.smsOn,
                "settings.callOn", settings.callOn
            )
            .addOnSuccessListener { Log.d(TAG, "User DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating User document", e) }
    }

    fun addHistoryItem(dRef: String, item: HistoryItem) {
        Log.d(TAG, "${item.date} ${item.time} ${item.questionnaireType} ${item.score}")
        db.collection("Users").document(dRef)
            .collection("History").add(item)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "History document added!")
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    fun readHistory(callback: HistoryCallback, dRef: String) {
        db.collection("Users").document(dRef).collection("History")
            .get()
            .addOnSuccessListener { result ->
                val histories: MutableList<HistoryItem> = mutableListOf()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    histories.add(
                        HistoryItem(
                            document.getString("date").toString(),
                            document.getString("time").toString(),
                            document.getString("questionnaireType").toString(),
                            document.getDouble("score")?.toInt() ?: return@addOnSuccessListener
                        )
                    )
                    Log.d(TAG, "$histories")
                }

                callback.onCallback(histories)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting History documents.", exception)
            }
    }

    fun readLastThreeHistories(callback: HistoryCallback, dRef: String) {
        db.collection("Users").document(dRef).collection("History")
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                val histories: MutableList<HistoryItem> = mutableListOf()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    histories.add(
                        HistoryItem(
                            document.getString("date").toString(),
                            document.getString("time").toString(),
                            document.getString("questionnaireType").toString(),
                            document.getDouble("score")?.toInt() ?: return@addOnSuccessListener
                        )
                    )
                    Log.d(TAG, "$histories")
                }

                callback.onCallback(histories)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting History documents.", exception)
            }
    }


    companion object {
        const val TAG: String = "FirebaseService"
    }
}