package com.example.mstate.services

import com.example.mstate.models.HistoryItem

interface HistoryCallback {
    fun onPostExecute(histories: List<HistoryItem>?)
}