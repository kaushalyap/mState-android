package com.example.mstate.models

class Guider(private val lastList: List<HistoryItem>) {

    fun generateGuidelines(): String {
        var phq9Increasing = false
        var epdsIncreasing = false

        val lastItemScore = lastList[0].score
        var beforeLastItemScore = -1
        if (lastList.size >= 2) {
            beforeLastItemScore = lastList[1].score!!
        }
        val testType = lastList[0].questionnaireType

        var guidelines = ""

        if (testType == QuestionnaireType.PHQ9.name) {
            val isInPhq9MinimalRange =
                lastItemScore in GuidanceLevel.Minimal.phq9Boundaries.first..GuidanceLevel.Minimal.phq9Boundaries.second
            val isInPhq9MildRange =
                lastItemScore in GuidanceLevel.Mild.phq9Boundaries.first..GuidanceLevel.Mild.phq9Boundaries.second
            val isInPhq9MajorRange =
                lastItemScore in GuidanceLevel.Major.phq9Boundaries.first..GuidanceLevel.Major.phq9Boundaries.second

            if (lastItemScore != null && beforeLastItemScore != -1) {
                if (lastItemScore >= beforeLastItemScore) {
                    phq9Increasing = true
                    if (phq9Increasing) {
                        when {
                            isInPhq9MinimalRange -> guidelines = GuidanceLevel.Minimal.guideText
                            isInPhq9MildRange -> guidelines = GuidanceLevel.Mild.guideText
                            isInPhq9MajorRange -> guidelines = GuidanceLevel.Major.guideText
                        }
                    }
                }
            } else {
                when {
                    isInPhq9MinimalRange -> guidelines = GuidanceLevel.Minimal.guideText
                    isInPhq9MildRange -> guidelines = GuidanceLevel.Mild.guideText
                    isInPhq9MajorRange -> guidelines = GuidanceLevel.Major.guideText
                }
            }
        } else {
            val isInEpdsMinimalRange =
                lastItemScore in GuidanceLevel.Minimal.epds9Boundaries.first..GuidanceLevel.Minimal.epds9Boundaries.second
            val isInEpdsMildRange =
                lastItemScore in GuidanceLevel.Mild.epds9Boundaries.first..GuidanceLevel.Mild.epds9Boundaries.second
            val isInEpdsMajorRange =
                lastItemScore in GuidanceLevel.Major.epds9Boundaries.first..GuidanceLevel.Major.epds9Boundaries.second
            if (lastItemScore != null && beforeLastItemScore != -1) {
                if (lastItemScore >= beforeLastItemScore) {
                    epdsIncreasing = true
                    if (phq9Increasing) {
                        when {
                            isInEpdsMinimalRange -> guidelines = GuidanceLevel.Minimal.guideText
                            isInEpdsMildRange -> guidelines = GuidanceLevel.Mild.guideText
                            isInEpdsMajorRange -> guidelines = GuidanceLevel.Major.guideText
                        }
                    }
                }
            }
            when {
                isInEpdsMinimalRange -> guidelines = GuidanceLevel.Minimal.guideText
                isInEpdsMildRange -> guidelines = GuidanceLevel.Mild.guideText
                isInEpdsMajorRange -> guidelines = GuidanceLevel.Major.guideText
            }
        }
        return guidelines
    }
}