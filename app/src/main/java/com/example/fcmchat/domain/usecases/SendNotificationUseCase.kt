package com.example.fcmchat.domain.usecases

import com.example.fcmchat.data.FCMRepositoryImpl
import com.example.fcmchat.data.FCMessage
import com.example.fcmchat.domain.repository.ChatRepository
import javax.inject.Inject

class SendNotificationUseCase @Inject constructor(private val repository: FCMRepositoryImpl) {
    suspend operator fun invoke(
        targetToken: String,
        title: String,
        body: String
    ) = repository.sendMessage(
        targetToken = targetToken,
        title = title,
        body = body
    )
}