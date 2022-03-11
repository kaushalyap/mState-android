package com.example.mstate.models

data class AppUser(
    val uid: String?,
    val name: String?,
    val email: String,
    val profileComplete: Boolean,
    val address: String?,
    val mobileNo: String?,
    val guardian: Guardian?,
    val settings: Settings?
)