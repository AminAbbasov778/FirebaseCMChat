package com.example.fcmchat.domain.usecases

import com.example.fcmchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(): String? {
        return firebaseRepository.getCurrentUserId()

    }
}