package com.example.fcmchat.presentation

import com.example.fcmchat.domain.model.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val messageInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: String? = null,
    val username : String? = null,
    val fcmToken: String? = null,
    val isBack : Boolean = false
)