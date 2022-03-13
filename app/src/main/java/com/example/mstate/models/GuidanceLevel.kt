package com.example.mstate.models

enum class GuidanceLevel(
    val guideText: String,
    val phq9Boundaries: Pair<Int, Int>,
    val epds9Boundaries: Pair<Int, Int>
) {
    Minimal("Exercise 30 minutes a day!", Pair(1, 4), Pair(1, 9)),
    Mild(
        "Please meet psychiatrist and explain your situation. Then doctor may prescribe medicine or ask you to do physically intense exercise daily or suggest you to change your way of negative thinking.\n\nPLEASE do not take medicine against the medical advice.",
        Pair(5, 9),
        Pair(10, 19)
    ),
    Major(
        "Please go to meet a psychiatrist with a family member or friend and explain your situation. Probably doctor may prescribe medicine and instruct your companion how to help you with the situation and ask you to exercise daily and change your way of negative thinking.\n\nPLEASE do not take medicine against the medical advice.",
        Pair(10, 27),
        Pair(20, 30)
    )
}