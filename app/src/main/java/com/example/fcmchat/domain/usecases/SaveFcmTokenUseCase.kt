package com.example.fcmchat.domain.usecases

import com.example.fcmchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class SaveFcmTokenUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
  operator  suspend fun invoke(userId: String, token: String): Result<Unit> {
        return repository.saveFcmToken(userId, token)
    }
}