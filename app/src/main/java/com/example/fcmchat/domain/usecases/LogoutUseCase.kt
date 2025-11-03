package com.example.fcmchat.domain.usecases

import com.example.fcmchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke() = firebaseRepository.logout()

}