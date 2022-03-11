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

    fun addUser(callback: UserCallback, appUser: AppUser) {
        db.collection("Users")
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
                        db.collection("Users")
                            .document(appUser.uid ?: return@addOnCompleteListener)
                            .set(appUser)
                            .addOnSuccessListener { _ ->
                                Log.d(TAG, "User document added!")
                                callback.onPostExecute(docRef)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding User document", e)
                            }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }


    fun readUser(callbackReadUser: UserCallback, uid: String) {
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { document ->
                val name = document.getString("name")
                val email = document.getString("email")
                val isProfileComplete = document.getBoolean("profileComplete")
                val user = AppUser(null, name, email!!, isProfileComplete!!, null, null, null, null)
                callbackReadUser.onPostExecute(user)
                Log.d(TAG, "profileComplete = $isProfileComplete")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding User document", e)
            }
    }

    fun updateUser(uid: String, appUser: AppUser) {
        db.collection("Users")
            .document(uid)
            .update(
                "guardian.fullName", appUser.guardian?.fullName,
                "guardian.mobileNo", appUser.guardian?.mobileNo,
                "settings.smsOn", appUser.settings?.smsOn,
                "settings.callOn", appUser.settings?.callOn
            )
            .addOnSuccessListener { Log.d(TAG, "User DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating User document", e) }
    }

    fun updateSettings(uid: String, settings: Settings) {
        db.collection("Users")
            .document(uid)
            .update(
                "settings.smsOn", settings.smsOn,
                "settings.callOn", settings.callOn
            )
            .addOnSuccessListener { Log.d(TAG, "User DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating User document", e) }
    }

    fun addHistoryItem(uid: String, item: HistoryItem) {
        Log.d(TAG, "${item.date} ${item.time} ${item.questionnaireType} ${item.score}")
        db.collection("Users").document(uid)
            .collection("History").add(item)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "History document added!")
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    fun readHistory(callback: HistoryCallback, uid: String) {
        db.collection("Users").document(uid).collection("History")
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

                callback.onPostExecute(histories)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting History documents.", exception)
            }
    }

    fun readLastThreeHistories(callback: HistoryCallback, uid: String) {
        db.collection("Users").document(uid).collection("History")
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
                callback.onPostExecute(histories)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting History documents.", exception)
            }
    }

    companion object {
        const val TAG: String = "FirebaseService"
    }
}