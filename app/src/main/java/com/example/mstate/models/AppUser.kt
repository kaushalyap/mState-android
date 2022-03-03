package com.example.mstate.models

data class AppUser(
    val name: String?,
    val email: String,
    val address: String?,
    val mobileNo: String?,
    val guardian: Guardian?,
    val settings: Settings?
)