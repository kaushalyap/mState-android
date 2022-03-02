package com.example.mstate.services

import android.util.Log
import com.example.mstate.models.HistoryItem
import com.example.mstate.models.Settings
import com.example.mstate.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


const val TAG: String = "FirebaseService"

class FirestoreService {

    private val db = Firebase.firestore

    fun addUser(callback: UserCallback, user: User) {
        var docRef: String? = null
        var userExists = 0

        db.collection("Users")
            .whereEqualTo("email", user.email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    docRef = document.id
                    userExists = if (docRef != null) 1 else 0
                    callback.onCallback(docRef ?: return@addOnSuccessListener)

                    if (userExists == 0) {
                        db.collection("Users")
                            .add(user)
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

    /*  fun readUser(dRef: String): User? {
          var user: User? = null
          db.collection("Users").document(dRef)
              .get()
              .addOnSuccessListener { document ->
                  Log.d(TAG, "User document fetched!")
                  Log.d(TAG, "${document.id} => ${document.data}")
                  user = document.toObject<User>()
              }
              .addOnFailureListener { exception ->
                  Log.w(TAG, "Error getting User document.", exception)
              }
          return user
      }*/

    fun updateSettings(dRef: String, settings: Settings) {
        db.collection("Users")
            .document(dRef)
            .update(
                "settings.emergencyContact", settings.emergencyContact,
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
                val histories: MutableList<HistoryItem>? = mutableListOf()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    histories?.add(
                        HistoryItem(
                            document.getString("date").toString(),
                            document.getString("time").toString(),
                            document.getString("questionnaireType").toString(),
                            document.getDouble("score")?.toInt()!!
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
}