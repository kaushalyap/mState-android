package com.example.mstate.models

enum class EpdsDepressionLevels(val diagnosis: String) {
    Undefined("Undefined"),
    Not("Not Depressed"),
    NotWithSuicidal(
        "Not Depressed but have suicidal thoughts which can be due to some bereavement"
    ),
    Possible("Possible Depression"),
    PossibleWithSuicidal("Possible Depression with suicidal thoughts"),
    Severe("Severe Depression"),
    SevereWithSuicidal("Severe Depression with suicidal thoughts")
}