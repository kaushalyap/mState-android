package com.example.mstate.models

enum class EpdsDepressionLevels(val diagnosis: String, val score: Int) {
    Undefined("Undefined", -1),
    Not("Not Depressed", 0),
    NotWithSuicidal(
        "Not Depressed but have suicidal thoughts which can be due to some bereavement",
        0
    ),
    Possible("Possible Depression", 1),
    PossibleWithSuicidal("Possible Depression with suicidal thoughts", 2),
    Severe("Severe Depression", 3),
    SevereWithSuicidal("Severe Depression with suicidal thoughts", 4)
}