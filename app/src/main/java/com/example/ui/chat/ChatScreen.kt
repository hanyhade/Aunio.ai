package com.example.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ChatMessageEntity
import com.example.ui.AunioViewModel.ProjectSuggestion
import com.example.ui.AunioViewModel.GoalSuggestion
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandGreenPrimary
import com.example.ui.theme.BrandGreenSecondary
import com.example.ui.theme.BrandSecondaryBackground
import com.example.ui.theme.BrandTextPrimary
import com.example.ui.theme.BrandTextSecondary

import com.example.ui.AunioViewModel.ActionSuggestion
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Star

@Composable
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    chatInput: String,
    isGenerating: Boolean,
    isArabic: Boolean,
    suggestedProject: ProjectSuggestion?,
    suggestedGoal: GoalSuggestion?,
    suggestedActions: List<ActionSuggestion>,
    onConfirmProject: (ProjectSuggestion) -> Unit,
    onDismissProject: () -> Unit,
    onConfirmGoal: (GoalSuggestion) -> Unit,
    onDismissGoal: () -> Unit,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    // Smooth scrolling only on new message insertions
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        // Chat Bubble Logs
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (messages.size >= 30) {
                item(key = "load_older_header") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(
                            onClick = onLoadMore,
                            colors = ButtonDefaults.textButtonColors(contentColor = BrandGreenPrimary)
                        ) {
                            Text(
                                text = if (isArabic) "تحميل الرسائل السابقة 💬" else "Load Older Messages 💬",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                ChatBubble(message = msg)
            }
            if (isGenerating) {
                item(key = "typing_indicator") {
                    AiTypingIndicator(isArabic = isArabic)
                }
            }
        }

        // Suggested Project Action Card
        if (suggestedProject != null) {
            Surface(
                color = BrandCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(0.5.dp, BrandGreenPrimary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isArabic) "💡 اقتراح مشروع جديد" else "💡 Suggested Project",
                        color = BrandGreenPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = suggestedProject.title,
                        color = BrandTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (suggestedProject.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = suggestedProject.description,
                            color = BrandTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) "المدة المقترحة: ${suggestedProject.durationDays} يوم" else "Suggested Duration: ${suggestedProject.durationDays} days",
                        color = BrandTextSecondary.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismissProject,
                            colors = ButtonDefaults.textButtonColors(contentColor = BrandTextSecondary)
                        ) {
                            Text(text = if (isArabic) "تجاهل" else "Dismiss", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onConfirmProject(suggestedProject) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreenPrimary,
                                contentColor = BrandBackground
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = if (isArabic) "تأكيد وإنشاء" else "Confirm & Create", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Suggested Goal Action Card
        if (suggestedGoal != null) {
            Surface(
                color = BrandCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(0.5.dp, BrandGreenPrimary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isArabic) "🎯 اقتراح هدف فرعي جديد" else "🎯 Suggested Goal Milestone",
                        color = BrandGreenPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = suggestedGoal.title,
                        color = BrandTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) "الجدول الزمني: في خلال ${suggestedGoal.targetDays} يوم" else "Timeline: within ${suggestedGoal.targetDays} days",
                        color = BrandTextSecondary.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismissGoal,
                            colors = ButtonDefaults.textButtonColors(contentColor = BrandTextSecondary)
                        ) {
                            Text(text = if (isArabic) "تجاهل" else "Dismiss", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onConfirmGoal(suggestedGoal) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreenPrimary,
                                contentColor = BrandBackground
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = if (isArabic) "تأكيد وإضافة" else "Confirm & Add", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Quick Actions Row
        androidx.compose.animation.AnimatedVisibility(
            visible = suggestedActions.isNotEmpty(),
            enter = androidx.compose.animation.expandVertically(animationSpec = androidx.compose.animation.core.tween(400)),
            exit = androidx.compose.animation.shrinkVertically(animationSpec = androidx.compose.animation.core.tween(400))
        ) {
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(suggestedActions) { action ->
                    ActionSuggestionCard(
                        action = action,
                        onClick = {
                            onInputChange(action.prompt)
                        }
                    )
                }
            }
        }

        // Premium Floating Input Area
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                .imePadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandCard, RoundedCornerShape(28.dp))
                    .border(BorderStroke(0.5.dp, BrandBorder), RoundedCornerShape(28.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attach Action (+)
                IconButton(
                    onClick = { /* Premium Asset Attach Selector */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Attach",
                        tint = BrandGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Input Text Box (with clean, boundaryless borders)
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            text = if (isArabic) "اكتب لـ AUNIO.AI..." else "Message AUNIO.AI...",
                            color = BrandTextSecondary.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                        focusedTextColor = BrandTextPrimary,
                        unfocusedTextColor = BrandTextPrimary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Send Button in floating Brand Green container
                val isSendEnabled = chatInput.isNotBlank() && !isGenerating
                IconButton(
                    onClick = onSendClick,
                    enabled = isSendEnabled,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSendEnabled) BrandGreenPrimary else BrandCard)
                        .testTag("send_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Send",
                        tint = if (isSendEnabled) BrandBackground else BrandTextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionSuggestionCard(action: ActionSuggestion, onClick: () -> Unit) {
    val icon = when (action.iconType) {
        "Plan" -> Icons.Rounded.DateRange
        "Memory" -> Icons.Rounded.Lightbulb
        "Project" -> Icons.Rounded.Folder
        "Search" -> Icons.Rounded.Search
        "Summarize" -> Icons.Rounded.Edit
        "Study" -> Icons.Rounded.School
        "Image" -> Icons.Rounded.Image
        else -> Icons.Rounded.Star
    }

    Surface(
        color = BrandCard,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(0.5.dp, BrandBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = action.title,
                tint = BrandGreenPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = action.title,
                    color = BrandTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (action.subtitle != null) {
                    Text(
                        text = action.subtitle,
                        color = BrandTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
