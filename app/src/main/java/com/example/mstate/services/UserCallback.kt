package com.example.mstate.services

import com.example.mstate.models.AppUser

interface UserCallback {
    fun onPostExecute(dRef: String)
    fun onPostExecute(user: AppUser)
}
