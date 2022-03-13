package com.example.mstate.models

import com.google.firebase.Timestamp

data class HistoryItem(
    val timestamp: Timestamp? = null,
    val questionnaireType: String = "",
    val score: Int? = -1,
)