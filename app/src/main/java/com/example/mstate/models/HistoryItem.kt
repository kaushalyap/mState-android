package com.example.mstate.models

data class HistoryItem(
    val timestamp: String = "",
    val questionnaireType: String = "",
    val score: Int? = -1,
)