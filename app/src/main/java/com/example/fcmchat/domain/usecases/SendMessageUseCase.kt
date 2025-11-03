package com.example.fcmchat.domain.usecases

import com.example.fcmchat.domain.model.Message
import com.example.fcmchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    suspend operator fun invoke(text: String, senderId: String, senderName: String,token : String): Result<Unit> {
        val message = Message(
            id = System.currentTimeMillis().toString(),
            text = text,
            senderId = senderId,
            senderName = senderName,
            fcmToken = token,
            timestamp = System.currentTimeMillis()
        )
        return repository.sendMessage(message)
    }
}