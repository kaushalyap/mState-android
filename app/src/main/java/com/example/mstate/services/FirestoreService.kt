package com.example.mstate.services

import android.util.Log
import com.example.mstate.models.HistoryItem
import com.example.mstate.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

const val TAG: String = "FirebaseService"

class FirestoreService {

    private val db = Firebase.firestore
    private var docRef: String = ""

    fun addUser(user: User): String {
        val userHashMap = hashMapOf(
            "Name" to user.name,
            "Email" to user.email,
            "Emergency_Contact" to user.settings.emergencyContact,
            "Sms_On" to user.settings.smsOn,
            "Call_On" to user.settings.callOn
        )
        db.collection("Users")
            .add(userHashMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "User document added!")
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                docRef = documentReference.id
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding User document", e)
            }
        return docRef
    }

    fun readUser(dRef: String): User? {
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
    }

    fun updateEmergencyContact(dRef: String, eContact: String) {
        db.collection("Users")
            .document(dRef)
            .update("emergencyContact", eContact)
            .addOnSuccessListener { Log.d(TAG, "User DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating User document", e) }
    }

    fun addHistoryItem(dRef: String, item: HistoryItem) {
        val historyHashMap = hashMapOf(
            "Date" to item.date,
            "Time" to item.time,
            "Questionnaire_Type" to item.questionnaireType,
            "Score" to item.score,
            "Diagnosis" to item.diagnosis
        )
        db.collection("Users").document(dRef)
            .collection("History").add(historyHashMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "History document added!")
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    fun readHistory(dRef: String): List<HistoryItem> {
        val histories = mutableListOf<HistoryItem>()
        db.collection("Users").document(dRef).collection("History")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    histories.add(document.toObject<HistoryItem>())
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting History documents.", exception)
            }
        return histories
    }
}