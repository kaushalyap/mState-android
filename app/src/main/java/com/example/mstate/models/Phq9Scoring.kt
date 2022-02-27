package com.example.mstate.models

class Phq9Scoring(var answers: Array<QuestionItem>) {
    private var score: Int = 0
    private val sociallyImpaired = answers[8].selected > 1
    private val disorder = (answers[0].selected > 1 || answers[1].selected > 1)

    fun diagnosis(): String {
        for (answer in answers)
            score += answer.selected

        var level = DepressionLevels.Undefined
        if (score <= 1)
            level = DepressionLevels.Not
        else if (score <= 2 && disorder && sociallyImpaired)
            level = DepressionLevels.Other
        else if (score <= 4 && sociallyImpaired)
            level = DepressionLevels.Minimal

        /* Major Depressive Disorders */
        else if (score <= 9 && disorder && sociallyImpaired)
            level = DepressionLevels.Mild
        else if (score <= 14 && disorder && sociallyImpaired)
            level = DepressionLevels.Moderate
        else if (score <= 19 && disorder && sociallyImpaired)
            level = DepressionLevels.ModeratelySevere
        else if (score <= 27 && disorder && sociallyImpaired)
            level = DepressionLevels.Severe
        return level.disorderName
    }
}