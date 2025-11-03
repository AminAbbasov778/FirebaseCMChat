package com.example.fcmchat.di

import android.content.Context
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.example.fcmchat.data.FCMRepositoryImpl
import com.example.fcmchat.data.FcmApi
import com.example.fcmchat.data.FirebaseRepositoryImpl
import com.example.fcmchat.domain.repository.ChatRepository
import com.example.fcmchat.domain.repository.FcmRepository
import com.example.fcmchat.domain.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent :: class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirebaseRepository(firestore: FirebaseFirestore, firebaseMessaging: FirebaseMessaging,firebaseAuth: FirebaseAuth): FirebaseRepository {
        return FirebaseRepositoryImpl(firestore,firebaseMessaging,firebaseAuth)
    }
    @Provides
    @Singleton
    fun provideFCMRepository(@ApplicationContext context: Context, api: FcmApi): FcmRepository {
        return FCMRepositoryImpl(context, api)

    }



}