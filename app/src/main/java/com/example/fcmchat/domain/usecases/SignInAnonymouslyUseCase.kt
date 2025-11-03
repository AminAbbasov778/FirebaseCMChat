package com.example.fcmchat.domain.usecases

import com.example.fcmchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class SignInAnonymouslyUseCase @Inject constructor(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(): Result<String> {
        return firebaseRepository.signInAnonymously()
    }

}