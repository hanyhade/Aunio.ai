package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.FrostedBg
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.LavenderTertiary

@Composable
fun AunioBackgroundOrbs(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FrostedBg)
    ) {
        // Glowing Frosted Violet Orb (Top Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-50).dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(LavenderPrimary.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )
        // Glowing Frosted Purple Orb (Bottom Left)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 50.dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(LavenderTertiary.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )
        content()
    }
}
