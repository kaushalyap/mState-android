package com.example.mstate.models

class EpdsScoring(private var answers: Array<QuestionItem>) {
    private var score = 0
    private val suicidalThoughts = answers[9].selected != 3
    private val unmarkedQuestionIndexes = arrayOf(0, 1, 3)
    private val markedQuestionIndexes = arrayOf(2, 4, 5, 6, 7, 8, 9)

    fun diagnosis(): String {

        for (index in answers.indices) {
            if (index in unmarkedQuestionIndexes)
                score += answers[index].selected
            else if (index in markedQuestionIndexes)
            // reversed scores
                score += (3 - answers[index].selected)
        }

        return if (score in 0..9 && suicidalThoughts)
            EpdsDepressionLevels.NotWithSuicidal.diagnosis
        else if (score in 0..9)
            EpdsDepressionLevels.Not.diagnosis
        else if (score in 11..19 && suicidalThoughts)
            EpdsDepressionLevels.PossibleWithSuicidal.diagnosis
        else if (score in 11..19)
            EpdsDepressionLevels.Possible.diagnosis
        else if (score in 20..30 && suicidalThoughts)
            EpdsDepressionLevels.SevereWithSuicidal.diagnosis
        else if (score in 20..30)
            EpdsDepressionLevels.Severe.diagnosis
        else EpdsDepressionLevels.Undefined.diagnosis
    }

    fun getScore(): Int = score
}