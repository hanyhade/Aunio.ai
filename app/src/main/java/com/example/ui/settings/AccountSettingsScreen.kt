package com.example.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AccountSettingsScreen(
    isArabic: Boolean,
    memoryCount: Int,
    projectCount: Int,
    goalsCompletedCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        Column {
            Text(
                text = if (isArabic) "الحساب والملف التعريفي" else "Account Profile",
                color = BrandTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "إحصائيات الاتصال والقدرات المعرفية الخاصة بك" else "Your integration telemetry and cognitive workspace statistics",
                color = BrandTextSecondary,
                fontSize = 12.sp
            )
        }

        // Profile Avatar Card
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(BrandGreenPrimary.copy(alpha = 0.15f))
                        .border(BorderStroke(1.dp, BrandGreenPrimary), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        tint = BrandGreenPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "AUNIO Partner",
                        color = BrandTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "UID: AUNIO-992-KXZ",
                        color = BrandTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(BrandGreenPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AI PARTNER ACTIVE",
                            color = BrandGreenPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Workspace Telemetry Grid
        Text(
            text = if (isArabic) "📊 الإحصائيات المعرفية" else "📊 Cognitive Metrics",
            color = BrandTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Memory Vault Telemetry
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                border = BorderStroke(0.5.dp, BrandBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.Memory, contentDescription = "Memory", tint = BrandGreenPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$memoryCount", color = BrandTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic) "ذكرايات محفوظة" else "Active Memories",
                        color = BrandTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Strategic Projects
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                border = BorderStroke(0.5.dp, BrandBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.Flag, contentDescription = "Projects", tint = BrandGreenPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$projectCount", color = BrandTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic) "مشاريع نشطة" else "Active Plans",
                        color = BrandTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Accomplished Goals
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                border = BorderStroke(0.5.dp, BrandBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.Stars, contentDescription = "Goals", tint = BrandGreenPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$goalsCompletedCount", color = BrandTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic) "أهداف منجزة" else "Accomplished Goals",
                        color = BrandTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Security Notice
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "🔒 حماية الخصوصية المطلقة" else "🔒 Zero-Knowledge Privacy Architecture",
                    color = BrandTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isArabic)
                        "يتم حفظ وتشفير ملفك التعريفي وبياناتك المعرفية بالكامل محلياً على جهازك، دون أي تتبع خارجي أو معالجة سحابية لطرف ثالث."
                    else
                        "Your telemetry remains strictly local and client-encrypted under Room secure schemas, preventing leak channels entirely.",
                    color = BrandTextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}
