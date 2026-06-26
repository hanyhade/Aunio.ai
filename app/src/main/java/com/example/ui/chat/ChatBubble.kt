package com.example.ui.chat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ChatMessageEntity
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandGreenPrimary
import com.example.ui.theme.BrandTextPrimary
import com.example.ui.theme.BrandTextSecondary

@Composable
fun BoldableText(
    text: String,
    color: Color = BrandTextPrimary,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    lineHeight: TextUnit = 21.sp,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(text) {
        val builder = AnnotatedString.Builder()
        val parts = text.split("**")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                builder.withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = Color.White)) {
                    builder.append(part)
                }
            } else {
                builder.append(part)
            }
        }
        builder.toAnnotatedString()
    }
    Text(
        text = annotatedString,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        lineHeight = lineHeight,
        modifier = modifier
    )
}

@Composable
fun FormattedAiResponse(text: String, modifier: Modifier = Modifier) {
    val lines = text.split("\n")
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        lines.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.isEmpty() -> {
                    Spacer(modifier = Modifier.height(2.dp))
                }
                trimmed.startsWith("###") -> {
                    val headingText = trimmed.removePrefix("###").trim()
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(14.dp)
                                .background(BrandGreenPrimary, RoundedCornerShape(1.5.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        BoldableText(
                            text = headingText,
                            color = BrandGreenPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
                trimmed.startsWith("##") -> {
                    val headingText = trimmed.removePrefix("##").trim()
                    Spacer(modifier = Modifier.height(6.dp))
                    BoldableText(
                        text = headingText,
                        color = BrandGreenPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                trimmed.startsWith("#") -> {
                    val headingText = trimmed.removePrefix("#").trim()
                    Spacer(modifier = Modifier.height(8.dp))
                    BoldableText(
                        text = headingText,
                        color = BrandGreenPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("• ") -> {
                    val bulletText = trimmed.substring(2).trim()
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Text(
                            text = "•",
                            color = BrandGreenPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        BoldableText(
                            text = bulletText,
                            color = BrandTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                else -> {
                    BoldableText(
                        text = trimmed,
                        color = BrandTextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessageEntity) {
    val isAi = message.sender == "ai"
    val alignment = if (isAi) Alignment.Start else Alignment.End

    val bubbleBg = if (isAi) BrandCard else BrandGreenPrimary
    val borderStroke = if (isAi) {
        BorderStroke(0.5.dp, BrandGreenPrimary.copy(alpha = 0.3f))
    } else {
        null
    }

    var isExpanded by remember { mutableStateOf(false) }
    // Long responses are defined as anything exceeding 260 characters
    val isLongResponse = isAi && message.text.length > 260

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bubbleBg,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isAi) 4.dp else 16.dp,
                bottomEnd = if (isAi) 16.dp else 4.dp
            ),
            border = borderStroke,
            tonalElevation = if (isAi) 0.dp else 2.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                if (isAi) {
                    val textToShow = if (isLongResponse && !isExpanded) {
                        message.text.take(180) + "..."
                    } else {
                        message.text
                    }

                    FormattedAiResponse(text = textToShow)

                    if (isLongResponse) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isExpanded) "Show Less ↑" else "Read More ↓",
                            color = BrandGreenPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { isExpanded = !isExpanded }
                                .padding(vertical = 4.dp)
                        )
                    }
                } else {
                    BoldableText(
                        text = message.text,
                        color = Color.Black, // Dark contrast on Primary Brand Green bubble
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = if (isAi) "AUNIO.AI" else "YOU",
            color = BrandTextSecondary.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun AiTypingIndicator(isArabic: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = BrandCard,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(0.5.dp, BrandGreenPrimary.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = if (isArabic) "جاري التحليل" else "Thinking",
                    color = BrandGreenPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                val infiniteTransition = rememberInfiniteTransition(label = "dots")
                val d1 by infiniteTransition.animateFloat(
                    initialValue = 0.2f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 0), repeatMode = RepeatMode.Reverse), label = "d1"
                )
                val d2 by infiniteTransition.animateFloat(
                    initialValue = 0.2f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 150), repeatMode = RepeatMode.Reverse), label = "d2"
                )
                val d3 by infiniteTransition.animateFloat(
                    initialValue = 0.2f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 300), repeatMode = RepeatMode.Reverse), label = "d3"
                )
                Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(BrandGreenPrimary.copy(alpha = d1)))
                Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(BrandGreenPrimary.copy(alpha = d2)))
                Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(BrandGreenPrimary.copy(alpha = d3)))
            }
        }
    }
}
