package com.example.fcmchat.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fcmchat.domain.model.Message

@Composable
fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean
) {
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
    val bubbleColor by animateColorAsState(
        targetValue = if (isCurrentUser) {
            Color(0xFF667EEA)
        } else {
            Color(0xFF1E2746)
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bubble_color"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!isCurrentUser) {
            Text(
                text = message.senderName.take(10),
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isCurrentUser) 20.dp else 4.dp,
                        bottomEnd = if (isCurrentUser) 4.dp else 20.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isCurrentUser) 20.dp else 4.dp,
                        bottomEnd = if (isCurrentUser) 4.dp else 20.dp
                    )
                )
                .background(bubbleColor)
                .padding(horizontal = 12.dp).padding(top = 8.dp, bottom = 2.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    fontSize = 15.sp,
                    color = Color.White,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatTimestamp(message.timestamp),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
