package com.example.mstate.models

enum class Phq9DepressionLevels(val disorderName: String) {
    Undefined("Undefined"),
    Not("Not Depressed"),
    Minimal("Minimal Depression"),
    Mild("Other Depressive Disorder with mild levels of depression"),
    Moderate("Major Depressive Disorder with moderate depression"),
    ModeratelySevere("Major Depressive Disorder with moderately severe depression"),
    Severe("Major Depressive Disorder with severe depression")
}