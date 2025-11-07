package com.example.fcmchat.presentation

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effect = viewModel.effect
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
     val navControlller = rememberNavController()

    val granted = remember { mutableStateOf(false) }

    LaunchedEffect(state.isBack) {
        if (state.isBack) {
            navControlller.popBackStack()
        }
    }




    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
       granted.value = isGranted
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }




    LaunchedEffect(granted) {

        if(granted.value){
            effect.collect { eff ->
                when (eff) {
                    is ChatEffect.ShowError -> {
                        snackbarHostState.showSnackbar(eff.message)
                    }
                    is ChatEffect.ShowToast -> {
                        Toast.makeText(context, eff.message, Toast.LENGTH_SHORT).show()
                    }
                    is ChatEffect.MessageSent -> {}
                    is ChatEffect.ScrollToBottom -> {
                        scope.launch {
                            if (state.messages.isNotEmpty()) {
                                listState.animateScrollToItem(state.messages.size - 1)
                            }
                        }
                    }
                }
            }

        }

    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor =  Color(0xFF0A0E27),
        topBar = { ChatTopBar(viewModel) },
        bottomBar = {
            ChatInputBar(
                message = state.messageInput,
                onMessageChange = { viewModel.handleIntent(ChatIntent.UpdateMessageInput(it)) },
                onSendClick = { viewModel.handleIntent(ChatIntent.SendMessage(state.messageInput)) },
                isLoading = state.isLoading
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            items(state.messages, key = { it.id }) { message ->
                MessageBubble(
                    message = message,
                    isCurrentUser = message.senderId == state.currentUserId
                )
            }
        }
    }

}




