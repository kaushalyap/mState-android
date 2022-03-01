package com.example.mstate.models

data class HistoryItem(
    val date: String,
    val time: String,
    val questionnaireType: String,
    val score: Int,
)