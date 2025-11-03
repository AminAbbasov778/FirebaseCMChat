package com.example.fcmchat.data


data class FcmMessageRequest(
    val message: Message
) {
    data class Message(
        val token: String,
        val notification: Notification,
        val data: Map<String, String> = emptyMap()
    )

    data class Notification(
        val title: String,
        val body: String
    )
}
