package com.example.fcmchat.domain.model

data class Message(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val fcmToken: String = "")