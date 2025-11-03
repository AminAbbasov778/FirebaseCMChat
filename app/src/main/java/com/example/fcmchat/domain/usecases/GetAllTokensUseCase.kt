package com.example.fcmchat.domain.usecases

import com.example.fcmchat.data.FirebaseRepositoryImpl
import javax.inject.Inject

class GetAllTokensUseCase @Inject constructor(private val firebaseRepositoryImpl: FirebaseRepositoryImpl) {
    suspend operator fun invoke() = firebaseRepositoryImpl.getUserTokens()

}