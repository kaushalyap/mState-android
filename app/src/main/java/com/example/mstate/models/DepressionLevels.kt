package com.example.mstate.models

enum class DepressionLevels(val disorderName: String, val score: Int) {
    Undefined("Undefined", -1),
    Not("Not Depressed", 0),
    Other("Some other Depressive Disorder", 1),
    Minimal("Minimal Depression", 2),
    Mild("Major Depressive Disorder with mild levels of depression", 3),
    Moderate("Major Depressive Disorder with moderate depression", 4),
    ModeratelySevere("Major Depressive Disorder with moderately severe depression", 5),
    Severe("Major Depressive Disorder with severe depression", 6)
}