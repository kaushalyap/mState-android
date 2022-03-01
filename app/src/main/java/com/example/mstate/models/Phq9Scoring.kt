package com.example.mstate.models

class Phq9Scoring(var answers: Array<QuestionItem>) {
    private var score: Int = 0
    private val sociallyImpaired = answers[8].selected > 1
    private val disorder = (answers[0].selected > 1 || answers[1].selected > 1)

    fun diagnosis(): String {
        for (answer in answers)
            score += answer.selected

        var level = Phq9DepressionLevels.Undefined
        if (score in 0..1)
            level = Phq9DepressionLevels.Not
        else if (score == 2 && disorder && sociallyImpaired)
            level = Phq9DepressionLevels.Other
        else if (score in 3..4 && sociallyImpaired)
            level = Phq9DepressionLevels.Minimal

        /* Major Depressive Disorders */
        else if (score in 5..9 && disorder && sociallyImpaired)
            level = Phq9DepressionLevels.Mild
        else if (score in 10..14 && disorder && sociallyImpaired)
            level = Phq9DepressionLevels.Moderate
        else if (score in 15..19 && disorder && sociallyImpaired)
            level = Phq9DepressionLevels.ModeratelySevere
        else if (score in 20..27 && disorder && sociallyImpaired)
            level = Phq9DepressionLevels.Severe
        return level.disorderName
    }

    fun getScore(): Int = score
}