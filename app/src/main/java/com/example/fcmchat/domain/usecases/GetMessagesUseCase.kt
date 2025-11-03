package com.example.fcmchat.domain.usecases

import com.example.fcmchat.domain.model.Message
import com.example.fcmchat.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    operator suspend  fun invoke(): Flow<List<Message>> = repository.getMessages()
}