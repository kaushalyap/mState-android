package com.example.mstate.models

data class QuestionItem(
    val questionNo: String,
    val question: String,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    val answer4: String,
    var selected: Int,
    val errorMessage: String
)