package com.example.fcmchat.domain.usecases

import com.example.fcmchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetFcmTokenUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.getFcmToken()
    }
}