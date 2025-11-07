package com.example.arthas.service

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object MessageRepository {
    private val _messages = mutableStateOf<List<String>>(emptyList())
    var messages: List<String> by _messages

    fun addMessage(message: String) {
        _messages.value = _messages.value + message
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }
}